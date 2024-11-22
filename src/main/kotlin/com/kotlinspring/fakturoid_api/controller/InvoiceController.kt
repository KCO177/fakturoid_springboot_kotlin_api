package com.kotlinspring.fakturoid_api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kotlinspring.fakturoid_api.domain.InvoiceDomain
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.LocalDate

@Controller
class InvoiceController {


    fun getInvoices(bearerToken: String): List<InvoiceDomain>? {
        println(bearerToken)
        val userAgent : String = System.getenv("USER_AGENT")
        val slug : String = System.getenv("SLUG")

        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/invoices.json"

        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", userAgent)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", bearerToken)
            .build()

        val response = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("since", LocalDate.of(LocalDate.now().year, 1, 1))
                    .build()
            }
            .retrieve()
            .bodyToMono<String>()
            .block()

        val objectMapper = jacksonObjectMapper()
        return objectMapper.readValue(response, objectMapper.typeFactory.constructCollectionType(List::class.java, InvoiceDomain::class.java))
    }

    fun getInvoicesBySubject(bearerToken: String, subjectId : Int): List<InvoiceDomain>? {
        val userAgent : String = System.getenv("USER_AGENT")
        val slug : String = System.getenv("SLUG")

        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/invoices.json"

        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", userAgent)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", bearerToken)
            .build()

        val response = webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("subject_id", subjectId)
                    .queryParam("since", LocalDate.of(LocalDate.now().year, 1, 1))
                    .build()
            }
            .retrieve()
            .bodyToMono<String>()
            .block()


        val objectMapper = jacksonObjectMapper()
        return objectMapper.readValue(response, objectMapper.typeFactory.constructCollectionType(List::class.java, InvoiceDomain::class.java))
    }


    fun createInvoices(bearerToken: String, invoiceDomains: List<InvoiceDomain>) {
        println(bearerToken)
        //TODO it is not possible to send the invoices in collections
        val userAgent : String = System.getenv("USER_AGENT")
        val slug : String = System.getenv("SLUG")

        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/invoices.json"
        invoiceDomains.forEach { invoiceDomain ->
            WebClient.builder()
                .baseUrl(url)
                .defaultHeader("User-Agent", userAgent)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", bearerToken)
                .build()
                .post()
                .bodyValue(jacksonObjectMapper().writeValueAsString(invoiceDomain).also { println(it) })
                .retrieve()
                .bodyToMono<String>()
                .block()
            }
    }
}








