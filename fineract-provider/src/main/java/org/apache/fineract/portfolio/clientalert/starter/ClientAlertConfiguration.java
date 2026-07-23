package org.apache.fineract.portfolio.clientalert.starter;

import org.apache.fineract.infrastructure.core.service.PaginationHelper;
import org.apache.fineract.infrastructure.core.service.database.DatabaseSpecificSQLGenerator;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.ClientRepository;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertRepository;
import org.apache.fineract.portfolio.clientalert.mapper.ClientAlertMapper;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertReadService;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertReadServiceImpl;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertWriteService;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertWriteServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ClientAlertConfiguration {

    @Bean
    @ConditionalOnMissingBean(ClientAlertWriteService.class)
    ClientAlertWriteService clientAlertWriteService(
            ClientAlertRepository alertRepository,
            ClientRepository clientRepository,
            PlatformSecurityContext securityContext) {

        return new ClientAlertWriteServiceImpl(
            alertRepository,
            clientRepository,
            securityContext
        );
    }

    @Bean
    @ConditionalOnMissingBean(ClientAlertReadService.class)
    ClientAlertReadService clientAlertReadService(
            ClientAlertRepository alertRepository,
            ClientRepository clientRepository,
            ClientAlertMapper mapper,
            JdbcTemplate jdbcTemplate,
            PaginationHelper paginationHelper,
            DatabaseSpecificSQLGenerator sqlGenerator) {

        return new ClientAlertReadServiceImpl(
            alertRepository,
            clientRepository,
            mapper,
            jdbcTemplate,
            paginationHelper,
            sqlGenerator
        );
    }
}