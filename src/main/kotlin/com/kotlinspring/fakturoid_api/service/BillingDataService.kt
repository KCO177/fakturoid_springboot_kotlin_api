package com.kotlinspring.fakturoid_api.service

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BillingDataService {

    val billingDataRaw = createApplicationData()
    val yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val billingData = mutableListOf<ApplicationData>()
    val removedData = mutableListOf<ApplicationData>()
    val billingDataGroupedByMonth = billingDataRaw.groupBy {
        it.applicationDate.format(yearMonthFormatter)
    }.mapValues { (_, applications) ->
        val distinctApplications = applications.distinctBy { app ->
            listOf(app.firstName, app.lastName, app.email, app.companyRegistrationNumber)
        }
        val removedApplications = applications - distinctApplications
        billingData.addAll(distinctApplications)
        removedData.addAll(removedApplications)

        billingData.forEach { println("billingData $it") }
        removedData.forEach { println("removedData $it") }
        println(billingData.size)
        println(removedData.size)
    }

    fun createApplicationData():List<ApplicationData> {
            return listOf(
                ApplicationData(LocalDateTime.now(), "Pepa", "Minka", "test@email.com", "TestJobTitle", "Test Company", "12345678", null),
                ApplicationData(LocalDateTime.now().plusMinutes(1), "Pepa", "Minka", "test@email.com", "TestJobTitle", "Test Company", "12345678", null),
                ApplicationData(LocalDateTime.now().plusMinutes(2), "Pepa", "Minka", "test@email.com", "TestJobTitle", "Test Company", "12345678", null),
                ApplicationData(LocalDateTime.now().minusMonths(1), "Pepa", "Minka", "test@email.com", "TestJobTitle", "Test Company", "12345678", null),
                ApplicationData(LocalDateTime.now().minusMonths(1).plusMinutes(1), "Pepa", "Minka", "test@email.com", "TestJobTitle", "Test Company", "12345678", null),
                )



        }

}
fun main() {
    val billingService = BillingDataService()
}



data class ApplicationData(
    val applicationDate: LocalDateTime,
    val firstName: String,
    val lastName: String,
    val email: String,
    val vacancyTitle: String,
    val companyLawName: String,
    val companyRegistrationNumber: String,
    val invoiceId : String?
)


