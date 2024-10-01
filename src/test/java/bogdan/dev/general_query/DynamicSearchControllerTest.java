package bogdan.dev.general_query;

import bogdan.dev.general_query.api.DynamicSearchController;
import bogdan.dev.general_query.model.SearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
public class DynamicSearchControllerTest {

    @Autowired
    private DynamicSearchController dynamicSearchController;

    @Test
    public void testSearchEndpoint() throws Exception {
        MockMvc mockMvc = standaloneSetup(dynamicSearchController).build();

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setTables(Collections.singletonList("employees"));
        searchRequest.setFilters(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tables\": [\"employees\"], \"filters\": []}")
        ).andExpect(status().isOk());
    }
}