package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate


class CreditInvoiceHandlerDomain (
    subjects : List<SubjectDomain>,
    creditInvoices : List<InvoiceDomain>,
    invoiceData : List<InvoiceDataDomain>

) {

    //match credit invoices with tenants from db which uploaded CVs last month
    private val matchedCreditInvoices: List<InvoiceDomain> = creditInvoices.filter { creditInvoice ->
        creditInvoice.subject_id in subjects.map { it.id!! }
    }

    //if matchedCreditTenants is not empty -> get number of credits invoiced and invoice date
    private val firstDates: List<CreditSubject> = matchedCreditInvoices.map { invoice ->
        CreditSubject(invoice.subject_id, LocalDate.parse(invoice.issued_on!!),
            invoice.lines.filter { it.name.uppercase().contains("SAVER") }.sumOf { it.quantity.toInt() })
    }

    //for each subject in CreditInvoice calculate the number of credits
    val restCreditNumber: List<CreditSubjectDomain> = firstDates.map { creditSubject ->
        val numberOfUploadSinceFirstInvoice = numberOfCvUploadedSinceFirstInvoice(creditSubject, subjects, invoiceData)
        CreditSubjectDomain(
            subjectId = creditSubject.subjectId,
            restOfCreditNumber = creditSubject.creditNumber - numberOfUploadSinceFirstInvoice,
            fiftypercentReached = creditSubject.creditNumber / 2 <= numberOfUploadSinceFirstInvoice,
            seventyfivepercentReached = creditSubject.creditNumber * 3 / 4 <= numberOfUploadSinceFirstInvoice
        )
    }

    private fun numberOfCvUploadedSinceFirstInvoice(creditSubject: CreditSubject, subjects: List<SubjectDomain>, invoiceData: List<InvoiceDataDomain>): Int {
        val invoiceDate = creditSubject.invoiceDate
        val tenantRegistrationNumber: String = subjects.first { it.id == creditSubject.subjectId }.CIN
        val datesOfCvUpdates: List<LocalDate>? =
            invoiceData.firstOrNull { it.tenant.companyRegistrationNumber == tenantRegistrationNumber }?.datesOfCvUploads
        return datesOfCvUpdates?.count { it.isEqual(invoiceDate) || it.isAfter(invoiceDate) } ?: 0
    }

    private class CreditSubject(
        val subjectId: Int,
        val invoiceDate: LocalDate,
        val creditNumber: Int
    )

}
