package com.kotlinspring.fakturoid_api

import com.kotlinspring.fakturoid_api.controller.AuthorizationController
import com.kotlinspring.fakturoid_api.controller.InvoiceController
import com.kotlinspring.fakturoid_api.controller.SubjectController
import com.kotlinspring.fakturoid_api.domain.ClaimDataDomain
import com.kotlinspring.fakturoid_api.domain.TenantDomain
import com.kotlinspring.fakturoid_api.service.InvoiceService
import com.kotlinspring.fakturoid_api.service.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class InvoiceServiceTest {

    private val testUtils = TestUtils()

    @MockBean
    lateinit var authorizationControllerMockk: AuthorizationController

    @MockBean
    lateinit var invoiceControllerMockk: InvoiceController

    @MockBean
    lateinit var subjectControllerMockk: SubjectController

    @Autowired
    lateinit var invoiceService: InvoiceService

    // (subjectServiceMockk, invoiceControllerMockk, authorizationControllerMockk)



    @Test
    fun `when several subjects with different invoicing system are given sort and produce right invoices`() {

        //Given
        // subject 1 with buffered system directly invoiced
        val sub1applications = List(10) { LocalDate.now() }

        val sub1finClaimMockk = ClaimDataDomain(
            tenant = TenantDomain(
                companyRegistrationNumber = "1234567891",
                companyContactEmail = "tenant@email.com",
                companyLawName = "Test tenant Name"
            ),
            cvUploadedNumberMonth = sub1applications.count { it.month == LocalDate.now().month && it.year == LocalDate.now().year },
            datesOfCvUploads = sub1applications
        )

        val sub1subjectMockk = testUtils.createSubject(id = 1234567891, name = "Subject 1")



        //subject 2 with buffered system reached the limit

        //subject 3 with buffered system not reached the limit

        //subject 4 with direct invoicing system

        //subject 5 switching from buffered to direct invoicing system with residual applications not reached buffer limit

        //subject 6 with credit system reached the limit 50% of the applications

        //subject 7 with credit system reached the limit 75% of the applications

        //subject 8 with credit system reached the limit 100% of the applications and with new continual saver dealed

        //subject 9 with credit system reached the limit 100% of the applications and with no continual saver dealed switching to buffered system not reached the buffer limit

        //subject 10 with credit system reached the limit 100% of the applications and with no continual saver dealed switching to buffered system reached the buffer limit

        //subject 11 with credit system reached the limit 100% of the applications and with no continual saver dealed switching to buffered system reached the buffer limit after two months

        //subject 12 with credit system reached the limit 100% of the applications and with saver dealed after two months with residual applications not reached the buffer limit in the gap between savers

        //When
        val invoices = invoiceService.createInvoices()



        //Then
        assertNotNull(invoices)
        assertEquals(1, invoices.size)
        //assertEquals("invoice", bufferedInvoiceDomain.first().documentType)
        //assertEquals(10.0, bufferedInvoiceDomain.first().lines.sumOf { it.quantity })





    }




    }