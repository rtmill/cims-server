package org.openhds.integration.bioko;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.dao.service.GenericDao;
import org.openhds.domain.model.Individual;
import org.openhds.domain.model.Location;
import org.openhds.domain.model.Membership;
import org.openhds.domain.model.Relationship;
import org.openhds.domain.model.Residency;
import org.openhds.domain.model.SocialGroup;
import org.openhds.integration.AbstractResourceTest;
import org.openhds.integration.util.WebContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.server.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader = WebContextLoader.class, locations = { "/testContext.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup(value = "/individualFormResourceDb.xml", type = DatabaseOperation.REFRESH)
@Ignore
public class IndividualFormResourceTest extends AbstractResourceTest {

    private static final String A_DATE = "2000-01-01T00:00:00-05:00";
    private static final String FORMATTED_DATE = "2013-06-13";
    private static final String FORMATTED_DATETIME = "2013-06-13 12:12:12";
    
    private static final String HEAD_OF_HOUSEHOLD_FORM_XML = "<individualForm>"
            + "<processed_by_mirth>false</processed_by_mirth>"
            + "<field_worker_ext_id>FWEK1D</field_worker_ext_id>" + "<collection_date_time>"
            + A_DATE
            + "</collection_date_time>"
            + "<household_ext_id>newHouse_id</household_ext_id>"
            + "<individual_ext_id>1234567890aa</individual_ext_id>"
            + "<individual_first_name>Test HoH First</individual_first_name>"
            + "<individual_last_name>Test HoH Last</individual_last_name>"
            + "<individual_other_names>Test HoH Other</individual_other_names>"
            + "<individual_age>100</individual_age>"
            + "<individual_age_units>years</individual_age_units>"
            + "<individual_date_of_birth>"
            + A_DATE
            + "</individual_date_of_birth>"
            + "<individual_gender>M</individual_gender>"
            + "<individual_relationship_to_head_of_household>1</individual_relationship_to_head_of_household>"
            + "<individual_phone_number>12345678890</individual_phone_number>"
            + "<individual_other_phone_number>0987654321</individual_other_phone_number>"
            + "<individual_language_preference>English</individual_language_preference>"
            + "<individual_point_of_contact_name></individual_point_of_contact_name>"
            + "<individual_point_of_contact_phone_number></individual_point_of_contact_phone_number>"
            + "<individual_dip>12345</individual_dip>"
            + "<individual_member_status>permanent</individual_member_status>"
            + "</individualForm>";
    private static final String MEMBER_OF_HOUSEHOLD_FORM_XML = "<individualForm>"
            + "<processed_by_mirth>false</processed_by_mirth>"
            + "<field_worker_ext_id>FWEK1D</field_worker_ext_id>" + "<collection_date_time>"
            + A_DATE
            + "</collection_date_time>"
            + "<household_ext_id>existing_id</household_ext_id>"
            + "<individual_ext_id>1234567890bb</individual_ext_id>"
            + "<individual_first_name>Test Member First</individual_first_name>"
            + "<individual_last_name>Test Member Last</individual_last_name>"
            + "<individual_other_names>Test Member Other</individual_other_names>"
            + "<individual_age>100</individual_age>"
            + "<individual_age_units>years</individual_age_units>"
            + "<individual_date_of_birth>"
            + A_DATE
            + "</individual_date_of_birth>"
            + "<individual_gender>F</individual_gender>"
            + "<individual_relationship_to_head_of_household>2</individual_relationship_to_head_of_household>"
            + "<individual_phone_number>12345678890</individual_phone_number>"
            + "<individual_other_phone_number>0987654321</individual_other_phone_number>"
            + "<individual_language_preference>English</individual_language_preference>"
            + "<individual_point_of_contact_name></individual_point_of_contact_name>"
            + "<individual_point_of_contact_phone_number></individual_point_of_contact_phone_number>"
            + "<individual_dip>12345</individual_dip>"
            + "<individual_member_status>permanent</individual_member_status>"
            + "</individualForm>";
    private static final String INDIVIDUAL_FORM_INCOMPLETE = "<individualForm>"
            + "<individual_first_name>Test First</individual_first_name>"
            + "<individual_last_name>Test Last</individual_last_name>"
            + "<individual_other_names>Test Other</individual_other_names>"
            + "<individual_age>10</individual_age>"
            + "<individual_age_units>years</individual_age_units>" + "</individualForm>";
    private static final String MEMBER_OF_HOUSEHOLD_DATE_FORMATTED = "<individualForm>"
            + "<processed_by_mirth>false</processed_by_mirth>"
            + "<field_worker_ext_id>FWEK1D</field_worker_ext_id>" + "<collection_date_time>"
            + FORMATTED_DATETIME
            + "</collection_date_time>"
            + "<household_ext_id>existing_id</household_ext_id>"
            + "<individual_ext_id>1234599899bb</individual_ext_id>"
            + "<individual_first_name>DATE Test Member First</individual_first_name>"
            + "<individual_last_name>DATE Test Member Last</individual_last_name>"
            + "<individual_other_names>DATE Test Member Other</individual_other_names>"
            + "<individual_age>100</individual_age>"
            + "<individual_age_units>years</individual_age_units>"
            + "<individual_date_of_birth>"
            + FORMATTED_DATE
            + "</individual_date_of_birth>"
            + "<individual_gender>F</individual_gender>"
            + "<individual_relationship_to_head_of_household>2</individual_relationship_to_head_of_household>"
            + "<individual_phone_number>12345678890</individual_phone_number>"
            + "<individual_other_phone_number>0987654321</individual_other_phone_number>"
            + "<individual_language_preference>English</individual_language_preference>"
            + "<individual_point_of_contact_name></individual_point_of_contact_name>"
            + "<individual_point_of_contact_phone_number></individual_point_of_contact_phone_number>"
            + "<individual_dip>12345</individual_dip>"
            + "<individual_member_status>permanent</individual_member_status>"
            + "</individualForm>";

    @Autowired
    private GenericDao genericDao;

    private MockHttpSession session;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = buildMockMvc();
        session = getMockHttpSession("admin", "test", mockMvc);
    }

    @Test
    public void testPostHeadOfHouseholdFormXml() throws Exception {
        mockMvc.perform(
                post("/individualForm").session(session).accept(MediaType.APPLICATION_XML)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(HEAD_OF_HOUSEHOLD_FORM_XML.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML));

        verifyEntityCrud("1234567890aa", "newHouse_id", "1234567890aa", "1");
    }

    @Test
    public void testPosMemberOfHouseholdFormXml() throws Exception {
        mockMvc.perform(
                post("/individualForm").session(session).accept(MediaType.APPLICATION_XML)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(MEMBER_OF_HOUSEHOLD_FORM_XML.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML));

        verifyEntityCrud("1234567890bb", "existing_id", "NBAS1I", "2");
    }

    @Test
    public void testRepeatPostHeadOfHouseholdFormXml() throws Exception {
        mockMvc.perform(
                post("/individualForm").session(session).accept(MediaType.APPLICATION_XML)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(HEAD_OF_HOUSEHOLD_FORM_XML.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML));

        mockMvc.perform(
                post("/individualForm").session(session).accept(MediaType.APPLICATION_XML)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(HEAD_OF_HOUSEHOLD_FORM_XML.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML));

        verifyEntityCrud("1234567890aa", "newHouse_id", "1234567890aa", "1");
    }

    @Test
    public void testRepeatPostMemberOfHouseholdFormXml() throws Exception {
        mockMvc.perform(
                post("/individualForm").session(session).accept(MediaType.APPLICATION_XML)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(MEMBER_OF_HOUSEHOLD_FORM_XML.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML));

        mockMvc.perform(
                post("/individualForm").session(session).accept(MediaType.APPLICATION_XML)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(MEMBER_OF_HOUSEHOLD_FORM_XML.getBytes()))
                .andExpect(status().isCreated())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML));

        verifyEntityCrud("1234567890bb", "existing_id", "NBAS1I", "2");
    }

    @Test
    public void testPostIncompleteIndividualFormXml() throws Exception {
        mockMvc.perform(
                post("/individualForm").session(session).accept(MediaType.APPLICATION_XML)
                        .contentType(MediaType.APPLICATION_XML)
                        .body(INDIVIDUAL_FORM_INCOMPLETE.getBytes()))
                .andExpect(status().isBadRequest())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML));
    }
    
//    @Test
//    public void testPostMemberOfHouseholdFormattedDate() throws Exception {
//    	
//        mockMvc.perform(
//                post("/individualForm").session(session).accept(MediaType.APPLICATION_XML)
//                        .contentType(MediaType.APPLICATION_XML)
//                        .body(MEMBER_OF_HOUSEHOLD_DATE_FORMATTED.getBytes()))
//                .andExpect(status().isCreated())
//                .andExpect(content().mimeType(MediaType.APPLICATION_XML));
//
//        verifyEntityCrud("1234599899bb", "existing_id", "NBAS1I", "2");
//    	
//    }

    private void verifyEntityCrud(String individualExtId, String householdExtId, String headExtId,
            String membershipType) {

        // individual exists
        Individual individual = genericDao.findByProperty(Individual.class, "extId",
                individualExtId);
        assertNotNull(individual);

        // location exists
        Location location = genericDao.findByProperty(Location.class, "extId", householdExtId);
        assertNotNull(location);

        // residency at location
        Residency residency = null;
        for (Residency r : individual.getAllResidencies()) {
            if (r.getLocation().equals(location)) {
                residency = r;
                break;
            }
        }
        assertNotNull(residency);

        // socialGroup exists
        SocialGroup socialGroup = genericDao.findByProperty(SocialGroup.class, "extId",
                householdExtId);
        assertNotNull(socialGroup);

        // membership in social group
        Membership membership = null;
        for (Membership m : individual.getAllMemberships()) {
            if (m.getSocialGroup().equals(socialGroup)) {
                membership = m;
                break;
            }
        }
        assertNotNull(membership);
        assertEquals(membershipType, membership.getbIsToA());

        // head of household exists
        Individual head = genericDao.findByProperty(Individual.class, "extId", headExtId);
        assertNotNull(head);

        // relationship to head
        // membership in social group
        Relationship relationship = null;
        for (Relationship r : individual.getAllRelationships1()) {
            if (r.getIndividualB().equals(head)) {
                relationship = r;
                break;
            }
        }
        assertNotNull(relationship);
        assertEquals(membershipType, relationship.getaIsToB());
    }

}
