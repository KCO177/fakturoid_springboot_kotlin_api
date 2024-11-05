package com.kotlinspring.fakturoid_api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kotlinspring.fakturoid_api.domain.BankAccountDomain
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

class BankAccountController {

    val userAgent : String = System.getenv("USER_AGENT")
    val slug : String = System.getenv("SLUG")

    fun getBankAccount(slug: String, bearerToken: String): BankAccountDomain? {
        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/bank_accounts.json"


        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", userAgent)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", bearerToken)
            .build()

        val response = webClient.get()
            .retrieve()
            .bodyToMono<String>()
            .block()

        val objectMapper = jacksonObjectMapper()
        return objectMapper.readValue(response, BankAccountDomain::class.java)
    }
}


