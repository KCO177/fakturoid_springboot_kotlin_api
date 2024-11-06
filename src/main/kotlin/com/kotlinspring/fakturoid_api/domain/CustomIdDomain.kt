package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class CustomIdDomain (id : String)
{
    /** template = "YYYY-MM-NNN" **/
    init {
        require(Regex("""\d{4}-\d{2}-\d{3}""").matches(id)) { "Invalid Invoice custom ID format ${id}" }
    }

    val year = getInvoiceIdValues(id)[0]
    val month = getInvoiceIdValues(id)[1]
    val number = getInvoiceIdValues(id)[2]

    fun getInvoiceIdValues(id: String) : List<Int> {
        val year = id.substring(0, 4).toInt()
        val month = id.substring(5, 7).toInt()
        val number = id.substring(8, 11).toInt()
        return listOf(year, month, number)
    }


    companion object {
     fun getCustomId(invoices: List<InvoiceDomain>): CustomIdDomain {
            val lastCustomId = invoices
                .map { it.customId }
                .maxWithOrNull(compareBy({ it.year }, { it.month }, { it.number }))
                ?: throw IllegalArgumentException("No invoices found")

            val newCustomId =  CustomIdDomain.createNewNumber(lastCustomId)
         return  newCustomId
    }

        private fun createNewNumber(lastCustomId: CustomIdDomain): CustomIdDomain {
            val year = lastCustomId.year
            val month = lastCustomId.month
            val number = lastCustomId.number
            val newYear = if (year == LocalDate.now().year) year else LocalDate.now().year
            val newMonth = if (month == LocalDate.now().month.value) month else LocalDate.now().month.value
            val newNumber = if (newYear != year || newMonth != month) 1 else number + 1

            return CustomIdDomain("$newYear-$newMonth-$newNumber")
        }

        }
    }


