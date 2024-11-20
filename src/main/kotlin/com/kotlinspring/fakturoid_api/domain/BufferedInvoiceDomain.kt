package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class BufferedInvoiceDomain (
    finClaim: List<ClaimDataDomain>,
    subjects: List<SubjectDomain>
)
{


    private val directInvoiceBuffer = finClaim.filter { it.cvUploadedNumberMonth >= 10 }
    private val directInvoice = directInvoiceBuffer.map { claim ->
        InvoiceDomain(
            id = null,
            customId = null,
            documentType = "invoice",
            subjectId = requireNotNull(subjects.find { it.CIN == claim.tenant.companyRegistrationNumber }?.id) { "Subject ${claim.tenant.companyRegistrationNumber} could not be found" },
            status = "open",
            due = 14,
            issuedOn = LocalDate.now().toString(),
            taxableFulfillmentDue = LocalDate.now().toString(),
            lines = LinesDomain.createLinesFromFinClaim(claim),
            currency = "EUR",
            totalWOVat = null,
            totalWithVat = null
        )
    }

    private val claimBuffer = finClaim.filter { it.cvUploadedNumberMonth < 10 }
    private val cumulativeInvoices = claimBuffer.mapNotNull { claim ->
        if (CumulativeCvsDomain(claim.datesOfCvUploads).finalUploads > 0) {

            val lines = CumulativeCvsDomain(claim.datesOfCvUploads).lastAdjusted.map {
                LinesDomain(
                    name = "Buffered CV uploads ${it.key}",
                    quantity = it.value.toDouble(),
                    unitName = "CV upload",
                    unitPrice = 7.0,
                    vatRate = 21.0,
                    totalWOVat = null,
                    totalWithVat = null
                )
            }.toList()

            val subjectId: Int =
                requireNotNull(subjects.find { it.CIN == claim.tenant.companyRegistrationNumber }?.id) { "Subject ${claim.tenant.companyRegistrationNumber} could not be found" }

            InvoiceDomain(
                id = null,
                customId = null,
                documentType = "invoice",
                subjectId = subjectId,
                status = "open",
                due = 14,
                issuedOn = LocalDate.now().toString(),
                taxableFulfillmentDue = LocalDate.now().toString(),
                lines = lines,
                currency = "EUR",
                totalWOVat = null,
                totalWithVat = null
            )
        } else {
            null
        }
    }

    val bufferedInvoice: List<InvoiceDomain> =
        buildList {
            addAll(cumulativeInvoices)
            addAll(directInvoice)
        }


}


