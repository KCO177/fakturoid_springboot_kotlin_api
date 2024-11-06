package com.kotlinspring.fakturoid_api.service

import com.kotlinspring.fakturoid_api.controller.AuthorizationController
import com.kotlinspring.fakturoid_api.controller.InvoiceController
import com.kotlinspring.fakturoid_api.demo.DemoUtils
import com.kotlinspring.fakturoid_api.domain.CustomIdDomain
import com.kotlinspring.fakturoid_api.domain.InvoiceDataDomain
import com.kotlinspring.fakturoid_api.domain.InvoiceDomain

class InvoiceService (
    private val subjectService: SubjectService,
    private val invoiceController: InvoiceController,
    private val authorizationController: AuthorizationController,
    private val demoUtils: DemoUtils
)
{
    private val bearerToken = requireNotNull( authorizationController.getBearerToken(AuthorizationController().refreshToken, AuthorizationController().authorizationClient)) { "Bearer token for fakturoid could not be created" }

    //receive data from payload
    val invoicesPayload = requireNotNull( InvoiceController().getInvoices(bearerToken) ) { "Invoices from fakturoid could not be fetched" }

    //CREDITS:
    //TODO filter invoice payload by subjectId -> filter if the invoice containt credit amount
    //TODO get last credit amount

    //CVs LIMIT FOR INVOICING:

    //prepare data from db
    val invoiceData = InvoiceDataDomain.getInvoiceData(demoUtils.dbOutput())

    //get customId
    val newCustomId = CustomIdDomain.getCustomId(invoicesPayload)

    //create payload to sent
    val invoices = InvoiceDomain.getInvoices(invoiceData, newCustomId, bearerToken, subjectService)
    //TODO if invoice not exists for the date range(month) -> create invoice / else update
    val createdInvoices = invoiceController.createInvoice(bearerToken, invoices)





}
