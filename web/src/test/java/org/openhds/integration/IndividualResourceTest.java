package org.openhds.integration;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhds.dao.service.GenericDao;
import org.openhds.integration.util.WebContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(loader=WebContextLoader.class, locations={"/testContext.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup(value = "/individualResourceDb.xml", type = DatabaseOperation.REFRESH)
public class IndividualResourceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private GenericDao genericDao;

    private MockHttpSession session;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webApplicationContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();

        session = getMockHttpSession("admin", "test");
    }

    @Test
    public void testGetAllIndividualsXml() throws Exception {
        mockMvc.perform(get("/individuals").session(session)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML))
                .andExpect(xpath("/individuals").nodeCount(1))
                .andExpect(xpath("/individuals/individual[extId='flapper']/collectedBy/uuid").string("FieldWorker1"))
                .andExpect(xpath("/individuals/individual[extId='flapper']/uuid").string("FLAPPER"))
                .andExpect(xpath("/individuals/individual[extId='flapper']/firstName").string("first name 1"))
                .andExpect(xpath("/individuals/individual[extId='flapper']/lastName").string("last name 1"));
    }

    @Test
    public void testGetIndividualByExtIdXml() throws Exception {
        mockMvc.perform(get("/individuals/hippie").session(session)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML))
                .andExpect(xpath("/individual").nodeCount(1))
                .andExpect(xpath("/individual[extId='hippie']/collectedBy/uuid").string("FieldWorker1"))
                .andExpect(xpath("/individual[extId='hippie']/uuid").string("HIPPIE"))
                .andExpect(xpath("/individual[extId='hippie']/firstName").string("first name 2"))
                .andExpect(xpath("/individual[extId='hippie']/lastName").string("last name 2"));
    }

    @Test
    public void testGetOldIndividualXml() throws Exception {
        mockMvc.perform(get("/individuals/byDate")
                .param("since", "1920-01-01")
                .param("until", "1930-01-01")
                .session(session)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML))
                .andExpect(xpath("/individuals").nodeCount(1))
                .andExpect(xpath("/individuals/individual").nodeCount(1))
                .andExpect(xpath("/individuals/individual/uuid").string("FLAPPER"));
    }

    @Test
    public void testGetNewIndividualXml() throws Exception {
        mockMvc.perform(get("/individuals/byDate")
                .param("since", "1965-01-01")
                .param("until", "1975-01-01")
                .session(session)
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(MediaType.APPLICATION_XML))
                .andExpect(xpath("/individuals").nodeCount(1))
                .andExpect(xpath("/individuals/individual").nodeCount(1))
                .andExpect(xpath("/individuals/individual/uuid").string("HIPPIE"));
    }

    private MockHttpSession getMockHttpSession(String username, String password) throws Exception {
        return (MockHttpSession)mockMvc.perform(
                post("/loginProcess")
                        .param("j_username", username)
                        .param("j_password", password))
                .andReturn()
                .getRequest()
                .getSession();
    }
}
