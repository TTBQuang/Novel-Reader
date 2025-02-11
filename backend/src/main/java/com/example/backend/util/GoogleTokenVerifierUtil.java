package com.example.backend.util;

import com.example.backend.dto.auth.UserGoogleProfile;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifierUtil {
    @Value("${google.client.id}")
    private String clientId;

    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final NetHttpTransport transport = new NetHttpTransport();

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    public void init() {
        verifier = new GoogleIdTokenVerifier.Builder(transport, JSON_FACTORY)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public UserGoogleProfile getUserInfoFromIdToken(String idTokenString) {
        try {
            System.out.println("clientId: " + clientId);
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String username = (String) payload.get("name");
                return new UserGoogleProfile(email, username);
            }
            throw new IllegalArgumentException("Invalid ID token");
        } catch (Exception e) {
            throw new RuntimeException("Error verifying ID token: " + e.getMessage());
        }
    }
}
