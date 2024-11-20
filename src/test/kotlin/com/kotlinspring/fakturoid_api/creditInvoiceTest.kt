package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.domain.CreditInvoiceDomain
import com.kotlinspring.fakturoid_api.domain.InvoiceDomain
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import kotlin.test.Test

class creditInvoiceDomainTest {

    private val testUtils = TestUtils()


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
        println(proformaInvoices.first().lines.size)
        assertEquals("DO NOT PAY. PAID FROM YOUR CREDITS.", proformaInvoices.first().note)
        assertEquals(123456, proformaInvoices.first().relatedId)
        assertEquals("SAVER ${creditQuantity} CVS / APPLICATIONS", proformaInvoices.last().lines.first().name)
        assertEquals("final_invoice", proformaInvoices.last().documentType)
    }



    @Test
    fun `when 100% credits are spent and no validated saver proforma was dealed send offer porforma and switch to buffer system no additional invoice sent because the limit is not reached`() {

        //Given
        val creditQuantity = 4.0
        val creditInvoicesMockk = testUtils.createCreditMockkInvoice(quantity = creditQuantity)
        val subjectsMockk = testUtils.createCreditSubject()
        val invoiceDataMockk = listOf(testUtils.createClaimData(datesOfCvUploads = List(6) { LocalDate.now() }))
        val invoicePayload = testUtils.createCreditMockkInvoice(quantity = creditQuantity)
        val creditInvoiceDomain = CreditInvoiceDomain(creditInvoicesMockk, subjectsMockk, invoiceDataMockk, invoicePayload)
        //When
        val proformaInvoices = creditInvoiceDomain.creditInvoices

        //Then
        assertEquals(1, invoiceDataMockk.size)
        assert(proformaInvoices.isNotEmpty())
        assertEquals(2, proformaInvoices.size)
        assertEquals("100% of credits applied from total ${creditQuantity.toInt()} credits", proformaInvoices.first().lines.first().name)
        assertEquals("DO NOT PAY. PAID FROM YOUR CREDITS.", proformaInvoices.first().note)
        assertEquals("CV applications exceeded the credit", proformaInvoices.first().lines.last().name)
        assertEquals(123456, proformaInvoices.first().relatedId)
        assertEquals("Offer your next Saver 4 CVs / applications should start from the next month", proformaInvoices.last().lines.first().name)
        assertEquals("proforma", proformaInvoices.last().documentType)
    }


    @Test
    fun `when reached cv limit in the same month send buffer invoice, 100% credits are spent and no validated saver dealed switched to buffer system `() {

        //Given
        val creditQuantity = 4.0
        val creditInvoicesMockk = testUtils.createCreditMockkInvoice(quantity = creditQuantity)
        val subjectsMockk = testUtils.createCreditSubject()
        val finClaimMockk = listOf(testUtils.createClaimData(datesOfCvUploads = List(14) { LocalDate.now() }))
        val invoicePayload = testUtils.createCreditMockkInvoice(quantity = creditQuantity)

        val creditInvoiceDomain = CreditInvoiceDomain(creditInvoicesMockk, subjectsMockk, finClaimMockk, invoicePayload)

        //When
        val creditInvoice = creditInvoiceDomain.creditInvoices

        //Then
        assert(creditInvoice.isNotEmpty())
        assertEquals(3, creditInvoice.size)

        assertEquals("100% of credits applied from total ${creditQuantity.toInt()} credits", creditInvoice[0].lines.first().name)
        assertEquals("CV applications exceeded the credit", creditInvoice[0].lines.last().name)
        assertEquals(10.0, creditInvoice[2].lines.last().quantity)
        assertEquals(0.0, creditInvoice[0].lines.last().unitPrice)

        assertEquals("DO NOT PAY. PAID FROM YOUR CREDITS.", creditInvoice[0].note)
        assertEquals(123456, creditInvoice[0].relatedId)
        assertEquals("proforma", creditInvoice[0].documentType)

        assertEquals("Offer your next Saver 4 CVs / applications should start from the next month", creditInvoice[1].lines.first().name)
        assertEquals("DO NOT PAY. THIS IS AN OFFER PROFORMA.", creditInvoice[1].note)
        assertEquals("proforma", creditInvoice[1].documentType)

        assertEquals("CVs upload DreamJobs service", creditInvoice[2].lines.first().name)
        assertEquals(10.0, creditInvoice[2].lines.first().quantity)
        assertEquals( 7.0, creditInvoice[2].lines.first().unitPrice)
    }


    @Test
    fun `when reached in next month send buffer invoice, 100% credits are spent and no validated saver dealed switched to buffer system `() {

        val applications = listOf(
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            //here is the credit limit reached
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now(),
            LocalDate.now(),
            LocalDate.now(),
            LocalDate.now(),
            LocalDate.now(),
            LocalDate.now(),
            LocalDate.now()
            //here is reached buffer limit
        )

        //Given
        val creditQuantity = 4.0
        val remaining = (applications.filter { it.isAfter(LocalDate.now().withDayOfMonth(1).minusDays(1)) }.size.toDouble()) - creditQuantity
        val creditInvoicesMockk = testUtils.createCreditMockkInvoice(quantity = creditQuantity)
        val subjectsMockk = testUtils.createCreditSubject()
        val finClaimRawMockk = listOf(testUtils.createClaimData(datesOfCvUploads = applications))
        val invoicePayload = testUtils.createCreditReached100ProformaMockkInvoice(quantity= creditQuantity, exceeded = remaining, issuedOn = LocalDate.now().toString())

        val creditInvoiceDomain = CreditInvoiceDomain(creditInvoicesMockk, subjectsMockk, finClaimRawMockk, invoicePayload)

        //When
        val creditInvoice = creditInvoiceDomain.creditInvoices

        //Then
        assert(creditInvoice.isNotEmpty())
        assertEquals(2, creditInvoice.size)

        println(creditInvoice[0].lines.first().name)
        println(creditInvoice[1].lines.first().name)



        //assertEquals("Offer your next Saver 4 CVs / applications should start from the next month", creditInvoice[1].lines.first().name)
        assertEquals("DO NOT PAY. THIS IS AN OFFER PROFORMA.", creditInvoice[0].note)
        assertEquals("proforma", creditInvoice[0].documentType)

        assertEquals("CVs upload DreamJobs service", creditInvoice[1].lines.first().name)
        assertEquals("invoice", creditInvoice[1].documentType)
        assertEquals(12.0, creditInvoice[1].lines.first().quantity)
        assertEquals( 7.0, creditInvoice[1].lines.first().unitPrice)
    }

    @Test
    fun `when 100% credits are spent and new validate saver was dealed with one month gap and some applications are made without reached buffer limit these remaining cvs are in second line of new saver `() {

        val applications = listOf(
            //here is the credit limit reached for old saver
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            //here is finished last month without reached buffer limit and start of the new saver
        )

        //Given
        val creditQuantity = 4.0
        val remaining = (applications.filter { it.isAfter(LocalDate.now().withDayOfMonth(1).minusDays(1)) }.size.toDouble()) - creditQuantity
        val creditInvoicesMockk = testUtils.createCreditMockkInvoice(quantity = creditQuantity)
        val subjectsMockk = testUtils.createCreditSubject()
        val finClaimRawMockk = listOf(testUtils.createClaimData(datesOfCvUploads = applications))
        val invoicePayload =
            buildList<InvoiceDomain> {
                addAll(testUtils.createValidatedProformaMockkInvoice(quantity = creditQuantity))
                addAll(testUtils.createCreditReached100ProformaMockkInvoice(quantity= creditQuantity, exceeded = remaining, issuedOn = LocalDate.now().minusMonths(2).toString()))
            }

        val creditInvoiceDomain = CreditInvoiceDomain(creditInvoicesMockk, subjectsMockk, finClaimRawMockk, invoicePayload)

        //When
        val creditInvoice = creditInvoiceDomain.creditInvoices

        //Then
        assert(creditInvoice.isNotEmpty())
        assertEquals(1, creditInvoice.size)


        //assertEquals("Offer your next Saver 4 CVs / applications should start from the next month", creditInvoice[1].lines.first().name)
        assertEquals("Thank you for your business.", creditInvoice[0].note)
        assertEquals("final_invoice", creditInvoice[0].documentType)

        assertEquals("SAVER ${creditQuantity} CVS / APPLICATIONS", creditInvoice[0].lines.first().name)
        println(creditInvoice[0].lines.last().name)
        println(creditInvoice[0].lines.last().quantity)
        println(creditInvoice[0].lines.last().unitPrice)
        assertEquals(2, creditInvoice[0].lines.size)
        assertEquals(4.0, creditInvoice[0].lines.first().quantity)
        assertEquals( 7.0, creditInvoice[0].lines.first().unitPrice)
        assertEquals("Remaining CV applications not reached buffer limit", creditInvoice[0].lines.last().name)
        assertEquals( 5.0, creditInvoice[0].lines.last().quantity)
        assertEquals( 7.0, creditInvoice[0].lines.last().unitPrice)
    }

}




