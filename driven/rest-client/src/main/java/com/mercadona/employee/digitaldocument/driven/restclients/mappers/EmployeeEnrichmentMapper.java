package com.mercadona.employee.digitaldocument.driven.restclients.mappers;

import com.mercadona.employee.digitaldocument.domain.CertificationInfo;
import com.mercadona.employee.digitaldocument.domain.EmployeeInfo;
import com.mercadona.employee.digitaldocument.driven.restclients.dto.CertificationDataDTO;
import com.mercadona.employee.digitaldocument.driven.restclients.dto.EmployeeDataDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmployeeEnrichmentMapper {

    EmployeeInfo toEmployeeInfo(EmployeeDataDTO dto);

    CertificationInfo toCertificationInfo(CertificationDataDTO dto);
}
