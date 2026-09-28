package com.overcode.security;

import java.util.HashMap;
import java.util.Map;

public record AuthClaims(Long uid, String username) {

    public Map<String, Object> toMap() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", this.uid);
        claims.put("username", this.username);
        return claims;
    }
}
