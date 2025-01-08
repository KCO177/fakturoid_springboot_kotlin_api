package com.kotlinspring.fakturoid_api.domain

import mu.KotlinLogging
import java.time.LocalDate

class CreditInvoiceDomain (
    creditInvoices: List<InvoiceDomain>,
    internal val subjects: List<SubjectDomain>,
    internal val finClaimRaw: List<ClaimDataDomain>,
    invoicesPayload: List<InvoiceDomain>
) {


    private val logger = KotlinLogging.logger {}
    private val proformaInvoicesPayload: List<InvoiceDomain> = invoicesPayload.filter { it.documentType == "proforma" }
    internal val creditSubjects = remainingCreditNumber(creditInvoices, subjects, finClaimRaw)
    internal val creditInvoices: List<InvoiceDomain> = manageCreditInvoices(creditSubjects)

    internal fun remainingCreditNumber(
        creditInvoices: List<InvoiceDomain>,
        subjects: List<SubjectDomain>,
        invoiceData: List<ClaimDataDomain>
    ): List<CreditSubjectDomain> {
        val matchedCreditInvoices: List<InvoiceDomain> = creditInvoices.filter { creditInvoice ->
            creditInvoice.subjectId in subjects.map { it.id!! }
        }
        val firstDates: List<CreditSubject> = matchedCreditInvoices.map { invoice ->
            CreditSubject(
                invoice.subjectId,
                requireNotNull(invoice.id) { "Credit Saver Invoice id can not be null" },
                LocalDate.parse(invoice.issuedOn!!),
                /** here are no proforma savers, were filtered out before this class **/
                invoice.lines.filter { it.name.uppercase().contains("SAVER") || it.name.uppercase().contains("TRANSFERRED UNITS") }.sumOf { it.quantity.toInt() })

        }

        val creditSubjects = firstDates.map { creditSubject ->
            val numberOfUploadSinceFirstInvoice =
                numberOfCvUploadedSinceFirstInvoice(creditSubject, subjects, invoiceData)
            CreditSubjectDomain(
                subjectId = creditSubject.subjectId,
                saverInvoiceId = creditSubject.saverInvoiceId,
                saverInvoiceDate = creditSubject.invoiceDate,
                remainingNumberOfCredits = creditSubject.creditNumber - numberOfUploadSinceFirstInvoice,
                totalCreditNumber = creditSubject.creditNumber,
                fiftypercentReached = creditSubject.creditNumber / 2 <= numberOfUploadSinceFirstInvoice,
                seventyfivepercentReached = creditSubject.creditNumber * 0.75 <= numberOfUploadSinceFirstInvoice,
                hundredpercentReached = creditSubject.creditNumber <= numberOfUploadSinceFirstInvoice
            )
        }
        return creditSubjects
    }

    private fun numberOfCvUploadedSinceFirstInvoice(
        creditSubject: CreditSubject,
        subjects: List<SubjectDomain>,
        invoiceData: List<ClaimDataDomain>
    ): Int {
        val invoiceDate = creditSubject.invoiceDate
        val tenantRegistrationNumber: String = requireNotNull( subjects.first { it.id == creditSubject.subjectId }.registration_no ) { "Tenant CIN not found" }
        val datesOfCvUpdates: List<LocalDate>? =
            invoiceData.firstOrNull { it.tenant.companyRegistrationNumber == tenantRegistrationNumber }?.datesOfCvUploads
        return datesOfCvUpdates?.count { it.isEqual(invoiceDate) || it.isAfter(invoiceDate) } ?: 0
    }

    //TODO send info message to create new credit offer
    fun manageCreditInvoices(creditSubjects: List<CreditSubjectDomain>): List<InvoiceDomain> {
        return creditSubjects.flatMap { creditSubject ->
            when {
                creditSubject.hundredpercentReached -> {
                    val lineName = "100% of credits applied from total ${creditSubject.totalCreditNumber} credits"
                    manageCreditOverflow(creditSubject, lineName)
                }
                creditSubject.seventyfivepercentReached -> {
                    //TODO send notification "75% of credits applied from total ${creditSubject.totalCreditNumber} credits"
                    emptyList()
                }
                creditSubject.fiftypercentReached -> {
                    //TODO send notification "50% of credits applied from total ${creditSubject.totalCreditNumber} credits"
                    emptyList()
                }
                else -> emptyList()
            }
        }
    }

    private fun createCreditInvoice(creditSubject: CreditSubjectDomain, lineName: String): InvoiceDomain {
        return InvoiceDomain(
            id = null,
            customId = null,
            documentType = "proforma",
            relatedId = creditSubject.saverInvoiceId,
            subjectId = creditSubject.subjectId,
            status = "open",
            due = 14,
            note = "DO NOT PAY. PAID FROM YOUR CREDITS.",
            issuedOn = LocalDate.now().toString(),
            taxableFulfillmentDue = LocalDate.now().toString(),
            lines = createProformaLines(creditSubject, lineName),
            currency = "EUR",
            totalWOVat = null,
            totalWithVat = null
        )
    }

    private fun createProformaLines(creditSubject: CreditSubjectDomain, lineName: String): List<LinesDomain> {
        if (creditSubject.hundredpercentReached) {
            return listOf(
                LinesDomain(
                    name = lineName,
                    quantity = creditSubject.totalCreditNumber.toDouble(),
                    unitName = "credit",
                    unitPrice = 0.0,
                    vatRate = 0.0,
                    totalWOVat = null,
                    totalWithVat = null
                ),
                LinesDomain(
                    name = "CV applications exceeded the credit",
                    quantity = -creditSubject.remainingNumberOfCredits.toDouble(),
                    unitName = "CV applications",
                    unitPrice = 0.0,
                    vatRate = 0.0,
                    totalWOVat = null,
                    totalWithVat = null
                )
            )
        } else {
            return listOf(
                LinesDomain(
                    name = lineName,
                    quantity = creditSubject.remainingNumberOfCredits.toDouble(),
                    unitName = "credit",
                    unitPrice = 0.0,
                    vatRate = 0.0,
                    totalWOVat = null,
                    totalWithVat = null
                )
            )
        }
    }


    internal fun manageCreditOverflow(creditSubject: CreditSubjectDomain, lineName: String): List<InvoiceDomain> {
        val listOfInoviceToReturn = mutableListOf<InvoiceDomain>()

        val validatedProformaInvoice = this.proformaInvoicesPayload.mapNotNull {
            val equalSubjectId = it.subjectId == creditSubject.subjectId
            val containsSaver = it.lines.any { line -> line.name.uppercase().contains("VALIDATED SAVER") }
            val isNotCreditRunOutSaver = creditSubject.saverInvoiceId != it.relatedId
            val isNotEarlierSaver = creditSubject.saverInvoiceDate.isBefore(LocalDate.parse(it.issuedOn!!))
            if (equalSubjectId && containsSaver && isNotCreditRunOutSaver && isNotEarlierSaver) {
                it
            } else {
                null
            }
        }
        val oneHundredProforma = createCreditInvoice(creditSubject, lineName)


        when (validatedProformaInvoice.size) {
            /** NO VALIDATED SAVER PROFORMA INVOICE FOUND **/
            0 -> {

                /** IF THERE IS FIRST CREDIT REACHED AND NO 100% PROFORMA INVOICE FOUND, USE THE NEW 100% PROFORMA **/
                val previousHundredProforma = proformaInvoicesPayload.firstOrNull {
                    it.subjectId == creditSubject.subjectId &&
                            it.lines.any { line -> line.name.uppercase().contains("100% OF CREDITS APPLIED") }
                }
                val hundredPercentReachedProforma = previousHundredProforma ?: oneHundredProforma.also {
                    listOfInoviceToReturn.add(it)
                }

                val newCreditOfferProforma = createOfferProformaInvoice(creditSubject)
                listOfInoviceToReturn.add(newCreditOfferProforma)

                val reachedMonth = LocalDate.parse(requireNotNull( hundredPercentReachedProforma.issuedOn){ "100% proforma for credit ${hundredPercentReachedProforma.id} missing" }).monthValue

                val finClaimMonth = finClaimRaw.filter { it.cvUploadedNumberMonth > 0 }
                val tenantFinClaim = finClaimMonth.first { it.tenant.companyRegistrationNumber == subjects.first { subject -> subject.id == creditSubject.subjectId }.registration_no }

                /** IF IT IS THE SAME MONTH AS REACHED 100% CREDITS USE OVERFLOW APPLICATIONS INSTEAD **/
                val uploadsDates = if (LocalDate.now().month.value == reachedMonth) {
                    List(-creditSubject.remainingNumberOfCredits) { LocalDate.now() }
                } else {
                    tenantFinClaim.datesOfCvUploads
                }

                val claimDomains = listOf(
                    ClaimDataDomain(
                        tenant = TenantDomain(
                            companyRegistrationNumber = requireNotNull( subjects.first { it.id == creditSubject.subjectId }.registration_no){"Tenant CIN not found"},
                            companyContactEmail = null,
                            companyLawName = null
                        ),
                        datesOfCvUploads = uploadsDates,
                        cvUploadedNumberMonth = uploadsDates.size
                    )
                )

                val bufferedInvoices = BufferedInvoiceDomain(
                    finClaim = claimDomains,
                    subjects = subjects.filter { it.id == creditSubject.subjectId }
                ).bufferedInvoice

                if (bufferedInvoices.isNotEmpty()) {
                    listOfInoviceToReturn.addAll(bufferedInvoices)
                }

                return listOfInoviceToReturn
            }


            1 -> {
                val previousHundredProforma = proformaInvoicesPayload.firstOrNull {
                    it.subjectId == creditSubject.subjectId && it.lines.any { line -> line.name.uppercase().contains("100% OF CREDITS APPLIED") }
                }

                if (previousHundredProforma == null) {
                    listOfInoviceToReturn.add(oneHundredProforma)

                    /** if there is switch from buffer system some cvs not reached buffer limit should stay there **/
                    val tenantCIN = subjects.first { it.id == creditSubject.subjectId }.registration_no
                    val tenantApplication = finClaimRaw.filter { it.tenant.companyRegistrationNumber == tenantCIN }.first().datesOfCvUploads
                    val numberOfRemainApplication = if (tenantApplication.size > 9) { CumulativeCvsDomain(tenantApplication).adjustedUploads } else {tenantApplication.size}

                    val newCreditInvoice = createNewCreditInvoice(creditSubject, validatedProformaInvoice, numberOfRemainApplication, "Remaining CV applications from not reached buffer limit")
                    listOfInoviceToReturn.add(newCreditInvoice)

                } else {

                    val lastReachedMonth = requireNotNull( previousHundredProforma.issuedOn?.let { LocalDate.parse(it).monthValue + 1 }) { "100% proforma for credit ${previousHundredProforma.id} missing" }
                    val currentMonth = LocalDate.now().monthValue

                    if (lastReachedMonth == currentMonth) {
                        /** if there was 100% proforma reached credit in past amd new saver directly, continue to add the overflowed cvs in new saver **/
                        val newCreditInvoice = createNewDirectCreditInvoice(creditSubject, validatedProformaInvoice)
                        listOfInoviceToReturn.add(newCreditInvoice)
                    } else {
                        /** if there was 100% proforma reached credit in past not directly and some cvs was applied before the new saver **/

                        val tenantCIN = subjects.first { it.id == creditSubject.subjectId }.registration_no
                        val tenantApplication = finClaimRaw.filter {
                            it.tenant.companyRegistrationNumber == tenantCIN && it.cvUploadedNumberMonth in (lastReachedMonth).until(
                                currentMonth
                            )
                        }.first().datesOfCvUploads
                        val numberOfRemainApplication = if (tenantApplication.size > 9) {
                            CumulativeCvsDomain(tenantApplication).adjustedUploads
                        } else {
                            tenantApplication.size
                        }

                        val newCreditInvoice = createNewCreditInvoice(creditSubject, validatedProformaInvoice, numberOfRemainApplication, "Remaining CV applications not reached buffer limit")
                        listOfInoviceToReturn.add(newCreditInvoice)

                    }

                }

                return listOfInoviceToReturn
            }

            else -> {
                logger.warn { "There are more then one valid Proforma invoice found for subject ${creditSubject.subjectId} for next credit the final invoice can not be created" }
                return listOfInoviceToReturn
            }
        }
    }

    private fun createNewCreditInvoice(
        creditSubject: CreditSubjectDomain,
        validatedProformaInvoice: List<InvoiceDomain>,
        numberOfRemainApplication: Int,
        secondLineName: String
    ): InvoiceDomain { return InvoiceDomain(
            id = null,
            customId = null,
            documentType = "final_invoice",
            relatedId = validatedProformaInvoice.first().id,
            subjectId = creditSubject.subjectId,
            status = "open",
            due = 14,
            issuedOn = LocalDate.now().toString(),
            taxableFulfillmentDue = LocalDate.now().toString(),
            lines = listOf(
                LinesDomain(
                    name = validatedProformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.name.uppercase().replace("VALIDATED ", ""),
                    quantity = validatedProformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.quantity,
                    unitName = validatedProformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.unitName,
                    unitPrice = validatedProformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.unitPrice,
                    vatRate = validatedProformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.vatRate,
                    totalWOVat = null,
                    totalWithVat = null
                ),
                LinesDomain(
                    name = secondLineName,
                    quantity = numberOfRemainApplication.toDouble(),
                    unitName = "CV applications",
                    unitPrice = 7.0,
                    vatRate = null,
                    totalWOVat = null,
                    totalWithVat = null
                )
            ),
            currency = "EUR",
            totalWOVat = null,
            totalWithVat = null
        )
    }

    }

    private fun createOfferProformaInvoice(creditSubject: CreditSubjectDomain) : InvoiceDomain {
        return InvoiceDomain(
        id = null,
        customId = null,
        documentType = "proforma",
        relatedId = null,
        subjectId = creditSubject.subjectId,
        status = "open",
        due = 14,
        note = "DO NOT PAY. THIS IS AN OFFER PROFORMA.",
        issuedOn = LocalDate.now().toString(),
        taxableFulfillmentDue = LocalDate.now().toString(),
        lines = listOf(
            LinesDomain(
                name = "Offer your next Saver ${creditSubject.totalCreditNumber} CVs / applications should start from the next month",
                unitName = null,
                unitPrice = 7.0, //TODO need to check which price to set
                quantity = creditSubject.totalCreditNumber.toDouble(),
                totalWOVat = null,
                totalWithVat = null
            )
        ),
        currency = "EUR",
            totalWOVat = null,
            totalWithVat = null
        )
    }

    private fun createNewDirectCreditInvoice(creditSubject: CreditSubjectDomain, proformaInvoice: List<InvoiceDomain>): InvoiceDomain {

        return InvoiceDomain(
            id = null,
            customId = null,
            documentType = "final_invoice",
            relatedId = proformaInvoice.first().id,
            subjectId = creditSubject.subjectId,
            status = "open",
            due = 14,
            issuedOn = LocalDate.now().toString(),
            taxableFulfillmentDue = LocalDate.now().toString(),
            lines = listOf(
                LinesDomain(
                    name = proformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.name.uppercase().replace("VALIDATED ", ""),
                    quantity = proformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.quantity,
                    unitName = proformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.unitName,
                    unitPrice = proformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.unitPrice,
                    vatRate = proformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.vatRate,
                    totalWOVat = null,
                    totalWithVat = null
                ),
                LinesDomain(
                    name = "Transferred units exceeded the previous credit",
                    quantity = creditSubject.remainingNumberOfCredits.toDouble(),
                    unitName = "CV applications",
                    unitPrice = proformaInvoice.first().lines.first {
                        it.name.uppercase().contains("VALIDATED SAVER")
                    }.unitPrice,
                    vatRate = null,
                    totalWOVat = null,
                    totalWithVat = null
                )
            ),
            currency = "EUR",
            totalWOVat = null,
            totalWithVat = null
        )
    }



private class CreditSubject(
    val subjectId: Int,
    val saverInvoiceId: Int,
    val invoiceDate: LocalDate,
    val creditNumber: Int
)


