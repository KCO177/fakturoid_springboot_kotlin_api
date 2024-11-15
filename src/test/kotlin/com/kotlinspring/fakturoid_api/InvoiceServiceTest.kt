package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.controller.AuthorizationController
import com.kotlinspring.fakturoid_api.controller.InvoiceController
import com.kotlinspring.fakturoid_api.service.CreditInvoiceService
import com.kotlinspring.fakturoid_api.service.InvoiceService
import com.kotlinspring.fakturoid_api.service.SubjectService
import org.springframework.boot.test.mock.mockito.MockBean
import kotlin.test.Test

class InvoiceServiceTest {

    @MockBean
    lateinit var subjectServiceMockk: SubjectService
    lateinit var invoiceControllerMockk: InvoiceController
    lateinit var authorizationControllerMockk: AuthorizationController
    lateinit var creditInvoiceServiceMockk: CreditInvoiceService



}