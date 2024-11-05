package com.kotlinspring.fakturoid_api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kotlinspring.fakturoid_api.domain.InvoiceDomain
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

class InvoicesController {

    val userAgent : String = System.getenv("USER_AGENT")
    val slug : String = System.getenv("SLUG")

    fun getInvoices(bearerToken: String): InvoiceDomain? {

        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/invoices.json"

        val invoiceDomain = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", userAgent)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", bearerToken)
            .build()
            .get()
            .retrieve()
            .bodyToMono<InvoiceDomain>()
            .block()

        return invoiceDomain
    }

    fun createInvoice(slug: String, bearerToken: String, invoiceDomain: InvoiceDomain) {
        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/invoices.json"

        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", "testovacíAplikace (koscelnik.p@gmail.com)")
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", bearerToken)
            .build()
            .post()
            .bodyValue(jacksonObjectMapper().writeValueAsString(invoiceDomain))
            .retrieve()
            .bodyToMono<String>()
            .block()
    }
}








