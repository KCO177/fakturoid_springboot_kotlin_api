package com.kotlinspring.fakturoid_api.service

import com.kotlinspring.fakturoid_api.controller.SubjectController
import com.kotlinspring.fakturoid_api.domain.SubjectDomain
import com.kotlinspring.fakturoid_api.domain.TenantDomain
import org.springframework.stereotype.Service

@Service
class SubjectService {
    fun findOrCreateTenant(bearerToken: String, tenants: List<TenantDomain>) : List<SubjectDomain> {
        println(bearerToken)
        val subjects : List<SubjectDomain> =  SubjectController().getSubject(bearerToken)

        val existingSubjects: List<SubjectDomain> = tenants.mapNotNull { tenant ->
            subjects.find { subject -> subject.registration_no == tenant.companyRegistrationNumber }
        }

        val newSubjects: List<SubjectDomain> = tenants.filter { tenant ->
            existingSubjects.none { subject -> subject.registration_no == tenant.companyRegistrationNumber }
        }.map { tenant ->
            requireNotNull(SubjectController().createSubject(bearerToken, SubjectDomain.mapTenantToSubjectDomain(tenant))) {
                "Subject for tenant ${tenant.companyRegistrationNumber} could not be created"
            }
        }

        return existingSubjects + newSubjects

    }
}