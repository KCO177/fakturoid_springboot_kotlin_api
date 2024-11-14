package com.kotlinspring.fakturoid_api.domain

import com.fasterxml.jackson.annotation.JsonProperty

class LinesDomain(
    val name : String,
    val quantity : Double,
    @JsonProperty("unit_name")
    val unitName : String,
    @JsonProperty("unit_price")
    val unitPrice : Double? = 7.0,
    @JsonProperty("vat_rate")
    val vatRate : Double? = 21.0,
    @JsonProperty("total_price_without_vat")
    val totalWOVat : Double = unitPrice!! * quantity,
    @JsonProperty("total_price_with_vat")
    val totalWithVat : Double = unitPrice!! * vatRate!! / 100

) {
    companion object {

        internal fun createLines(invoice: ClaimDataDomain): List<LinesDomain> {
            return listOf(
                LinesDomain(
                    name = "CVs upload DreamJobs service",
                    quantity = 0.0,//invoice.cvUploadedNumber.toDouble(),
                    unitName = "CV",
                    unitPrice = 7.0,
                    vatRate = 21.0
                )
            )
        }
    }
}


