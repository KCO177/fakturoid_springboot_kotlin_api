package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.domain.*
import java.time.LocalDate

class TestUtils {

    fun createSubject(
        id: Int? = 23545971,
        name: String = "Test Company",
        full_name: String? = "Test Company Full Name",
        street: String? = "123 Test St",
        city: String? = "Test City",
        zip: String? = "12345",
        countryCode: String? = "CZ",
        CIN: String = "123456789",
        vat_no: String? = "123456789",
        variable_symbol: String? = "123456"
    ): SubjectDomain {
        return SubjectDomain(
            id = id,
            name = name,
            full_name = full_name,
            street = street,
            city = city,
            zip = zip,
            countryCode = countryCode,
            CIN = CIN,
            vat_no = vat_no,
            variable_symbol = variable_symbol
        )
    }


    fun createMockkInvoice(
        id: Int? = null,
        customId: CustomIdDomain = CustomIdDomain("2024-11-001"),
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
            id = id,
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

    fun createCreditDemoInvoice(quantity: Double): List<InvoiceDomain> {
        return listOf( createMockkInvoice(
            id = 123456,
            lines = createCreditInvoiceLines(quantity = quantity),
            subjectId = 23545971
        ))

    }

    fun createCreditSubject(): List<SubjectDomain> {
        return listOf(
        createSubject()
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
            vatRate = vatRate
        )
    }

    fun createCreditInvoiceLines(
        name: String = "Saver 500 CVs / applications",
        quantity: Double = 500.0,
        unitName: String = "CV upload",
        unitPrice: Double = 7.0,
        vatRate: Double = 0.0
    ): LinesDomain {
        return LinesDomain(
            name = name,
            quantity = quantity,
            unitName = unitName,
            unitPrice = unitPrice,
            vatRate = vatRate
        )
    }

    fun dbOutput(): List<DjDbOutput> {
        return listOf(
            DjDbOutput(
                companyRegistrationNumber = "123456789",
                companyContactEmail = "some@email.com",
                companyLawName = "DreamJobs s.r.o.",
                cvQuantityMonth = 10,
                cvQuantityYear = 10,
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
            ),
            DjDbOutput(
                companyRegistrationNumber = "234567890",
                companyContactEmail = "some@email.com",
                companyLawName = "DreamJobs2 s.r.o.",
                cvQuantityMonth = 1,
                cvQuantityYear = 9,
                datesOfCvUpload = List(10) { LocalDate.now() }

            )
        )
    }

    fun createClaimData(
        tenant: TenantDomain = createTenant(),
        cvUploadedNumberMonth: Int = 10,
        datesOfCvUploads: List<LocalDate> =
            listOf(LocalDate.now())
        ): ClaimDataDomain {
            return ClaimDataDomain(
                tenant = tenant,
                cvUploadedNumberMonth = cvUploadedNumberMonth,
                datesOfCvUploads = datesOfCvUploads
                )

        }

    fun createTenant(
        companyRegistrationNumber: String = "123456789",
        companyContactEmail: String = "contact@example.com",
        companyLawName: String = "Test Company"
    ): TenantDomain { return TenantDomain(
            companyRegistrationNumber = companyRegistrationNumber,
            companyContactEmail = companyContactEmail,
            companyLawName = companyLawName
    )
    }


}
