package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.domain.*
import java.time.LocalDate

class TestUtils {

    fun createSubject(
        id: Int? = 23545971,
        name: String = "Test Company",
        email : String? = "email@test.com",
        CIN: String = "123456789",
    ): SubjectDomain {
        return SubjectDomain(
            id = id,
            name = name,
            registration_no = CIN,
            email = email,
        )
    }


    fun createMockkInvoice(
        id: Int? = null,
        customId: CustomIdDomain = CustomIdDomain("2024-11-001"),
        document_type: String? = "invoice",
        subjectId: Int = 23377698,
        relatedId: Int? = null,
        status: String? = "open",
        due: Int = 14,
        note: String? = "Thank you for your business.",
        issuedOn: String = LocalDate.now().toString(),
        taxableFulfillmentDue: String = LocalDate.now().toString(),
        lines: List<LinesDomain> =  createInvoiceLines(),
        currency: String = "EUR",
    ) : InvoiceDomain {
        return InvoiceDomain(
            id = id,
            customId = customId,
            documentType = document_type,
            subjectId = subjectId,
            relatedId = relatedId,
            status = status,
            due = due,
            note = note,
            issuedOn = issuedOn,
            taxableFulfillmentDue = taxableFulfillmentDue,
            lines = lines,
            currency = currency,
            totalWOVat = null,
            totalWithVat = null
        )

    }

    fun createCreditMockkInvoice(quantity: Double): List<InvoiceDomain> {
        return listOf( createMockkInvoice(
            id = 123456,
            lines = createCreditInvoiceLines(quantity = quantity),
            subjectId = 23545971,
            issuedOn = LocalDate.now().minusMonths(2).toString(),
        )
        )

    }

    fun createValidatedProformaMockkInvoice(quantity: Double): List<InvoiceDomain> {
            return listOf( createMockkInvoice(
                id = 123457,
                document_type = "proforma",
                relatedId = null,
                lines = createCreditInvoiceLines(
                    name = "Validated Saver ${quantity} CVs / applications",
                    quantity = quantity),

                subjectId = 23545971
            ))

        }

    fun createCreditReached100ProformaMockkInvoice(quantity: Double, exceeded : Double, issuedOn: String?): List<InvoiceDomain> {
        return listOf(createMockkInvoice(
            id = 123457,
            document_type = "proforma",
            relatedId = null,
            issuedOn = issuedOn?:LocalDate.now().toString(),
            lines = listOf( LinesDomain(
                name = "100% of credits applied from total ${quantity} credits",
                quantity = quantity,
                unitName = "credit",
                unitPrice = 0.0,
                vatRate = 0.0,
                totalWOVat = null,
                totalWithVat = null
            ),
                LinesDomain(
                    name = "CV applications exceeded the credit",
                    quantity = exceeded,
                    unitName = "CV applications",
                    unitPrice = 0.0,
                    vatRate = 0.0,
                    totalWOVat = null,
                    totalWithVat = null
                )),
            subjectId = 23545971
        ))

    }

    fun createCreditSubject(): List<SubjectDomain> {
        return listOf(
        createSubject()
        )
    }

    fun createCreditSubjectWithBuffer(): List<SubjectDomain> {
        return listOf(
            createSubject(id = 123456789),
        )
    }

    fun createInvoiceLines(
        name: String = "Hard work for the customer",
        quantity: Double = 1.0,
        unitName: String = "hour",
        unitPrice: Double = 100.0,
        vatRate: Double = 21.0
    ): List<LinesDomain> {
        return listOf<LinesDomain>( LinesDomain(
            name = name,
            quantity = quantity,
            unitName = unitName,
            unitPrice = unitPrice,
            vatRate = vatRate,
            totalWOVat = null,
            totalWithVat = null
        )
        )
    }

    fun createCreditInvoiceLines(
        name: String = "Saver 500 CVs / applications",
        quantity: Double = 500.0,
        unitName: String = "CV upload",
        unitPrice: Double = 7.0,
        vatRate: Double = 21.0
    ): List<LinesDomain> {
        return listOf( LinesDomain(
            name = name,
            quantity = quantity,
            unitName = unitName,
            unitPrice = unitPrice,
            vatRate = vatRate,
            totalWOVat = null,
            totalWithVat = null
        ))
    }

    fun dbOutput(): List<DjDbOutput> {
        return listOf(
            DjDbOutput(
                companyRegistrationNumber = "123456789",
                companyContactEmail = "some@email.com",
                companyLawName = "DreamJobs s.r.o.",
                cvQuantityMonth = 10,
                datesOfCvUpload = List(10) { LocalDate.now() }
            ),
            DjDbOutput(
                companyRegistrationNumber = "234567890",
                companyContactEmail = "some@email.com",
                companyLawName = "DreamJobs2 s.r.o.",
                cvQuantityMonth = 10,
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
