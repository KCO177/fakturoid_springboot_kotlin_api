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
)
{
    private val bearerToken = requireNotNull( authorizationController.getBearerToken(AuthorizationController().refreshToken, AuthorizationController().authorizationClient)) { "Bearer token for fakturoid could not be created" }

    //prepare data from db
    final val invoiceDataRaw : List<ClaimDataDomain> = ClaimDataDomain.getInvoiceData(demoUtils.dbOutput())
    final val invoiceData = invoiceDataRaw.filter { it.cvUploadedNumberMonth > 0 }

    //filter subject by tenant
    val tenantRegNumbers : List<TenantDomain> = invoiceData.map { it.tenant }
    val subjects : List<SubjectDomain> = tenantRegNumbers.map{ tenant -> subjectService.findOrCreateTenant(bearerToken, SubjectDomain.mapTenantToSubjectDomain(tenant))}

    //get all invoices from last year
    val invoicesPayload = requireNotNull( invoiceController.getInvoices(bearerToken)) { "Invoices could not be fetched from fakturoid" }

    //get customId
    val newCustomId = CustomIdDomain.getCustomId(invoicesPayload)

    //filter if credit exists
    val creditInvoices : List<InvoiceDomain> = invoicesPayload.filter { invoice ->
        invoice.lines.any { line -> line.name.uppercase().contains("SAVER") }
    }

    fun ProformaCreditInovices() : List<InvoiceDomain> {
        val creditSubjects = creditInvoiceService.restCreditNumber(creditInvoices, subjects, invoiceData)
        return creditInvoiceService.manageCreditInvoices(creditSubjects)
    } //TODO filter the invoices to sent






}






    //create payload to sent
