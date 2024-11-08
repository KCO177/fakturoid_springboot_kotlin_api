package com.kotlinspring.fakturoid_api.service

import com.kotlinspring.fakturoid_api.controller.AuthorizationController
import com.kotlinspring.fakturoid_api.controller.InvoiceController
import com.kotlinspring.fakturoid_api.demo.DemoUtils
import com.kotlinspring.fakturoid_api.demo.LinesDomain
import com.kotlinspring.fakturoid_api.domain.*
import java.time.LocalDate

class InvoiceService(
    private val subjectService: SubjectService,
    private val invoiceController: InvoiceController,
    private val authorizationController: AuthorizationController,
    private val demoUtils: DemoUtils,
    private val creditInvoiceService: CreditInvoiceService
) {
    private val bearerToken = requireNotNull(
        authorizationController.getBearerToken(
            AuthorizationController().refreshToken,
            AuthorizationController().authorizationClient
        )
    ) { "Bearer token for fakturoid could not be created" }

    //prepare data from db
    val finClaimDataRaw: List<ClaimDataDomain> = ClaimDataDomain.getInvoiceData(demoUtils.dbOutput())
    val finClaim = finClaimDataRaw.filter { it.cvUploadedNumberMonth > 0 }

    //filter subject by tenant
    val tenantRegNumbers: List<TenantDomain> = finClaim.map { it.tenant }
    val subjects: List<SubjectDomain> = tenantRegNumbers.map { tenant ->
        subjectService.findOrCreateTenant(
            bearerToken,
            SubjectDomain.mapTenantToSubjectDomain(tenant)
        )
    }

    //get all invoices from last year
    val invoicesPayload =
        requireNotNull(invoiceController.getInvoices(bearerToken)) { "Invoices could not be fetched from fakturoid" }


    //filter if credit exists
    val creditInvoices: List<InvoiceDomain> = invoicesPayload.filter { invoice ->
        invoice.lines.any { line -> line.name.uppercase().contains("SAVER") }
    }

    fun createProformaCreditInovices(): List<InvoiceDomain> {
        val creditSubjects = creditInvoiceService.restCreditNumber(creditInvoices, subjects, finClaim)
        val proformaInvoices: List<InvoiceDomain> = creditInvoiceService.manageCreditInvoices(creditSubjects)
        val proformaInvoicesPayload = invoicesPayload.filter { it.document_type == "proforma" }

        return proformaInvoices.filterNot { proformaInvoice ->
            proformaInvoicesPayload.any { payload ->
                payload.subject_id == proformaInvoice.subject_id &&
                        payload.lines.any { line -> proformaInvoice.lines.any { it.name == line.name } }
            }
        }
    }

    fun bufferedInvoices(finClaim: List<ClaimDataDomain>, subjects: List<SubjectDomain>): List<InvoiceDomain> {
        val claimBuffer = finClaim.filter { it.cvUploadedNumberMonth < 10 }
        val bufferedInvoice =
            claimBuffer.map { claim ->
                if (CumulativeCvsDomain(claim.datesOfCvUploads).finalUploads > 0) {

                    val lines = CumulativeCvsDomain(claim.datesOfCvUploads).lastAdjusted.map { it ->
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
                        document_type = "proforma",
                        subject_id = subjectId,
                        status = "open",
                        due = 14,
                        note = null,
                        issued_on = LocalDate.now().toString(),
                        taxable_fulfillment_due = LocalDate.now().toString(),
                        lines = lines,
                        currency = "EUR"
                    )
                } else {
                    null
                }
            }
        return bufferedInvoice.filterNotNull()
    }
}
