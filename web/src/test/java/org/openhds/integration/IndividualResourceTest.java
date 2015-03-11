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
                .andExpect(xpath("/individuals/individual[extId='old']/collectedBy/uuid").string("FieldWorker1"))
                .andExpect(xpath("/individuals/individual[extId='old']/uuid").string("OLD"))
                .andExpect(xpath("/individuals/individual[extId='old']/firstName").string("first name 1"))
                .andExpect(xpath("/individuals/individual[extId='old']/lastName").string("last name 1"));
    }

    private MockHttpSession getMockHttpSession(String username, String password) throws Exception {
        return (MockHttpSession)mockMvc.perform(post("/loginProcess")
                        .param("j_username", username)
                        .param("j_password", password)
        ).andReturn()
                .getRequest()
                .getSession();
    }
}
