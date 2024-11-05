package com.kotlinspring.fakturoid_api.domain

class BearerTokenDomain {
    class BearerTokenDomain(
        val access_token: String,
        val token_type: String,
        val expires_in: Int,
    )

}