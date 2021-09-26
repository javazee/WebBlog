package main.api.request;

import lombok.Data;

@Data
public class EditProfileRequest<T> {

    private T photo;

    private String name;

    private String email;

    private String password;

    private int removePhoto;
}
