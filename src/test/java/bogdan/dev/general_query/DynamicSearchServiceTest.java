package bogdan.dev.general_query;

import bogdan.dev.general_query.service.DynamicSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class DynamicSearchServiceTest {

    @Autowired
    private DynamicSearchService dynamicSearchService;

    @Test
    public void testExecuteDynamicQuery() throws SQLException {
        List<Map<String, Object>> result = dynamicSearchService.executeDynamicQuery("employees", Collections.emptyList(), 0, 10);
        assertFalse(result.isEmpty(), "The result should not be empty");
    }
}
