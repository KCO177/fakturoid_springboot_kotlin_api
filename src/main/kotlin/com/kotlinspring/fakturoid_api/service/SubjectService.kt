package com.kotlinspring.fakturoid_api.service

import com.kotlinspring.fakturoid_api.controller.SubjectController
import com.kotlinspring.fakturoid_api.domain.SubjectDomain


class SubjectService (
    private val subjectController: SubjectController
)

{
    fun findOrCreateTenant(bearerToken: String, subject: SubjectDomain) : Int {
        val tenants : List<SubjectDomain> =  subjectController.getSubject(bearerToken)
        val tenantId = tenants.filter { tenant -> subject.name == tenant.name }.mapNotNull { it.id }.firstOrNull()
        if (tenantId != null) {
            return tenantId
        } else {
            return requireNotNull( subjectController.createSubject(bearerToken, subject) ) { "Subject ${subject.id} ${subject.CIN} could not be created" }
        }
    }
}