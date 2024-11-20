package com.kotlinspring.fakturoid_api.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.kotlinspring.fakturoid_api.service.SubjectService
import java.time.LocalDate

open class InvoiceDomain(
    val id: Int? = null,
    val customId: CustomIdDomain?,
    @JsonProperty("document_type")
    val documentType: String? = "invoice",
    @JsonProperty("related_id")
    val relatedId : Int? = null,
    @JsonProperty("subject_id")
    val subjectId: Int,
    val status : String?,
    val due: Int? = 14,
    val note: String? = "Thank you for your business.",
    @JsonProperty("issued_on")
    val issuedOn: String? = LocalDate.now().toString(),
    @JsonProperty("taxable_fulfillment_due")
    val taxableFulfillmentDue: String? = LocalDate.now().toString(),
    val lines: List<LinesDomain>,
    val currency: String? = "EUR",
    val totalWOVat : Double?,
    val totalWithVat : Double?
)
