package com.mercadona.employee.digitaldocument.driving.controllers.mappers;

import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.driving.controllers.dto.DocumentStatusDTO;
import com.mercadona.employee.digitaldocument.driving.controllers.dto.DocumentStatusResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DigitalDocumentDTOMapper {

    @Mapping(target = "status", expression = "java(document.getStatus().name())")
    DocumentStatusDTO toStatusDTO(DigitalDocument document);

    default DocumentStatusResponseDTO toStatusResponse(DigitalDocument document) {
        return DocumentStatusResponseDTO.builder()
                .data(toStatusDTO(document))
                .build();
    }
}
