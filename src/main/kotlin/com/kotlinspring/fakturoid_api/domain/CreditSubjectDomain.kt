package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class CreditSubjectDomain(
    val subjectId: Int,
    val saverInvoiceId: Int,
    val saverInvoiceDate: LocalDate,
    val remainingNumberOfCredits: Int,
    val totalCreditNumber: Int,
    val fiftypercentReached : Boolean,
    val seventyfivepercentReached : Boolean,
    val hundredpercentReached : Boolean
)
