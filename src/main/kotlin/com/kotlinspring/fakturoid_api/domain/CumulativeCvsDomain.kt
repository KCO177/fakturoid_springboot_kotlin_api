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

    private val cumulativeUploadsUntilReachedMonth = if (firstReachedMonth != null) {
        cvUploadsPerMonth
            .entries
            .sortedByDescending { it.key }
            .takeWhile { it.key > firstReachedMonth }
            .sumOf { it.value }
    } else 0


    private val numberContinuouslyReached = cumulativeUploadsUntilReachedMonth / cvLimit
    private val adjustedUploads = cumulativeUploadsUntilReachedMonth - (numberContinuouslyReached * cvLimit)

     val lastAdjusted = datesOfCvUploads.takeLast(adjustedUploads).groupBy { it.month }.mapValues { it.value.size }

     val finalUploads = if (adjustedUploads >= cvLimit) {
        cvLimit
    } else if (
        LocalDate.now().monthValue == 12) {
        adjustedUploads
    } else {
        0
    }
}