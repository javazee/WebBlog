package main.api.response.postsResponse;

import lombok.Data;

@Data
public class AuthorOfComment {

    private int id;

    private String name;

    private String photo;
}