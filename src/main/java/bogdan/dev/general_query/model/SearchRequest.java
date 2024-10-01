package bogdan.dev.general_query.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
public class SearchRequest {
    private List<String> tables;
    private List<Map<String, Object>> filters;
}
