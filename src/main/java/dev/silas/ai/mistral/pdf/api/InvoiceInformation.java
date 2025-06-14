package dev.silas.ai.mistral.pdf.api;

public record InvoiceInformation(
        String creditor,
        String debitor,
        Float totalAmount,
        String currency,
        String invoiceDate
) {
}