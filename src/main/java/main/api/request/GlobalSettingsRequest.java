package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GlobalSettingsRequest {

    @JsonProperty("MULTIUSER_MODE")
    private boolean multiUsed;

    @JsonProperty("POST_PREMODERATION")
    private boolean preModerated;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean isPublic;
}
