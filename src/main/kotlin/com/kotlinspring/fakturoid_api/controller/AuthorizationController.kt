package com.kotlinspring.fakturoid_api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kotlinspring.fakturoid_api.domain.BearerTokenDomain
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.http.MediaType

class AuthorizationController {

    val refreshToken : String = System.getenv("REFRESH_TOKEN")
    val authorizationClient : String = System.getenv("AUTHORIZATION_CLIENT")
    val userAgent : String = System.getenv("USER_AGENT")


    fun getBearerToken(refreshToken: String, authorizationClient: String): String? {
        val url = "https://app.fakturoid.cz/api/v3/oauth/token"

        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", userAgent)
            .defaultHeader("Authorization", authorizationClient)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .build()

        val requestBody = mapOf(
            "grant_type" to "refresh_token",
            "refresh_token" to refreshToken
        )

        // Send POST request and handle response
            val response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus({ status -> !status.is2xxSuccessful }) { clientResponse ->
                    clientResponse.bodyToMono<String>().map { body ->
                        throw RuntimeException("Request failed with status: ${clientResponse.statusCode()}, response: $body")
                    }
                }
                .bodyToMono<String>()
                .block() // Blocking for simplicity; consider using reactive chaining in a reactive context

            val objectMapper = jacksonObjectMapper()
            val bearerTokenIn = objectMapper.readValue(response, BearerTokenDomain.BearerTokenDomain::class.java)

            val bearerToken = "${bearerTokenIn.token_type} ${bearerTokenIn.access_token}"
            return bearerToken
    }


}
