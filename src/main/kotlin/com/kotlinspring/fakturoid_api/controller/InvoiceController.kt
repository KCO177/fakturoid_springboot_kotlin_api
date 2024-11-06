package com.kotlinspring.fakturoid_api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kotlinspring.fakturoid_api.domain.InvoiceDomain
import com.kotlinspring.fakturoid_api.domain.SubjectDomain
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

class InvoiceController {

    val userAgent : String = System.getenv("USER_AGENT")
    val slug : String = System.getenv("SLUG")

    fun getInvoices(bearerToken: String): List<InvoiceDomain>? {

        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/invoices.json"

        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", userAgent)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", bearerToken)
            .build()

        val response = webClient.get()
            .retrieve()
            .bodyToMono<String>()
            .block()

        val objectMapper = jacksonObjectMapper()
        return objectMapper.readValue(response, objectMapper.typeFactory.constructCollectionType(List::class.java, InvoiceDomain::class.java))
    }

    fun createInvoice(bearerToken: String, invoiceDomains: List<InvoiceDomain>) {
        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/invoices.json"

        WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", "testovacíAplikace (koscelnik.p@gmail.com)")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", bearerToken)
            .build()
            .post()
            .bodyValue(jacksonObjectMapper().typeFactory.constructCollectionType(List::class.java, InvoiceDomain::class.java))
            .retrieve()
            .bodyToMono<String>()
            .block()
    }
}








