package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.domain.CreditInvoiceDomain
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class creditInvoiceDomainTest{

    private val testUtils = TestUtils()



    @Test
    fun `test get last credit number in CreditSubjectDomain`() {

        //Given
        val creditInvoicesMockk = testUtils.createCreditDemoInvoice()
        val subjectsMockk = testUtils.createCreditSubject()
        val invoiceDataMockk = testUtils.createClaimDataDemo()
        val creditInvoiceDomain = CreditInvoiceDomain(creditInvoicesMockk, subjectsMockk, invoiceDataMockk, creditInvoicesMockk)
        //When
        val creditSubject = creditInvoiceDomain.creditSubjects

        //Then
        assert(creditSubject.isNotEmpty())
        assertEquals(499, creditSubject.first().remainingNumberOfCredits)

    }



}