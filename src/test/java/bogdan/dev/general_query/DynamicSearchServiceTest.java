package bogdan.dev.general_query;

import bogdan.dev.general_query.service.DynamicSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DynamicSearchServiceTest {

    @Autowired
    private DynamicSearchService dynamicSearchService;

    @Test
    public void testExecuteDynamicQuerySimpleFilter() throws SQLException {
        List<Map<String, Object>> result = dynamicSearchService.executeDynamicQuery("employees", Collections.singletonList(
                Collections.singletonMap("employee_name", "Anna")
        ), 0, 10);
        assertFalse(result.isEmpty(), "The result should not be empty for employee_name filter.");
    }

    @Test
    public void testExecuteDynamicQueryWildcardFilter() throws SQLException {
        List<Map<String, Object>> result = dynamicSearchService.executeDynamicQuery("employees", Collections.singletonList(
                Collections.singletonMap("ALL_COLUMNS", "Anna")
        ), 0, 10);
        assertFalse(result.isEmpty(), "The result should not be empty for ALL_COLUMNS wildcard filter.");
    }

    @Test
    public void testExecuteDynamicQueryMultipleTables() throws SQLException {
        List<Map<String, Object>> resultEmployees = dynamicSearchService.executeDynamicQuery("employees", Collections.singletonList(
                Collections.singletonMap("id", 1)
        ), 0, 10);

        List<Map<String, Object>> resultDepartments = dynamicSearchService.executeDynamicQuery("departments", Collections.singletonList(
                Collections.singletonMap("id", 1)
        ), 0, 10);

        assertFalse(resultEmployees.isEmpty(), "The employees result should not be empty.");
        assertFalse(resultDepartments.isEmpty(), "The departments result should not be empty.");
    }

    @Test
    public void testExecuteDynamicQueryWithPagination() throws SQLException {
        List<Map<String, Object>> result = dynamicSearchService.executeDynamicQuery("departments", Collections.emptyList(), 1, 2);
        assertNotNull(result, "The result should not be null for pagination.");
    }

    @Test
    public void testExecuteDynamicQueryBooleanFilter() throws SQLException {
        List<Map<String, Object>> result = dynamicSearchService.executeDynamicQuery("employees", Collections.singletonList(
                Collections.singletonMap("is_permanent", true)
        ), 0, 10);
        assertFalse(result.isEmpty(), "The result should not be empty for boolean filter.");
    }

    @Test
    public void testExecuteDynamicQueryDateFilter() throws SQLException {
        List<Map<String, Object>> result = dynamicSearchService.executeDynamicQuery("departments", Collections.singletonList(
                Collections.singletonMap("created_date", "2020-01-01")
        ), 0, 10);
        assertFalse(result.isEmpty(), "The result should not be empty for date filter.");
    }

    @Test
    public void testGetAllTableNames() throws SQLException {
        List<String> tableNames = dynamicSearchService.getAllTableNames();
        assertNotNull(tableNames, "The list of table names should not be null.");
        assertFalse(tableNames.isEmpty(), "The list of table names should not be empty.");
    }

    @Test
    public void testGetAllTablesAndColumns() throws SQLException {
        Map<String, List<String>> tablesAndColumns = dynamicSearchService.getAllTablesAndColumns();
        assertNotNull(tablesAndColumns, "The map of tables and columns should not be null.");
        assertFalse(tablesAndColumns.isEmpty(), "The map of tables and columns should not be empty.");
    }
}
