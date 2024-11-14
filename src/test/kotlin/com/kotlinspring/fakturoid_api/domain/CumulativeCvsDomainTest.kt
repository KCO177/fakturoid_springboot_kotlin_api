package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate
import java.time.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class CumulativeCvsDomainTest {

    //when is reached value this month it does not go through this domain object
    //when was reached limit last month
    //when was reached limit two months ago
    //when was reached limit cumulatively two months ago
    //when is start of new invoicing period and was not reached limit
    //when is end of invoicing period without reached limit


    @Test
    fun testReceiveCumulativeDomain() {
    //given
        val cumulativeDates  = datesOfCvUploads
    //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)
    //then
        assertEquals(5, cumulativeCvsDomain.adjustedUploads)
        assertEquals(0, cumulativeCvsDomain.finalUploads)
        assertEquals(hashMapOf(Month.JANUARY to 2, Month.FEBRUARY to 3), cumulativeCvsDomain.lastAdjusted)
    }

    @Test
    fun testWhenWasReachedLimitLastMonth() {
        //given
        val cumulativeDates  = listOf(
            LocalDate.of(2024, 10, 10),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 11, 5),
            LocalDate.of(2024, 11, 15),
            LocalDate.of(2024, 11, 20),
        )

        //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)
        //then
        assertEquals(3, cumulativeCvsDomain.adjustedUploads)
        assertEquals(0, cumulativeCvsDomain.finalUploads)
        assertEquals(hashMapOf(Month.NOVEMBER to 3), cumulativeCvsDomain.lastAdjusted)
    }

    @Test
    fun whenWasReachedLimitTwoMonthsAgo() {
        //given
        val cumulativeDates  = listOf(
            LocalDate.of(2024, 10, 10),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 11, 5),
            LocalDate.of(2024, 11, 15),
            LocalDate.of(2024, 11, 20),
            LocalDate.of(2024, 12, 11),
            LocalDate.of(2024, 12, 12),
        )

        //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)

        //then
        assertEquals(5, cumulativeCvsDomain.adjustedUploads)
        assertEquals(0, cumulativeCvsDomain.finalUploads)
        assertEquals(hashMapOf(Month.NOVEMBER to 3, Month.DECEMBER to 2), cumulativeCvsDomain.lastAdjusted)
    }



    @Test
    fun whenWasReachedLimitCumulativelyTwoMonthsAgo() {
        //given
        val cumulativeDates  = listOf(
            LocalDate.of(2024, 7, 10),
            LocalDate.of(2024, 7, 25),
            LocalDate.of(2024, 8, 25),
            LocalDate.of(2024, 8, 25),
            LocalDate.of(2024, 9, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 10, 25),
            LocalDate.of(2024, 11, 5),
            LocalDate.of(2024, 11, 15),
            LocalDate.of(2024, 11, 20),
            LocalDate.of(2024, 12, 11),
            LocalDate.of(2024, 12, 12),
        )

        //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)

        //then
        //assertEquals(1, cumulativeCvsDomain.numberContinuouslyReached)
        assertEquals(5, cumulativeCvsDomain.adjustedUploads)
        assertEquals(0, cumulativeCvsDomain.finalUploads)
        assertEquals(hashMapOf(Month.NOVEMBER to 3, Month.DECEMBER to 2), cumulativeCvsDomain.lastAdjusted)
    }







    val datesOfCvUploads = listOf(
        LocalDate.of(2024, 1, 15),
        LocalDate.of(2024, 1, 20),
        LocalDate.of(2024, 2, 10),
        LocalDate.of(2024, 2, 25),
        LocalDate.of(2024, 3, 5),
        LocalDate.of(2024, 3, 15),
        LocalDate.of(2024, 3, 20),
        LocalDate.of(2024, 4, 10),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 4, 25),
        LocalDate.of(2024, 5, 5),
        LocalDate.of(2024, 5, 15),
        LocalDate.of(2024, 5, 20),
        LocalDate.of(2024, 6, 10),
        LocalDate.of(2024, 6, 25),
        LocalDate.of(2024, 7, 5),
        LocalDate.of(2024, 7, 15),
        LocalDate.of(2024, 7, 20),
        LocalDate.of(2024, 8, 10),
        LocalDate.of(2024, 8, 25),
        LocalDate.of(2024, 9, 5),
        LocalDate.of(2024, 9, 15),
        LocalDate.of(2024, 9, 20),
        LocalDate.of(2024, 10, 10),
        LocalDate.of(2024, 10, 25),
        LocalDate.of(2024, 10, 25),
        LocalDate.of(2024, 10, 25),
        LocalDate.of(2024, 10, 25),
        LocalDate.of(2024, 10, 25),
        LocalDate.of(2024, 10, 25),
        LocalDate.of(2024, 11, 5),
        LocalDate.of(2024, 11, 15),
        LocalDate.of(2024, 11, 20),
        LocalDate.of(2024, 12, 10),
        LocalDate.of(2024, 12, 25),
        LocalDate.of(2024, 1, 5),
        LocalDate.of(2024, 1, 15),
        LocalDate.of(2024, 2, 15),
        LocalDate.of(2024, 2, 15),
        LocalDate.of(2024, 2, 15)
    )
}