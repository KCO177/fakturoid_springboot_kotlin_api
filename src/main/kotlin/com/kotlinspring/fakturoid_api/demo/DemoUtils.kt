package com.kotlinspring.fakturoid_api.demo

import com.kotlinspring.fakturoid_api.domain.CustomIdDomain
import com.kotlinspring.fakturoid_api.domain.DjDbOutput
import com.kotlinspring.fakturoid_api.domain.InvoiceDomain
import com.kotlinspring.fakturoid_api.domain.LinesDomain
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DemoUtils {

    fun createDemoInvoice(
        customId: CustomIdDomain= CustomIdDomain("2024-11-001"),
        document_type: String? = "invoice",
        subjectId: Int = 23377698,
        status : String? = "open",
        due: Int = 14,
        note : String? = "Thank you for your business.",
        issuedOn: String = LocalDate.now().toString(),
        taxableFulfillmentDue: String = LocalDate.now().toString(),
        lines: LinesDomain =  createInvoiceLines(),
        currency: String = "EUR",
        ) : InvoiceDomain {
        return InvoiceDomain(
            id = null,
            customId = customId,
            documentType = document_type,
            subjectId = subjectId,
            status = status,
            due = due,
            note = note,
            issuedOn = issuedOn,
            taxableFulfillmentDue = taxableFulfillmentDue,
            lines = listOf(lines),
            currency = currency,
            totalWOVat = null,
            totalWithVat = null
        )

    }

    fun createInvoiceLines(
        name: String = "Hard work for the customer",
        quantity: Double = 1.0,
        unitName: String = "hour",
        unitPrice: Double = 100.0,
        vatRate: Double = 21.0
    ): LinesDomain {
        return LinesDomain(
            name = name,
            quantity = quantity,
            unitName = unitName,
            unitPrice = unitPrice,
            vatRate = vatRate,
            totalWOVat = null,
            totalWithVat = null
        )
    }

    fun dbOutput(): List<DjDbOutput> {
        return listOf(
            DjDbOutput(
                companyRegistrationNumber = "123456789",
                companyContactEmail = "some@email.com",
                companyLawName = "Test tenant 01 s.r.o.",
                cvQuantityMonth = 10,
                datesOfCvUpload = List(15) { LocalDate.now() }
            ),
            DjDbOutput(
                companyRegistrationNumber = "234567890",
                companyContactEmail = "some@email.com",
                companyLawName = "Test tenant 02 s.r.o.",
                cvQuantityMonth = 10,
                datesOfCvUpload = listOf(
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                    LocalDate.now(),
                )
            )
        )
    }
}
