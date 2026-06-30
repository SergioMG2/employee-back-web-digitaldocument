package com.mercadona.employee.digitaldocument.driven.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.mercadona.employee.digitaldocument.application.ports.driven.PdfGeneratorPort;
import com.mercadona.employee.digitaldocument.domain.CertificationInfo;
import com.mercadona.employee.digitaldocument.domain.EmployeeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    @Override
    public byte[] generate(EmployeeInfo employeeInfo) {
        log.info("Generating PDF for employeeId={}", employeeInfo.getEmployeeId());

        try (var baos = new ByteArrayOutputStream()) {
            var pdfWriter = new PdfWriter(baos);
            var pdfDocument = new PdfDocument(pdfWriter);
            var document = new Document(pdfDocument);

            addTitle(document);
            addEmployeeSection(document, employeeInfo);
            addCertificationSection(document, employeeInfo.getCertification());

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF for employeeId=" + employeeInfo.getEmployeeId(), e);
        }
    }

    private void addTitle(Document document) {
        document.add(new Paragraph("AI Certification Digital Document")
                .setFontSize(20)
                .setBold()
                .setMarginBottom(20));
    }

    private void addEmployeeSection(Document document, EmployeeInfo info) {
        document.add(new Paragraph("Employee Information")
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginBottom(8));

        var table = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        addRow(table, "Full Name", info.getFullName());
        addRow(table, "Job Function", info.getJobFunction());
        addRow(table, "Department", info.getDepartment());
        addRow(table, "Email", info.getEmail());
        addRow(table, "Phone Extension", info.getPhoneExtension());
        addRow(table, "Location", info.getLocation());

        document.add(table);
    }

    private void addCertificationSection(Document document, CertificationInfo cert) {
        if (cert == null) {
            return;
        }

        document.add(new Paragraph("AI Certification Details")
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginBottom(8));

        var table = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        addRow(table, "Certification ID", cert.getCertificationId());
        addRow(table, "Status", cert.getStatus());
        addRow(table, "Valid", Boolean.TRUE.equals(cert.getIsValid()) ? "Yes" : "No");
        addRow(table, "Level", cert.getLevel());
        addRow(table, "Issued By", cert.getIssuedBy());
        addRow(table, "Issued Date", formatDate(cert.getIssuedDate()));
        addRow(table, "Start Date", formatDate(cert.getStartDate()));
        addRow(table, "Expiration Date", formatDate(cert.getExpirationDate()));
        addRow(table, "Approved Tools", formatList(cert.getApprovedTools()));
        addRow(table, "Description", cert.getDescription());

        document.add(table);
    }

    private void addRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(Optional.ofNullable(value).orElse("-"))));
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.toString() : "-";
    }

    private String formatList(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "-";
        }
        return String.join(", ", items);
    }
}
