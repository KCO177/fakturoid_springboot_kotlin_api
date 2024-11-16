package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class BufferedInvoiceDomain (
    finClaim: List<ClaimDataDomain>,
    subjects: List<SubjectDomain>
)
{
    val bufferedInvoice = finClaim.mapNotNull { claim ->
            if (CumulativeCvsDomain(claim.datesOfCvUploads).finalUploads > 0) {

                val lines = CumulativeCvsDomain(claim.datesOfCvUploads).lastAdjusted.map {
                    LinesDomain(
                        name = "Buffered CV uploads ${it.key}",
                        quantity = it.value.toDouble(),
                        unitName = "CV upload",
                        unitPrice = 7.0,
                        vatRate = 21.0
                    )
                }.toList()

                val subjectId: Int =
                    requireNotNull(subjects.find { it.CIN == claim.tenant.companyRegistrationNumber }?.id) { "Subject ${claim.tenant.companyRegistrationNumber} could not be found" }

                InvoiceDomain(
                    id = null,
                    customId = null,
                    documentType = "proforma",
                    subjectId = subjectId,
                    status = "open",
                    due = 14,
                    issuedOn = LocalDate.now().toString(),
                    taxableFulfillmentDue = LocalDate.now().toString(),
                    lines = lines,
                    currency = "EUR"
                )
            } else {
                null
            }
        }
}
