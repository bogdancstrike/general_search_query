package bogdan.dev.general_query.api;

import bogdan.dev.general_query.model.SearchRequest;
import bogdan.dev.general_query.service.DynamicSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DynamicSearchController {

    @Autowired
    private DynamicSearchService dynamicSearchService;

    @PostMapping("/search")
    public Map<String, List<Map<String, Object>>> search(
            @RequestBody SearchRequest searchRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws SQLException {

        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        for (String table : searchRequest.getTables()) {
            List<Map<String, Object>> tableData = dynamicSearchService.executeDynamicQuery(table, searchRequest.getFilters(), page, size);
            result.put(table, tableData);
        }

        return result;
    }

    @GetMapping("/metadata")
    public Map<String, List<String>> getAllTablesAndColumns(@RequestParam(defaultValue = "false") boolean only_tables) throws SQLException {
        return dynamicSearchService.getAllTablesAndColumns(only_tables);
    }

}
