package com.mercadona.employee.digitaldocument.driven.restclients.adapters;

import com.mercadona.employee.digitaldocument.application.exceptions.EmployeeNotFoundException;
import com.mercadona.employee.digitaldocument.application.ports.driven.EmployeeEnrichmentPort;
import com.mercadona.employee.digitaldocument.domain.EmployeeInfo;
import com.mercadona.employee.digitaldocument.driven.restclients.dto.AiCertificationResponseDTO;
import com.mercadona.employee.digitaldocument.driven.restclients.mappers.EmployeeEnrichmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
@Slf4j
@Service
public class EmployeeEnrichmentClientAdapter implements EmployeeEnrichmentPort {

    private static final String ENRICHMENT_PATH =
            "/managed-groups/{managedGroupId}/employees/{employeeId}/ai-certification";

    private final RestTemplate restTemplate;
    private final EmployeeEnrichmentMapper mapper;
    private final String basePath;

    public EmployeeEnrichmentClientAdapter(
            RestTemplate restTemplate,
            EmployeeEnrichmentMapper mapper,
            @Value("${clients.cardgenerator.base-path}") String basePath) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.basePath = basePath;
    }

    @Override
    public EmployeeInfo enrich(String employeeId, String managedGroupId) {
        var url = basePath + ENRICHMENT_PATH;

        log.info("Enriching employee data: employeeId={}, managedGroupId={}", employeeId, managedGroupId);

        try {
            var response = restTemplate.getForObject(url, AiCertificationResponseDTO.class,
                    managedGroupId, employeeId);

            if (response == null || response.getData() == null) {
                throw new RuntimeException(
                        "Empty response from enrichment API for employeeId=" + employeeId);
            }

            return mapper.toEmployeeInfo(response.getData());

        } catch (HttpClientErrorException e) {
            log.error("Client error enriching employee employeeId={}, managedGroupId={}: status={}, body={}",
                    employeeId, managedGroupId, e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 404 || e.getStatusCode().value() == 400) {
                throw new EmployeeNotFoundException(employeeId, managedGroupId);
            }
            throw new RuntimeException("Enrichment API client error for employeeId=" + employeeId, e);
        } catch (HttpServerErrorException e) {
            log.error("Server error enriching employee employeeId={}, managedGroupId={}: status={}",
                    employeeId, managedGroupId, e.getStatusCode());
            throw new RuntimeException("Enrichment API server error for employeeId=" + employeeId, e);
        }
    }
}
