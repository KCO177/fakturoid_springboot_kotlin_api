package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class DjDbOutput(
    val companyRegistrationNumber: String,
    val companyContactEmail: String,
    val companyLawName: String,
    val cvQuantityMonth: Int,
    val cvQuantityYear: Int,
    val datesOfCvUpload: List<LocalDate>
)

