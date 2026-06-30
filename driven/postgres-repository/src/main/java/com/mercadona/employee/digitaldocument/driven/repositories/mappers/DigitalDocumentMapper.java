package com.mercadona.employee.digitaldocument.driven.repositories.mappers;

import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.driven.repositories.models.DigitalDocumentMO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface DigitalDocumentMapper {


    DigitalDocument fromModel(DigitalDocumentMO digitalDocumentMO);

    DigitalDocumentMO toModel(DigitalDocument digitalDocument);

    default Optional<DigitalDocument> fromOptionalModel(Optional<DigitalDocumentMO> digitalDocumentMO) {
        return digitalDocumentMO.map(this::fromModel);
    }

    default List<DigitalDocument> fromModels(List<DigitalDocumentMO> digitalDocumentMOs) {
        return digitalDocumentMOs.stream().map(this::fromModel).toList();
    }
}
