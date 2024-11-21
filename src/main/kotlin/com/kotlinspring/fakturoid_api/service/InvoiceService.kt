package com.kotlinspring.fakturoid_api.service

import com.kotlinspring.fakturoid_api.controller.AuthorizationController
import com.kotlinspring.fakturoid_api.controller.InvoiceController
import com.kotlinspring.fakturoid_api.demo.DemoUtils
import com.kotlinspring.fakturoid_api.domain.*
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class InvoiceService(
    private val subjectService: SubjectService,
    private val invoiceController: InvoiceController,
    private val authorizationController: AuthorizationController,
) {

    private val demoUtils: DemoUtils = DemoUtils()

    private val logger = KotlinLogging.logger {}

    private val bearerToken = requireNotNull(authorizationController.getBearerToken(AuthorizationController().refreshToken, AuthorizationController().authorizationClient)) { "Bearer token for fakturoid could not be created" }

    private val finClaimDataRaw: List<ClaimDataDomain> = ClaimDataDomain.getInvoiceData(demoUtils.dbOutput())
    private val finClaim = finClaimDataRaw.filter { it.cvUploadedNumberMonth > 0 }
    private val tenantRegNumbers: List<TenantDomain> = finClaim.map { it.tenant }
    private val subjects: List<SubjectDomain> = tenantRegNumbers.map { tenant -> subjectService.findOrCreateTenant(bearerToken, SubjectDomain.mapTenantToSubjectDomain(tenant)) }
    private val invoicesPayload = requireNotNull(invoiceController.getInvoices(bearerToken)) { "Invoices could not be fetched from fakturoid" }

    fun createInvoices(): List<InvoiceDomain> {
        val bufferedInvoices: List<InvoiceDomain> = BufferedInvoiceDomain(finClaim, subjects).bufferedInvoice
        val creditInvoices: List<InvoiceDomain> =
            invoicesPayload.filter { invoice -> invoice.lines.any { line -> line.name.uppercase().contains("SAVER") } && invoice.documentType != "proforma" }
            if (creditInvoices.isNotEmpty()) { CreditInvoiceDomain(creditInvoices, subjects, finClaimDataRaw, invoicesPayload).creditInvoices }

        val invoices: List<InvoiceDomain> =
            buildList{
                if (bufferedInvoices.isNotEmpty()) addAll(bufferedInvoices)
                if (creditInvoices.isNotEmpty()) addAll(creditInvoices) }

        return invoices
    }

    fun postInvoices(invoices: List<InvoiceDomain>) {
        if (invoices.isNotEmpty()) { invoiceController.createInvoices(bearerToken, invoices) } else {
            logger.info("There is no invoices to post in fakturoid")
        }
    }

}

