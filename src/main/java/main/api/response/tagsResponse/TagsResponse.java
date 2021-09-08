package main.api.response.tagsResponse;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TagsResponse {

    List<TagResponse> tags = new ArrayList<>();
}
