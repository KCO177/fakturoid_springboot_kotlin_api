package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class CumulativeCvsDomain(datesOfCvUploads: List<LocalDate>) {

    private val cvLimit: Int = 10 /** item limit to be invoiced **/

    private val cvUploadsPerMonth = datesOfCvUploads
        .groupBy { it.withDayOfMonth(1) }
        .mapValues { (_, dates) -> dates.size }
        .toSortedMap(compareByDescending { it })

    private val firstReachedMonth = cvUploadsPerMonth
        .filter { it.value >= cvLimit }
        .keys
        .maxOrNull()

    //TODO if first reached month is null, then calculate from the beginning of the year until the end of the month for each cv limit,  setup the month when reached cv limit and continue calculate from the next month

    val adjustedUploads =  if (firstReachedMonth != null) {
        calculateLimitisIfReached()
    } else calculateLimits(cvLimit)


    fun calculateLimitisIfReached() : Int {
        val cumulativeUploadsUntilReachedMonth =
            cvUploadsPerMonth
                .entries
                .sortedByDescending { it.key }
                .takeWhile { it.key > firstReachedMonth }
                .sumOf { it.value }

        val numberContinuouslyReached = cumulativeUploadsUntilReachedMonth / cvLimit
        val adjustedUploads = cumulativeUploadsUntilReachedMonth - (numberContinuouslyReached * cvLimit)
        return adjustedUploads
    }

     val lastAdjusted = datesOfCvUploads.takeLast(adjustedUploads).groupBy { it.month }.mapValues { it.value.size }

     val finalUploads = if (adjustedUploads >= cvLimit) {
        cvLimit
    } else if (
        LocalDate.now().monthValue == 12) {
        adjustedUploads
    } else {
        0
    }

    fun calculateLimits(limit: Int): Int {
        var sum = 0

        for ((index, value) in cvUploadsPerMonth.values.withIndex()) {
            sum += value
            if (sum >= limit) {
                sum = 0 // Reset sum to continue from the next month
            }
        }

        return sum
    }


}