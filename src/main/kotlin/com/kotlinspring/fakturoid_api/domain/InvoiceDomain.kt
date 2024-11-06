package com.kotlinspring.fakturoid_api.domain

import com.kotlinspring.fakturoid_api.demo.LinesDomain
import com.kotlinspring.fakturoid_api.service.SubjectService
import java.time.LocalDate

open class InvoiceDomain(
    val id: Int? = null,
    val customId: CustomIdDomain,
    val subject_id: Int,
    val due: Int? = 14,
    val issued_on: String? = LocalDate.now().toString(),
    val taxable_fulfillment_due: String? = LocalDate.now().toString(),
    val lines: List<LinesDomain>,
    val currency: String? = "EUR",
    val totalWOVat : Double? = lines.sumOf { it.totalWOVat },
    val totalWithVat : Double? = lines.sumOf { it.totalWithVat }
)
{
    companion object {

        fun getInvoices(
            invoiceData: List<InvoiceDataDomain>,
            newCustomIdDomain: CustomIdDomain,
            bearerToken: String,
            subjectService: SubjectService
        ): List<InvoiceDomain> {
            val invoices = mutableListOf<InvoiceDomain>()
            invoiceData.forEachIndexed { index, invoice ->
                val customIdNumber = index + 1
                val customId = CustomIdDomain("${newCustomIdDomain.year}-${newCustomIdDomain.month}-$customIdNumber")
                invoices.add(
                    InvoiceDomain(
                        id = null,
                        customId = customId,
                        subject_id = SubjectDomain.getSubjectId(invoice.tenant, subjectService, bearerToken),
                        due = null,
                        issued_on = null,
                        taxable_fulfillment_due = null,
                        lines = LinesDomain.createLines(invoice),
                        currency = null,
                        totalWOVat = null,
                        totalWithVat = null
                    )
                )
            }
            return invoices
        }

    }
}
