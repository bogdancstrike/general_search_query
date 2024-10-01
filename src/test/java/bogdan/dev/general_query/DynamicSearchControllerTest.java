package bogdan.dev.general_query;

import bogdan.dev.general_query.api.DynamicSearchController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
public class DynamicSearchControllerTest {

    @Autowired
    private DynamicSearchController dynamicSearchController;

    private MockMvc mockMvc;

    @Test
    public void testSearchEndpointSimpleFilter() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tables\": [\"employees\"], \"filters\": [{\"employee_name\": \"Anna\"}]}")
        ).andExpect(status().isOk());
    }

    @Test
    public void testSearchEndpointWildcardFilter() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tables\": [\"departments\", \"employees\"], \"filters\": [{\"ALL_COLUMNS\": \"Anna\"}]}")
        ).andExpect(status().isOk());
    }

    @Test
    public void testSearchEndpointSpecificFilter() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tables\": [\"departments\", \"employees\"], \"filters\": [{\"id\": 1}]}")
        ).andExpect(status().isOk());
    }

    @Test
    public void testSearchEndpointMultipleFilters() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tables\": [\"departments\", \"employees\"], \"filters\": [{\"ALL_COLUMNS\": \"Anna\"}, {\"id\": 1}]}")
        ).andExpect(status().isOk());
    }

    @Test
    public void testSearchEndpointWithPagination() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/search?page=0&size=3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tables\": [\"departments\"], \"filters\": [{\"created_date\": \"2020-01-01\"}]}")
        ).andExpect(status().isOk());
    }

    @Test
    public void testSearchEndpointBooleanFilter() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tables\": [\"employees\"], \"filters\": [{\"is_permanent\": true}]}")
        ).andExpect(status().isOk());
    }

    @Test
    public void testSearchEndpointComplexFilters() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tables\": [\"employees\", \"departments\"], \"filters\": [{\"hire_date\": \"2020-01-01\"}, {\"employee_name\": \"Employee\"}, {\"is_permanent\": true}, {\"department_id\": 1}]}")
        ).andExpect(status().isOk());
    }

    @Test
    public void testMetadataEndpointWithOnlyTables() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/metadata?only_tables=true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testMetadataEndpointWithColumns() throws Exception {
        mockMvc = standaloneSetup(dynamicSearchController).build();

        mockMvc.perform(MockMvcRequestBuilders.get("/metadata")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
