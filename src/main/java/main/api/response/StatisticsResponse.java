package main.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsResponse {

    private int postsCount;

    private int likesCount;

    private int dislikesCount;

    private int viewsCount;

    private long firstPublication;
}
