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

        // Collect conditions for the WHERE clause
        List<String> conditions = new ArrayList<>();

        if (!filters.isEmpty()) {
            for (Map<String, Object> filter : filters) {
                if (filter.containsKey("ALL_COLUMNS")) {
                    // Wildcard search across all columns
                    String wildcardValue = filter.get("ALL_COLUMNS").toString();
                    List<String> wildcardConditions = new ArrayList<>();
                    for (String column : columnTypes.keySet()) {
                        int columnType = columnTypes.get(column);

                        // Use LIKE for string columns, = for numeric and other types
                        if (columnType == java.sql.Types.VARCHAR || columnType == java.sql.Types.CHAR || columnType == java.sql.Types.LONGVARCHAR) {
                            wildcardConditions.add(column + " LIKE ?");
                            params.add("%" + wildcardValue + "%");
                        } else if (columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.BIGINT || columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE || columnType == java.sql.Types.NUMERIC) {
                            try {
                                Number numberValue = Double.parseDouble(wildcardValue);
                                wildcardConditions.add(column + " = ?");
                                params.add(numberValue);
                            } catch (NumberFormatException e) {
                                // Ignore columns that cannot be matched as numbers
                            }
                        } else if (columnType == java.sql.Types.DATE) {
                            try {
                                Date dateValue = new SimpleDateFormat("yyyy-MM-dd").parse(wildcardValue);
                                wildcardConditions.add(column + " = ?");
                                params.add(new java.sql.Date(dateValue.getTime()));
                            } catch (ParseException e) {
                                // Ignore invalid date formats
                            }
                        } else if (columnType == java.sql.Types.BOOLEAN) {
                            wildcardConditions.add(column + " = ?");
                            params.add(Boolean.parseBoolean(wildcardValue));
                        }
                    }
                    if (!wildcardConditions.isEmpty()) {
                        conditions.add("(" + String.join(" OR ", wildcardConditions) + ")");
                    }
                } else {
                    // Specific column filters
                    for (Map.Entry<String, Object> entry : filter.entrySet()) {
                        String column = entry.getKey();
                        Object value = entry.getValue();

                        // **Check if the column exists in the current table**
                        if (!columnTypes.containsKey(column)) {
                            // Skip the filter if the column doesn't exist in the table
                            continue;
                        }

                        int columnType = columnTypes.get(column);
                        if (columnType == java.sql.Types.VARCHAR || columnType == java.sql.Types.CHAR || columnType == java.sql.Types.LONGVARCHAR) {
                            conditions.add(column + " LIKE ?");
                            params.add("%" + value + "%");
                        } else if (columnType == java.sql.Types.INTEGER || columnType == java.sql.Types.BIGINT) {
                            conditions.add(column + " = ?");
                            params.add(Long.parseLong(value.toString()));
                        } else if (columnType == java.sql.Types.FLOAT || columnType == java.sql.Types.DOUBLE || columnType == java.sql.Types.NUMERIC) {
                            conditions.add(column + " = ?");
                            params.add(Double.parseDouble(value.toString()));
                        } else if (columnType == java.sql.Types.DATE) {
                            try {
                                Date dateValue = new SimpleDateFormat("yyyy-MM-dd").parse(value.toString());
                                conditions.add(column + " = ?");
                                params.add(new java.sql.Date(dateValue.getTime()));
                            } catch (ParseException e) {
                                throw new IllegalArgumentException("Invalid date format for column '" + column + "': " + value);
                            }
                        } else if (columnType == java.sql.Types.BOOLEAN) {
                            conditions.add(column + " = ?");
                            params.add(Boolean.parseBoolean(value.toString()));
                        }
                    }
                }
            }
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
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    private Map<String, Integer> getTableMetadata(String tableName) throws SQLException {
        // Check the cache first
        if (tableMetadataCache.containsKey(tableName)) {
            return tableMetadataCache.get(tableName);
        }

        // If not cached, retrieve metadata from the database
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

    public Map<String, List<String>> getAllTablesAndColumns(boolean onlyTables) throws SQLException {
        Map<String, List<String>> tableColumnsMap = new HashMap<>();
        DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();

        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                if (onlyTables) {
                    // If only table names are requested, add an empty list for columns
                    tableColumnsMap.put(tableName, new ArrayList<>());
                } else {
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
        }
        return tableColumnsMap;
    }
}
