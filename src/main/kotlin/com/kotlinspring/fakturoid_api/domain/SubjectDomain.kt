package com.kotlinspring.fakturoid_api.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.kotlinspring.fakturoid_api.service.SubjectService

@JsonIgnoreProperties(ignoreUnknown = true)
class SubjectDomain (
    val id: Int? = null,
    @JsonProperty("custom_id")
    val customId: String? = null,
    val name: String,
    @JsonProperty("full_name")
    val fullName : String?,
    val street: String?,
    val city: String?,
    val zip: String?,
    @JsonProperty("country")
    val countryCode: String?,
    @JsonProperty("registration_no")
    val CIN : String?,
    val vat_no: String?, //DIC??
    val variable_symbol: String? //Fixed variable symbol (used for all invoices for this client instead of invoice number)
) {
    companion object {

        fun mapTenantToSubjectDomain(tenant: TenantDomain): SubjectDomain {

            return SubjectDomain(
                id = null,
                name = requireNotNull( tenant.companyLawName ) { "Tenant ${tenant.companyRegistrationNumber} ${tenant.companyContactEmail} ${tenant.companyLawName} could not be created in Fakturoid" },
                fullName = tenant.companyContactEmail, //TODO need to check if default values
                street = null,
                city = null,
                zip = null,
                countryCode = null,
                CIN = tenant.companyRegistrationNumber,
                vat_no = null, //countryCode + tenant.companyRegistrationNumber,
                variable_symbol = null
            )
        }

        fun getSubjectId(tenant: TenantDomain, subjectService: SubjectService, bearerToken: String): Int {
            val subject = mapTenantToSubjectDomain(tenant)
            val subjectId = requireNotNull( subjectService.findOrCreateTenant(bearerToken, subject).id) { "Subject ${subject.id} ${subject.CIN} could not be created" }
            return subjectId
        }
    }
}


