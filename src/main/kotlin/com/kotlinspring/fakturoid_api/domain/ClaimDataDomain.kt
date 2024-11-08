package com.kotlinspring.fakturoid_api.domain

import java.time.LocalDate

class ClaimDataDomain (
    val tenant: TenantDomain,
    val cvUploadedNumberMonth : Int,
    val datesOfCvUploads: List<LocalDate>
) {

    companion object {
        fun getInvoiceData(dbOutput : List<DjDbOutput> ): List<ClaimDataDomain> {
            return dbOutput.map { tenant ->
                ClaimDataDomain(
                    tenant = TenantDomain(
                        companyRegistrationNumber = tenant.companyRegistrationNumber,
                        companyContactEmail = tenant.companyContactEmail,
                        companyLawName = tenant.companyLawName
                    ),
                    cvUploadedNumberMonth = tenant.cvQuantityMonth,
                    datesOfCvUploads = tenant.datesOfCvUpload
                )
            }
        }
    }
}

