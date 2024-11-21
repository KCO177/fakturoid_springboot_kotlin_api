package com.kotlinspring.fakturoid_api.domain

import com.kotlinspring.fakturoid_api.TestUtils
import kotlin.test.Test

class ClaimDataDomainTest {

    private val testUtils = TestUtils()


    @Test
    fun `When db data given return a list of ClaimDataDomain`() {

        // Given
        val dbOutput = testUtils.dbOutput()

        // When
        val claimDataDomain = ClaimDataDomain.getInvoiceData(dbOutput)

        // Then
        assert(claimDataDomain.size == 2)
        assert(claimDataDomain[0].tenant.companyRegistrationNumber == "123456789")
        assert(claimDataDomain[1].tenant.companyRegistrationNumber == "234567890")
    }
}