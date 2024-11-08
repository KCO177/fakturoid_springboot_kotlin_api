package com.kotlinspring.fakturoid_api.domain


class CustomIdDomain (id : String) {
    /** template = "YYYY-MM-NNN" **/
    init {
        require(Regex("""\d{4}-\d{2}-\d{3}""").matches(id)) { "Invalid Invoice custom ID format ${id}" }
    }

    val year = getInvoiceIdValues(id)[0]
    val month = getInvoiceIdValues(id)[1]
    val number = getInvoiceIdValues(id)[2]

    fun getInvoiceIdValues(id: String): List<Int> {
        val year = id.substring(0, 4).toInt()
        val month = id.substring(5, 7).toInt()
        val number = id.substring(8, 11).toInt()
        return listOf(year, month, number)
    }
}
