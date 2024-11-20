package com.kotlinspring.fakturoid_api.domain

import com.kotlinspring.fakturoid_api.TestUtils
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BufferedInvoiceDomainTest {

    private val testUtils = TestUtils()

    @Test
    fun whenWasReachedLimitThisMonth() {
        //given
        val applications = List(10) { LocalDate.now() }

        val finClaimMockk = ClaimDataDomain(
            tenant = TenantDomain(
                companyRegistrationNumber = "123456789",
                companyContactEmail = "tenant@email.com",
                companyLawName = "Test tenant Name"
            ),
            cvUploadedNumberMonth = applications.count { it.month == LocalDate.now().month && it.year == LocalDate.now().year },
            datesOfCvUploads = applications
        )

        val subjectMockk = testUtils.createSubject(id = 123456789, name = "Test subject")


        //when
        val bufferedInvoiceDomain = BufferedInvoiceDomain(listOf(finClaimMockk), listOf(subjectMockk)).bufferedInvoice

        //then
        assertNotNull(bufferedInvoiceDomain)
        assertEquals(1, bufferedInvoiceDomain.size)
        assertEquals("invoice", bufferedInvoiceDomain.first().documentType)
        assertEquals(10.0, bufferedInvoiceDomain.first().lines.sumOf { it.quantity })

    }

    @Test
    fun whenWasReachedLimitCumulativelyThisMonth() {
        //given
        val applications = listOf(
            LocalDate.now().minusMonths(4),
            LocalDate.now().minusMonths(4),
            LocalDate.now().minusMonths(3),
            LocalDate.now().minusMonths(3),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(1),
            LocalDate.now(),
            LocalDate.now(),
            LocalDate.now(),
            LocalDate.now(),
            LocalDate.now(),
            )



        val finClaimMockk = ClaimDataDomain(
            tenant = TenantDomain(
                companyRegistrationNumber = "123456789",
                companyContactEmail = "tenant@email.com",
                companyLawName = "Test tenant Name"
            ),
            cvUploadedNumberMonth = applications.count { it.month == LocalDate.now().month && it.year == LocalDate.now().year },
            datesOfCvUploads = applications)

        val subjectMockk = testUtils.createSubject(id = 123456789, name = "Test subject")


        //when
        val bufferedInvoiceDomain = BufferedInvoiceDomain(listOf(finClaimMockk), listOf(subjectMockk)).bufferedInvoice


        //then
        assertNotNull(bufferedInvoiceDomain)
        assertEquals(1, bufferedInvoiceDomain.size)
        assertEquals("invoice", bufferedInvoiceDomain.first().documentType)
        assertEquals("Buffered CV uploads JULY", bufferedInvoiceDomain.first().lines.first().name)
        assertEquals("Buffered CV uploads NOVEMBER", bufferedInvoiceDomain.first().lines.last().name)
        assertEquals(11.0, bufferedInvoiceDomain.first().lines.sumOf { it.quantity })

    }


    @Test
    fun testWhenWasReachedLimitLastMonth() {
        //given
        val applications = listOf(
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
        )

        val finClaimMockk = ClaimDataDomain(
            tenant = TenantDomain(
                companyRegistrationNumber = "123456789",
                companyContactEmail = "tenant@email.com",
                companyLawName = "Test tenant Name"
            ),
            cvUploadedNumberMonth = applications.count { it.month == LocalDate.now().month && it.year == LocalDate.now().year },
            datesOfCvUploads = applications)

        val subjectMockk = testUtils.createSubject(id = 123456789, name = "Test subject")


        //when
        val bufferedInvoiceDomain = BufferedInvoiceDomain(listOf(finClaimMockk), listOf(subjectMockk)).bufferedInvoice


        //then
        assertNotNull(bufferedInvoiceDomain)
        assertEquals(0, bufferedInvoiceDomain.size)

    }


}