package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.controller.AuthorizationController
import com.kotlinspring.fakturoid_api.controller.InvoiceController
import com.kotlinspring.fakturoid_api.controller.SubjectController
import com.kotlinspring.fakturoid_api.service.InvoiceService
import com.kotlinspring.fakturoid_api.service.SubjectService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FakturoidApiApplication

fun main(args: Array<String>) {
    runApplication<FakturoidApiApplication>(*args)

    val bearerToken = AuthorizationController().getBearerToken(AuthorizationController().refreshToken, AuthorizationController().authorizationClient)
    println(bearerToken)




//    val invoiceService = InvoiceService( SubjectService(SubjectController()), InvoiceController(), AuthorizationController(), )
//    invoiceService.createInvoices()

}
