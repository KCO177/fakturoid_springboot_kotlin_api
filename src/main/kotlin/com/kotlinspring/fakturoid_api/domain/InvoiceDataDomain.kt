package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class InvoiceDataDomain (
    val dateFrom: LocalDate,
    val dateTo: LocalDate,
    val tenant: TenantDomain,
    val cvUploadedNumberMonth : Int,
    val cvUploadedNumberYear : Int,
    val datesOfCvUploads: List<LocalDate>
) {
    companion object {
        fun getInvoiceData(dbOutput : List<DjDbOutput> ): List<InvoiceDataDomain> {
            return dbOutput.map { tenant ->
                InvoiceDataDomain(
                    dateFrom = LocalDate.now(),
                    dateTo = LocalDate.now(),
                    tenant = TenantDomain(
                        companyRegistrationNumber = tenant.companyRegistrationNumber,
                        companyContactEmail = tenant.companyContactEmail,
                        companyLawName = tenant.companyLawName
                    ),
                    cvUploadedNumberMonth = tenant.cvQuantityMonth,
                    cvUploadedNumberYear = tenant.cvQuantityYear,
                    datesOfCvUploads = tenant.datesOfCvUpload
                )
            }
        }
    }
}

