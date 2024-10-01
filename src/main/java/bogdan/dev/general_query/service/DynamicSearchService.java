package bogdan.dev.general_query.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DynamicSearchService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Map<String, Map<String, Integer>> tableMetadataCache = new HashMap<>();

    public DynamicSearchService() {
        // Constructor to initialize table metadata cache on service creation if needed.
    }

    public List<Map<String, Object>> executeDynamicQuery(String tableName, List<Map<String, Object>> filters, int page, int size) throws SQLException {
        Map<String, Integer> columnTypes = getTableMetadata(tableName);

        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("Table '" + tableName + "' does not exist or has no columns.");
        }

        // Build the SQL query
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);
        List<Object> params = new ArrayList<>();
        List<String> conditions = buildQueryConditions(columnTypes, filters, params);

        // Skip the table if no valid conditions were added
        if (conditions.isEmpty() && !filters.isEmpty()) {
            return Collections.emptyList();
        }

        // Add the WHERE clause if there are any conditions
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        // Add pagination
        sql.append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size);

        // Execute the query
        System.out.println(sql.toString());
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    private List<String> buildQueryConditions(Map<String, Integer> columnTypes, List<Map<String, Object>> filters, List<Object> params) {
        List<String> conditions = new ArrayList<>();

        for (Map<String, Object> filter : filters) {
            if (filter.containsKey("ALL_COLUMNS")) {
                // Handle wildcard search across all columns
                String wildcardValue = filter.get("ALL_COLUMNS").toString();
                List<String> wildcardConditions = new ArrayList<>();
                for (String column : columnTypes.keySet()) {
                    // Add condition only if it was successfully processed and is a valid column type for wildcard search
                    if (addConditionForColumn(column, wildcardValue, columnTypes.get(column), wildcardConditions, params, true)) {
                        continue;
                    }
                }
                if (!wildcardConditions.isEmpty()) {
                    conditions.add("(" + String.join(" OR ", wildcardConditions) + ")");
                }
            } else {
                // Handle specific column filters
                for (Map.Entry<String, Object> entry : filter.entrySet()) {
                    String column = entry.getKey();
                    Object value = entry.getValue();

                    // Skip non-existent columns for the current table
                    if (!columnTypes.containsKey(column)) {
                        continue;
                    }

                    // Add condition only if it was successfully processed
                    addConditionForColumn(column, value, columnTypes.get(column), conditions, params, false);
                }
            }
        }

        return conditions;
    }


    private boolean addConditionForColumn(String column, Object value, int columnType, List<String> conditions, List<Object> params, boolean isWildcardSearch) {
        try {
            if (columnType == java.sql.Types.VARCHAR || columnType == java.sql.Types.CHAR || columnType == java.sql.Types.LONGVARCHAR) {
                conditions.add(column + " LIKE ?");
                params.add("%" + value + "%");
            } else if (!isWildcardSearch) {  // Only add non-wildcard conditions for numeric, boolean, and date types
                if (columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.BIGINT) {
                    conditions.add(column + " = ?");
                    params.add(Long.parseLong(value.toString()));
                } else if (columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE || columnType == java.sql.Types.NUMERIC) {
                    conditions.add(column + " = ?");
                    params.add(Double.parseDouble(value.toString()));
                } else if (columnType == java.sql.Types.DATE) {
                    Date dateValue = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
                    conditions.add(column + " = ?");
                    params.add(new java.sql.Date(dateValue.getTime()));
                } else if (columnType == java.sql.Types.BOOLEAN || columnType == java.sql.Types.BIT) {
                    conditions.add(column + " = ?");
                    params.add(Boolean.parseBoolean(value.toString()));
                } else {
                    return false; // Unsupported column type
                }
            } else {
                return false; // Skip unsupported column types in wildcard search
            }
            return true; // Successfully added condition and parameter
        } catch (ParseException | NumberFormatException e) {
            // Skip invalid values for the column type
            return false;
        }
    }

    private Map<String, Integer> getTableMetadata(String tableName) throws SQLException {
        // Check the cache first
        if (tableMetadataCache.containsKey(tableName)) {
            return tableMetadataCache.get(tableName);
        }

        // Retrieve metadata from the database
        Map<String, Integer> columnTypes = new HashMap<>();
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
        try (ResultSet rsColumns = metaData.getColumns(null, null, tableName, null)) {
            while (rsColumns.next()) {
                String columnName = rsColumns.getString("COLUMN_NAME");
                int columnType = rsColumns.getInt("DATA_TYPE");
                columnTypes.put(columnName, columnType);
            }
        }

        // Cache the metadata
        tableMetadataCache.put(tableName, columnTypes);
        return columnTypes;
    }

    public List<String> getAllTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();

        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        }
        return tableNames;
    }

    public Map<String, List<String>> getAllTablesAndColumns() throws SQLException {
        Map<String, List<String>> tableColumnsMap = new HashMap<>();
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();

        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                List<String> columns = new ArrayList<>();

                try (ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null)) {
                    while (columnsResultSet.next()) {
                        String columnName = columnsResultSet.getString("COLUMN_NAME");
                        columns.add(columnName);
                    }
                }
                tableColumnsMap.put(tableName, columns);
            }
        }
        return tableColumnsMap;
    }
}
