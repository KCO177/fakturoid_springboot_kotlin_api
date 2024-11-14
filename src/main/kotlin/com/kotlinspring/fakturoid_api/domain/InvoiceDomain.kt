package com.kotlinspring.fakturoid_api.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.kotlinspring.fakturoid_api.service.SubjectService
import java.time.LocalDate

open class InvoiceDomain(
    val id: Int? = null,
    val customId: CustomIdDomain?,
    @JsonProperty("document_type")
    val documentType: String? = "invoice",
    @JsonProperty("subject_id")
    val subjectId: Int,
    val status : String?,
    val due: Int? = 14,
    val note: String? = "Thank you for your business.",
    @JsonProperty("issued_on")
    val issuedOn: String? = LocalDate.now().toString(),
    @JsonProperty("taxable_fulfillment_due")
    val taxableFulfillmentDue: String? = LocalDate.now().toString(),
    val lines: List<LinesDomain>,
    val currency: String? = "EUR",
    val totalWOVat : Double? = lines.sumOf { it.totalWOVat },
    val totalWithVat : Double? = lines.sumOf { it.totalWithVat }
)
{
    companion object {

        fun getInvoices(
            claimData: List<ClaimDataDomain>,
            bearerToken: String,
            subjectService: SubjectService
        ): List<InvoiceDomain> {
            val invoices = mutableListOf<InvoiceDomain>()
            claimData.forEach {claimFin ->
                invoices.add(
                    InvoiceDomain(
                        id = null,
                        customId = null,
                        documentType = null,
                        subjectId = SubjectDomain.getSubjectId(claimFin.tenant, subjectService, bearerToken),
                        status = null,
                        due = null,
                        note = null,
                        issuedOn = null,
                        taxableFulfillmentDue = null,
                        lines = LinesDomain.createLines(claimFin),
                        currency = null,
                        totalWOVat = null,
                        totalWithVat = null,
                    )
                )
            }
            return invoices
        }
    }
}
