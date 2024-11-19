package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.domain.CreditInvoiceDomain
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import kotlin.test.Test

class creditInvoiceDomainTest {

    private val testUtils = TestUtils()

    /*
    @Test
    fun `test get last credit number in CreditSubjectDomain`() {

        //Given
        val creditInvoicesMockk = testUtils.createCreditMockkInvoice(quantity = 500.0)
        val subjectsMockk = testUtils.createCreditSubject()
        val invoiceDataMockk = listOf(testUtils.createClaimData())
        val invoicePayload = testUtils.createValidatedProformaMockkInvoice(quantity = 500.0)
        val creditInvoiceDomain = CreditInvoiceDomain(creditInvoicesMockk, subjectsMockk, invoiceDataMockk, invoicePayload)

        //When
        val creditSubject = creditInvoiceDomain.creditSubjects

        //Then
        assert(creditSubject.isNotEmpty())
        assertEquals(499, creditSubject.first().remainingNumberOfCredits)
    }

    @Test
    fun `when 50% credits are spent get 50 percent proforma invoice in CreditSubjectDomain`() {

        //Given
        val creditQuantity = 2.0
        val creditInvoicesMockk = testUtils.createCreditMockkInvoice(quantity = creditQuantity)
        val subjectsMockk = testUtils.createCreditSubject()
        val invoiceDataMockk = listOf(testUtils.createClaimData())
        val creditInvoiceDomain =
            CreditInvoiceDomain(
                creditInvoices = creditInvoicesMockk,
                subjects = subjectsMockk,
                finClaimRaw = invoiceDataMockk,
                invoicesPayload = creditInvoicesMockk)
        //When
        val proformaInvoices = creditInvoiceDomain.creditInvoices

        //Then
        assertEquals(1, invoiceDataMockk.size)
        assert(proformaInvoices.isNotEmpty())
        assertEquals(1, proformaInvoices.size)
        assertEquals(
            "50% of credits applied from total ${creditQuantity.toInt()} credits",
            proformaInvoices.first().lines.first().name
        )
        assertEquals("DO NOT PAY. PAID FROM YOUR CREDITS.", proformaInvoices.first().note)
        assertEquals(123456, proformaInvoices.first().relatedId)

    }


    @Test
    fun `when 75% credits are spent get 50 percent proforma invoice in CreditSubjectDomain`() {

        //Given
        val creditQuantity = 4.0
        val creditInvoicesMockk = testUtils.createCreditMockkInvoice(quantity = creditQuantity)
        val subjectsMockk = testUtils.createCreditSubject()
        val invoiceDataMockk = listOf(testUtils.createClaimData(datesOfCvUploads = List(3) { LocalDate.now() }))
        val creditInvoiceDomain =
            CreditInvoiceDomain(creditInvoicesMockk, subjectsMockk, invoiceDataMockk, creditInvoicesMockk)
        //When
        val proformaInvoices = creditInvoiceDomain.creditInvoices

        //Then
        assertEquals(1, invoiceDataMockk.size)
        assert(proformaInvoices.isNotEmpty())
        assertEquals(1, proformaInvoices.size)
        assertEquals(
            "75% of credits applied from total ${creditQuantity.toInt()} credits",
            proformaInvoices.first().lines.first().name
        )
        assertEquals("DO NOT PAY. PAID FROM YOUR CREDITS.", proformaInvoices.first().note)
        assertEquals(123456, proformaInvoices.first().relatedId)
    }

*/
// TODO when 100% credits are spent get 100 percent proforma invoice in CreditSubjectDomain`() {
// TODO when more then 100% credits are spent and no new credit remaining are invoiced with standard invoice
// TODO when 100% credits are spent send proforma new credit



    @Test
    fun `when 100% credits are spent and new validated saver proforma was dealed make final invoice`() {

        //Given
        val creditQuantity = 4.0
        val creditInvoicesMockk = testUtils.createCreditMockkInvoice(quantity = creditQuantity)
        val subjectsMockk = testUtils.createCreditSubject()
        val invoiceDataMockk = listOf(testUtils.createClaimData(datesOfCvUploads = List(4) { LocalDate.now() }))
        val invoicePayload = testUtils.createValidatedProformaMockkInvoice(quantity = creditQuantity)
        val creditInvoiceDomain = CreditInvoiceDomain(creditInvoicesMockk, subjectsMockk, invoiceDataMockk, invoicePayload)
        //When
        val proformaInvoices = creditInvoiceDomain.creditInvoices

        //Then
        assertEquals(1, invoiceDataMockk.size)
        assert(proformaInvoices.isNotEmpty())
        assertEquals(2, proformaInvoices.size)
        assertEquals(
            "100% of credits applied from total ${creditQuantity.toInt()} credits",
            proformaInvoices.first().lines.first().name
        )
        assertEquals("DO NOT PAY. PAID FROM YOUR CREDITS.", proformaInvoices.first().note)
        assertEquals(123456, proformaInvoices.first().relatedId)
        assertEquals("SAVER 500 CVS / APPLICATIONS", proformaInvoices.last().lines.first().name)
        assertEquals("final_invoice", proformaInvoices.last().documentType)
    }
}


// TODO when 100% credits are spent get 100 percent proforma invoice in CreditSubjectDomain`()
// TODO when more then 100% credits are spent and no new credit remaining are invoiced with standard invoice
// TODO when 100% credits are spent send proforma new credit




