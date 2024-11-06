package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class InvoiceDataDomain (
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val tenant: TenantDomain,
    val cvUploadedNumber : Int,
) {
    companion object {
        fun getInvoiceData(dbOutput : List<DjDbOutput> ): List<InvoiceDataDomain> {
            return dbOutput.map { tenant ->
                InvoiceDataDomain(
                    dateFrom = LocalDate.now(),
                    dateTo = LocalDate.now(),
                    tenant = TenantDomain(
                        companyName = tenant.companyName,
                        companyRegistrationNumber = tenant.companyRegistrationNumber,
                        companyContactEmail = tenant.companyContactEmail,
                        companyLawName = tenant.companyLawName
                    ),
                    cvUploadedNumber = tenant.cvQuantity
                )
            }
        }
    }
}

