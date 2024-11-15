package com.kotlinspring.fakturoid_api.domain

import com.kotlinspring.fakturoid_api.TestUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class ClaimDomainTest {

    private val testUtils = TestUtils()

    @Test
    fun `test get invoice data`() {

        //Given
        val dbValues = testUtils.dbOutput()

        //When
        val claimDomain = ClaimDataDomain.getInvoiceData(dbValues)

        //Then
        assert(claimDomain.isNotEmpty())
        assertEquals(2, claimDomain.size)
        assertEquals("123456789", claimDomain.first().tenant.companyRegistrationNumber)
        assertEquals(10, claimDomain.first().datesOfCvUploads.size)
    }
}