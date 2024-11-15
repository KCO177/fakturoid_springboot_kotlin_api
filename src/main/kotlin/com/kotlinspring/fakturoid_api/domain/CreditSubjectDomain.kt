package com.kotlinspring.fakturoid_api.domain

class CreditSubjectDomain(
    val subjectId: Int,
    val remainingNumberOfCredits: Int,
    val totalCreditNumber: Int,
    val fiftypercentReached : Boolean,
    val seventyfivepercentReached : Boolean,
    val hundredpercentReached : Boolean
)
