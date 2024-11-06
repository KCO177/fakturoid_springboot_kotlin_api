package com.kotlinspring.fakturoid_api.demo

import com.kotlinspring.fakturoid_api.domain.InvoiceDomain
import java.time.LocalDate




class DemoUtils {

    fun createDemoInvoice(
        customId: String = "2345",
        subjectId: Int = 23377698,
        due: Int = 14,
        issuedOn: String = LocalDate.now().toString(),
        taxableFulfillmentDue: String = LocalDate.now().toString(),
        lines: LinesDomain =  createInvoiceLines(),
        currency: String = "EUR",
        ) : InvoiceDomain {
        return InvoiceDomain(
        id = null,
        customId = customId,
        subject_id = subjectId,
        due = due,
        issued_on = issuedOn,
        taxable_fulfillment_due = taxableFulfillmentDue,
        lines = listOf(lines),
        currency = currency
        )

    }

    fun createInvoiceLines(
        name: String = "Hard work for the customer",
        quantity: Double = 1.0,
        unitName: String = "hour",
        unitPrice: Double = 100.0,
        vatRate: Double = 21.0
    ): LinesDomain {
        return LinesDomain(
            name = name,
            quantity = quantity,
            unitName = unitName,
            unitPrice = unitPrice,
            vatRate = vatRate
        )
    }

    fun dbOutput(): List<DjDbOutput> {


    }
}
}