/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.clientalert.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.database.DatabaseSpecificSQLGenerator;
import org.apache.fineract.portfolio.client.domain.ClientRepository;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertData;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertSearchRequest;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertRepository;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertSeverity;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertStatus;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertType;
import org.apache.fineract.portfolio.clientalert.exception.ClientAlertNotFoundException;
import org.apache.fineract.portfolio.clientalert.mapper.ClientAlertMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientAlertReadServiceImpl implements ClientAlertReadService {
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 200;

    private final ClientAlertRepository clientAlertRepository;
    private final ClientRepository clientRepository;
    private final ClientAlertMapper clientAlertMapper;

    private final JdbcTemplate jdbcTemplate;
    private final PaginationHelper paginationHelper;
    private final DatabaseSpecificSQLGenerator sqlGenerator;

    private final ClientAlertRowMapper clientAlertRowMapper = new ClientAlertRowMapper();

    @Override
    @Transactional(readOnly = true)
    public Page<ClientAlertData> retrieveAll(Long clientId, ClientAlertSearchRequest request) {
        ensureClientExists(clientId);

        int limit = normalizeLimit(request.getLimit());
        int offset = normalizeOffset(request.getOffset());

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ")
            .append(sqlGenerator.calcFoundRows())
            .append("""
                 ca.id,
                 ca.client_id AS clientId,
                 ca.external_id AS externalId,
                 ca.alert_type AS alertType,
                 ca.severity,
                 ca.message,
                 ca.reason,
                 ca.status,
                 ca.valid_from AS validFrom,
                 ca.valid_until AS validUntil,
                 ca.created_on_utc AS createdOn,
                 ca.created_by AS createdBy,
                 ca.last_modified_on_utc AS lastModifiedOn,
                 ca.last_modified_by AS lastModifiedBy,
                 ca.resolved_on AS resolvedOn,
                 ca.resolved_by AS resolvedBy,
                 ca.resolution_note AS resolutionNote,
                 ca.version
            FROM m_client_alert ca
            WHERE ca.client_id = ?
                    """);
        
        List<Object> params = new ArrayList<>();
        params.add(clientId);

        appendStatusFilter(sql, params, request);
        appendSeverityFilter(sql, params, request);
        appendValidOnFilter(sql, params, request);
        appendExpiredFilter(sql, params, request);
        appendCriticalUnresolvedFilter(sql, params, request);
        appendCreatedRangeFilter(sql, params, request);
        appendCreatedByFilter(sql, params, request);

        sql.append("""
                ORDER BY
                    ca.created_on_utc DESC,
                    ca.id DESC
                """);

        sql.append(' ')
            .append(sqlGenerator.limit(limit, offset));

        return paginationHelper.fetchPage(
            jdbcTemplate,
            sql.toString(),
            params.toArray(),
            clientAlertRowMapper);
    }

    private void appendStatusFilter(
            StringBuilder sql,
            List<Object> arguments,
            ClientAlertSearchRequest request) {

        if (request.getStatus() == null) {
            return;
        }

        sql.append(" AND ca.status = ?");
        arguments.add(request.getStatus().name());
    }

    private void appendSeverityFilter(
            StringBuilder sql,
            List<Object> arguments,
            ClientAlertSearchRequest request) {

        if (request.getSeverity() == null) {
            return;
        }

        sql.append(" AND ca.severity = ?");
        arguments.add(request.getSeverity().name());
    }

     private void appendValidOnFilter(
            StringBuilder sql,
            List<Object> arguments,
            ClientAlertSearchRequest request) {

        LocalDate validOn = request.getValidOn();

        if (validOn == null) {
            return;
        }

        sql.append("""
             AND (
                 ca.valid_from IS NULL
                 OR ca.valid_from <= ?
             )
             AND (
                 ca.valid_until IS NULL
                 OR ca.valid_until >= ?
             )
            """);

        arguments.add(validOn);
        arguments.add(validOn);
    }

    private void appendExpiredFilter(
            StringBuilder sql,
            List<Object> arguments,
            ClientAlertSearchRequest request) {

        if (!Boolean.TRUE.equals(request.getExpired())) {
            return;
        }

        sql.append("""
             AND ca.valid_until IS NOT NULL
             AND ca.valid_until < ?
            """);

        arguments.add(DateUtils.getBusinessLocalDate());
    }

    private void appendCriticalUnresolvedFilter(
            StringBuilder sql,
            List<Object> arguments,
            ClientAlertSearchRequest request) {

        if (!Boolean.TRUE.equals(
                request.getCriticalUnresolved())) {
            return;
        }

        sql.append("""
             AND ca.status = ?
             AND ca.severity = ?
            """);

        arguments.add(ClientAlertStatus.ACTIVE.name());
        arguments.add(ClientAlertSeverity.CRITICAL.name());
    }

    private void appendCreatedRangeFilter(
            StringBuilder sql,
            List<Object> arguments,
            ClientAlertSearchRequest request) {

        if (request.getCreatedFrom() != null) {
            sql.append(" AND ca.created_on_utc >= ?");

            arguments.add(
                toUtcTimestamp(
                    request.getCreatedFrom()
                )
            );
        }

        if (request.getCreatedTo() != null) {
            /*
             * Exclusive upper bound:
             *
             * createdOn < start of next day
             *
             * Cách này không cần DATE(created_on_utc),
             * vì function trên column sẽ làm index khó dùng.
             */
            sql.append(" AND ca.created_on_utc < ?");

            arguments.add(
                toUtcTimestamp(
                    request.getCreatedTo().plusDays(1)
                )
            );
        }
    }

    private void appendCreatedByFilter(
            StringBuilder sql,
            List<Object> arguments,
            ClientAlertSearchRequest request) {

        if (request.getCreatedBy() == null) {
            return;
        }

        sql.append(" AND ca.created_by = ?");
        arguments.add(request.getCreatedBy());
    }

    private Timestamp toUtcTimestamp(LocalDate date) {
        return Timestamp.from(
            date.atStartOfDay(
                DateUtils.getDateTimeZoneOfTenant()
            ).toInstant()
        );
    }

    private int normalizeLimit(Integer requestedLimit) {
        if (requestedLimit == null) {
            return DEFAULT_LIMIT;
        }

        if (requestedLimit < 1
                || requestedLimit > MAX_LIMIT) {

            throw new IllegalArgumentException(
                "limit must be between 1 and "
                    + MAX_LIMIT
            );
        }

        return requestedLimit;
    }

    private int normalizeOffset(Integer requestedOffset) {
        if (requestedOffset == null) {
            return 0;
        }

        if (requestedOffset < 0) {
            throw new IllegalArgumentException(
                "offset must not be negative"
            );
        }

        return requestedOffset;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientAlertData retrieveOne(Long clientId, Long alertId) {
        ensureClientExists(clientId);

        return clientAlertRepository
                .findByClientIdAndAlertId(clientId, alertId)
                .map(clientAlertMapper::map)
                .orElseThrow(() -> new ClientAlertNotFoundException(clientId, alertId));
    }

    private void ensureClientExists(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException(clientId);
        }
    }

   private static final class ClientAlertRowMapper
            implements RowMapper<ClientAlertData> {

        @Override
        public ClientAlertData mapRow(
                ResultSet rs,
                int rowNum) throws SQLException {

            return ClientAlertData.builder()
                .id(JdbcSupport.getLong(rs, "id"))
                .clientId(
                    JdbcSupport.getLong(rs, "clientId")
                )
                .externalId(rs.getString("externalId"))
                .alertType(
                    ClientAlertType.valueOf(
                        rs.getString("alertType")
                    )
                )
                .severity(
                    ClientAlertSeverity.valueOf(
                        rs.getString("severity")
                    )
                )
                .message(rs.getString("message"))
                .reason(rs.getString("reason"))
                .status(
                    ClientAlertStatus.valueOf(
                        rs.getString("status")
                    )
                )
                .validFrom(
                    JdbcSupport.getLocalDate(
                        rs,
                        "validFrom"
                    )
                )
                .validUntil(
                    JdbcSupport.getLocalDate(
                        rs,
                        "validUntil"
                    )
                )
                .createdOn(
                    JdbcSupport.getOffsetDateTime(
                        rs,
                        "createdOn"
                    )
                )
                .createdBy(
                    JdbcSupport.getLong(
                        rs,
                        "createdBy"
                    )
                )
                .lastModifiedOn(
                    JdbcSupport.getOffsetDateTime(
                        rs,
                        "lastModifiedOn"
                    )
                )
                .lastModifiedBy(
                    JdbcSupport.getLong(
                        rs,
                        "lastModifiedBy"
                    )
                )
                .resolvedOn(
                    JdbcSupport.getLocalDate(
                        rs,
                        "resolvedOn"
                    )
                )
                .resolvedBy(
                    JdbcSupport.getLong(
                        rs,
                        "resolvedBy"
                    )
                )
                .resolutionNote(
                    rs.getString("resolutionNote")
                )
                .version(
                    JdbcSupport.getLong(rs, "version")
                )
                .build();
        }
    }
}
