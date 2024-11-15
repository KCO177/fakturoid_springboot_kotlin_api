package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class CreditInvoiceDomain (
    creditInvoices: List<InvoiceDomain>,
    subjects: List<SubjectDomain>,
    finClaim: List<ClaimDataDomain>,
    invoicesPayload: List<InvoiceDomain>
)
{
    internal val creditSubjects = remainingCreditNumber(creditInvoices, subjects, finClaim)
    internal val proformaInvoices: List<InvoiceDomain> = manageCreditInvoices(creditSubjects)
    internal val proformaInvoicesPayload = invoicesPayload.filter { it.documentType == "proforma" }

    val proformaInvoicesFiltered = proformaInvoices.filterNot { proformaInvoice ->
    proformaInvoicesPayload.any { payload ->
        payload.subjectId == proformaInvoice.subjectId &&
                payload.lines.any { line -> proformaInvoice.lines.any { it.name == line.name } }
        }
    }


    private fun remainingCreditNumber(creditInvoices: List<InvoiceDomain>, subjects: List<SubjectDomain>, invoiceData: List<ClaimDataDomain> ): List<CreditSubjectDomain> {
        val matchedCreditInvoices: List<InvoiceDomain> = creditInvoices.filter { creditInvoice ->
            creditInvoice.subjectId in subjects.map { it.id!! }
        }
        val firstDates: List<CreditSubject> = matchedCreditInvoices.map { invoice ->
            CreditSubject(invoice.subjectId, LocalDate.parse(invoice.issuedOn!!),
                invoice.lines.filter { it.name.uppercase().contains("SAVER") }.sumOf { it.quantity.toInt() })
        }
        val creditSubjects = firstDates.map { creditSubject ->
            val numberOfUploadSinceFirstInvoice = numberOfCvUploadedSinceFirstInvoice(creditSubject, subjects, invoiceData)
            CreditSubjectDomain(
                subjectId = creditSubject.subjectId,
                remainingNumberOfCredits = creditSubject.creditNumber - numberOfUploadSinceFirstInvoice,
                totalCreditNumber = creditSubject.creditNumber,
                fiftypercentReached = creditSubject.creditNumber / 2 <= numberOfUploadSinceFirstInvoice,
                seventyfivepercentReached = creditSubject.creditNumber * 0.75 <= numberOfUploadSinceFirstInvoice,
                hundredpercentReached = creditSubject.creditNumber <= numberOfUploadSinceFirstInvoice
            )
        }
        return creditSubjects
    }

    private fun numberOfCvUploadedSinceFirstInvoice(creditSubject: CreditSubject, subjects: List<SubjectDomain>, invoiceData: List<ClaimDataDomain>): Int {
        val invoiceDate = creditSubject.invoiceDate
        val tenantRegistrationNumber: String = subjects.first { it.id == creditSubject.subjectId }.CIN
        val datesOfCvUpdates: List<LocalDate>? =
            invoiceData.firstOrNull { it.tenant.companyRegistrationNumber == tenantRegistrationNumber }?.datesOfCvUploads
        return datesOfCvUpdates?.count { it.isEqual(invoiceDate) || it.isAfter(invoiceDate) } ?: 0
    }


    fun manageCreditInvoices(creditSubjects : List<CreditSubjectDomain>) : List<InvoiceDomain> {
        val creditInvoices = creditSubjects.map { creditSubject ->

            val lineName : String

            when {
                creditSubject.hundredpercentReached
                    -> lineName = "100% of credits applied from total ${creditSubject.totalCreditNumber} credits"
                //TODO send info message to create new credit offer


                creditSubject.seventyfivepercentReached -> lineName = "75% of credits applied from total ${creditSubject.totalCreditNumber} credits"
                //TODO send info message to create new credit offer
                creditSubject.fiftypercentReached -> lineName = "50% of credits applied from total ${creditSubject.totalCreditNumber} credits"
                else -> lineName = "applied credits from total ${creditSubject.totalCreditNumber} credits"
            }

            InvoiceDomain(
                id = null,
                customId = null,
                documentType = "proforma",
                subjectId = creditSubject.subjectId,
                status = "paid",
                due = 14,
                note = "DO NOT PAY. PAID FROM YOUR CREDITS.",
                issuedOn = LocalDate.now().toString(),
                taxableFulfillmentDue = LocalDate.now().toString(),
                lines = listOf( LinesDomain (
                    name = lineName,
                    quantity = (creditSubject.totalCreditNumber - creditSubject.remainingNumberOfCredits).toDouble(),
                    unitName = "CV upload credit",
                    unitPrice = 0.0,
                    vatRate = 0.0
                )
                ),
                currency = "EUR"
            )
        }
        return creditInvoices
    }


    private class CreditSubject(
        val subjectId: Int,
        val invoiceDate: LocalDate,
        val creditNumber: Int
    )

}
