package opsflow_backend.auth;

public class LoginResponse {

    private String token;
    private String tokenType;

    public LoginResponse(String token) {
        this.token = token;
        this.tokenType = "Bearer";
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }
}