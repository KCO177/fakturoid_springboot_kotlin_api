package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate
import java.time.Month

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

    val adjustedUploads =  if (firstReachedMonth != null) {
        calculateLimitisIfReached()
    } else { calculateLimits(cvLimit) }

     val lastAdjusted = datesOfCvUploads.takeLast(adjustedUploads).groupBy { it.month }.mapValues { it.value.size }

     val finalUploads = if (adjustedUploads >= cvLimit) {
        adjustedUploads
    } else if (
        LocalDate.now().monthValue == 12) {
        adjustedUploads
    } else {
        0
    }

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


    fun calculateLimits(limit: Int): Int {
        var sum = 0
        var i = 0
        val monthlyUploadsSorted = cvUploadsPerMonth.toSortedMap(compareBy { it })
        for ((index, value) in monthlyUploadsSorted.values.withIndex()) {

            sum += value
            if (sum >= limit && monthlyUploadsSorted.keys.elementAt(index).monthValue != LocalDate.now().monthValue) {
                sum = 0
                }
            }

        return sum
    }

}