package com.kotlinspring.fakturoid_api.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class SubjectDomain (
    val id: Int? = null,
    val name: String,
    val email: String?,
    @JsonProperty("registration_no")
    val CIN : String?,
) {

    companion object {

        fun mapTenantToSubjectDomain(tenant: TenantDomain): SubjectDomain {

            return SubjectDomain(
                name = requireNotNull( tenant.companyLawName ) { "Tenant ${tenant.companyRegistrationNumber} ${tenant.companyContactEmail} ${tenant.companyLawName} could not be created in Fakturoid missing name" },
                email = requireNotNull( tenant.companyContactEmail ) { "Tenant ${tenant.companyRegistrationNumber} ${tenant.companyLawName} could not be created in Fakturoid missing email" },
                CIN = requireNotNull( tenant.companyRegistrationNumber ) { "Tenant  ${tenant.companyContactEmail} ${tenant.companyLawName} could not be created in Fakturoid missing CIN" },
            )
        }

    }
}


