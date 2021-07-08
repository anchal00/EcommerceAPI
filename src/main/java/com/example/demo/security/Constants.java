package com.example.demo.security;

final public class Constants {

    public static final String SECRET_KEY = "xYbfg39gi9g3j9";
    public static final Long EXPIRATION_DELAY = 10_800_000L; // 3 hours from creation of JWTs
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_PATH = "/api/user/create";

}
