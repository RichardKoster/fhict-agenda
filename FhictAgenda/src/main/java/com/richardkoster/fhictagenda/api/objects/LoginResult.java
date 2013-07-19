package com.richardkoster.fhictagenda.api.objects;

import com.google.gson.annotations.SerializedName;

public class LoginResult {
    @SerializedName("access_token")
    public String accessToken;
    public User user;
}
