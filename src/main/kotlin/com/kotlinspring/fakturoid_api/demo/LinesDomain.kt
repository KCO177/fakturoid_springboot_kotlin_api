package com.kotlinspring.fakturoid_api.demo

import com.fasterxml.jackson.annotation.JsonProperty
import com.kotlinspring.fakturoid_api.domain.InvoiceDataDomain
import com.kotlinspring.fakturoid_api.domain.InvoiceDomain

class LinesDomain(
    val name : String,
    val quantity : Double,
    @JsonProperty("unit_name")
    val unitName : String,
    @JsonProperty("unit_price")
    val unitPrice : Double,
    @JsonProperty("vat_rate")
    val vatRate : Double,
    @JsonProperty("total_price_without_vat")
    val totalWOVat : Double = unitPrice * quantity,
    @JsonProperty("total_price_with_vat")
    val totalWithVat : Double = unitPrice * vatRate / 100

) {
    companion object {

        internal fun createLines(invoice: InvoiceDataDomain): List<LinesDomain> {
            return listOf(
                LinesDomain(
                    name = "CVs upload DreamJobs service",
                    quantity = invoice.cvUploadedNumber.toDouble(),
                    unitName = "CV",
                    unitPrice = 7.0,
                    vatRate = 21.0
                )
            )
        }
    }
}


