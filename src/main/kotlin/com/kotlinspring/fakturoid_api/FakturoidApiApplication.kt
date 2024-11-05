package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.controller.AuthorizationController
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FakturoidApiApplication

fun main(args: Array<String>) {
    runApplication<FakturoidApiApplication>(*args)

    val bearerToken = AuthorizationController().sendRefreshToken(AuthorizationController().refreshToken, AuthorizationController().authorizationClient)
    println(bearerToken)
}
