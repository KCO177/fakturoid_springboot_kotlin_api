package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals


class CumulativeCvsDomainTest {

    /**
    when is reached value this month it does not go through this domain object
    * test when was reached limit last month
    * test when was reached limit two months ago
    * test when was reached limit cumulatively two months ago
    * test when was reached limit cumulatively two times and the last two months ago
    * test when was reached limit cumulatively this month
    * test when was not reached limit at the end of invoicing period
**/


    @Test
    fun testWhenWasReachedLimitLastMonth() {
        //given
        val cumulativeDates = listOf(
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

        //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)

        //then
        assertEquals(3, cumulativeCvsDomain.adjustedUploads)
        assertEquals(0, cumulativeCvsDomain.finalUploads)
    }

    @Test
    fun whenWasReachedLimitTwoMonthsAgo() {
        //given
        val cumulativeDates = listOf(
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
            LocalDate.now().minusMonths(0),
            LocalDate.now().minusMonths(0),
        )

        //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)

        //then
        assertEquals(5, cumulativeCvsDomain.adjustedUploads)
        assertEquals(0, cumulativeCvsDomain.finalUploads)
    }


    @Test
    fun whenWasReachedLimitCumulativelyTwoMonthsAgo() {
        //given
        val cumulativeDates = listOf(
            LocalDate.now().minusMonths(5),
            LocalDate.now().minusMonths(5),
            LocalDate.now().minusMonths(4),
            LocalDate.now().minusMonths(4),
            LocalDate.now().minusMonths(3),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(0),
            LocalDate.now().minusMonths(0),
        )

        //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)

        //then
        //assertEquals(1, cumulativeCvsDomain.numberContinuouslyReached)
        assertEquals(5, cumulativeCvsDomain.adjustedUploads)
        assertEquals(0, cumulativeCvsDomain.finalUploads)
    }

    @Test
    fun whenWasReachedLimitCumulativelyTwoTimesLastTwoMonthsAgo() {
        //given
        val cumulativeDates = listOf(
            LocalDate.now().minusMonths(10),
            LocalDate.now().minusMonths(9),
            LocalDate.now().minusMonths(8),
            LocalDate.now().minusMonths(8),
            LocalDate.now().minusMonths(7),
            LocalDate.now().minusMonths(7),
            LocalDate.now().minusMonths(6),
            LocalDate.now().minusMonths(6),
            LocalDate.now().minusMonths(6),
            LocalDate.now().minusMonths(6),

            LocalDate.now().minusMonths(5),
            LocalDate.now().minusMonths(5),
            LocalDate.now().minusMonths(4),
            LocalDate.now().minusMonths(4),
            LocalDate.now().minusMonths(3),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),
            LocalDate.now().minusMonths(2),

            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusMonths(0),
            LocalDate.now().minusMonths(0),
        )

        //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)

        //then
        //assertEquals(1, cumulativeCvsDomain.numberContinuouslyReached)
        assertEquals(5, cumulativeCvsDomain.adjustedUploads)
        assertEquals(0, cumulativeCvsDomain.finalUploads)
    }



    @Test
    fun whenWasReachedLimitCumulativelyThisMonth() {
        //given
        val cumulativeDates = listOf(LocalDate.now().minusMonths(4),
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

        //when
        val cumulativeCvsDomain = CumulativeCvsDomain(cumulativeDates)

        //then
        //assertEquals(1, cumulativeCvsDomain.numberContinuouslyReached)
        assertEquals(11, cumulativeCvsDomain.adjustedUploads)
        assertEquals(11, cumulativeCvsDomain.finalUploads)
    }

}
