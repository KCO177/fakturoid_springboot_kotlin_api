package com.kotlinspring.fakturoid_api.service

import com.kotlinspring.fakturoid_api.controller.AuthorizationController
import com.kotlinspring.fakturoid_api.controller.InvoiceController
import com.kotlinspring.fakturoid_api.demo.DemoUtils
import com.kotlinspring.fakturoid_api.domain.*
import org.springframework.stereotype.Service

@Service
class InvoiceService (
    private val subjectService: SubjectService,
    private val invoiceController: InvoiceController,
    private val authorizationController: AuthorizationController,
    private val demoUtils: DemoUtils
)
{
    private val bearerToken = requireNotNull( authorizationController.getBearerToken(AuthorizationController().refreshToken, AuthorizationController().authorizationClient)) { "Bearer token for fakturoid could not be created" }

    //prepare data from db
    final val invoiceDataRaw : List<InvoiceDataDomain> = InvoiceDataDomain.getInvoiceData(demoUtils.dbOutput())
    final val invoiceData = invoiceDataRaw.filter { it.cvUploadedNumberMonth > 0 }

    //filter subject by tenant
    final val tenantRegNumbers : List<TenantDomain> = invoiceData.map { it.tenant }
    final val subjects : List<SubjectDomain> = tenantRegNumbers.map{ tenant -> subjectService.findOrCreateTenant(bearerToken, SubjectDomain.mapTenantToSubjectDomain(tenant))}

    //get all invoices from last year
    final val invoicesPayload = requireNotNull( invoiceController.getInvoices(bearerToken)) { "Invoices could not be fetched from fakturoid" }

    //get customId
    val newCustomId = CustomIdDomain.getCustomId(invoicesPayload)

    //filter if credit exists
    final val creditInvoices : List<InvoiceDomain> = invoicesPayload.filter { invoice ->
        invoice.lines.any { line -> line.name.uppercase().contains("SAVER") }
    }

    fun manageCredits() {
            val creditInvoices = CreditInvoiceHandlerDomain(subjects, creditInvoices, invoiceData)
            creditInvoices.restCreditNumber.forEach { creditSubject ->
                if (creditSubject.fiftypercentReached) {
                    //TODO CreditInvoiceDomain.sendInvoiceDoNotPay()
                    //TODO put invoice do not pay to invoice outcoming payload
                }
                if (creditSubject.seventyfivepercentReached) {
                    //TODO CreditInvoiceDomain.sendInvoiceDoNotPay()
                    //TODO send invoice do not pay to invoice outcoming payload
                }
            }
    }
}






    //create payload to sent
