package com.kotlinspring.fakturoid_api.domain


class BankAccountDomain(
    val id: Int,
    val name: String,
    val currency: String,
    val number: String,
    val iban: String,
    val swift_bic: String,
    val pairing: Boolean,
    val expense_pairing: Boolean,
    val payment_adjustment: Boolean,
    val default: Boolean,
    val created_at: String,
    val updated_at: String
)
