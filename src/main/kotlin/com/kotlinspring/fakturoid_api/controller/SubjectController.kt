package com.kotlinspring.fakturoid_api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kotlinspring.fakturoid_api.domain.SubjectDomain
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Controller
class SubjectController {

    private val logger = KotlinLogging.logger {}

    val userAgent : String = System.getenv("USER_AGENT")
    val slug : String = System.getenv("SLUG")

    fun getSubject(bearerToken: String): List<SubjectDomain> {
        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/subjects.json"

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
        return objectMapper.readValue(response, objectMapper.typeFactory.constructCollectionType(List::class.java, SubjectDomain::class.java))
    }

    fun createSubject(bearerToken: String, subjectDomain: SubjectDomain): SubjectDomain? {
        val url = "https://app.fakturoid.cz/api/v3/accounts/${slug}/subjects.json"

        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("User-Agent", userAgent)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Authorization", bearerToken)
            .build()

        val response = webClient.post()
            .bodyValue(subjectDomain)
            .retrieve()
            .bodyToMono<String>()
            .block()

        logger.info{ "Subject ${subjectDomain.name} ${subjectDomain.registration_no} created" }

        return response?.let {
            logger.debug("Response: $it")
            jacksonObjectMapper().readValue(it, SubjectDomain::class.java) }
    }
}
