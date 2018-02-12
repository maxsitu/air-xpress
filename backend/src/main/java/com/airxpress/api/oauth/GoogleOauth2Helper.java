package com.airxpress.api.oauth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GoogleOauth2Helper {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");

    public static String getUserInfo(Credential credential) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        Oauth2 oauth2 = new Oauth2.Builder(httpTransport, JSON_FACTORY, credential).build();
        Userinfoplus userinfo = oauth2.userinfo().get().execute();
        httpTransport.shutdown();
        return userinfo.toPrettyString();
    }

    public static GoogleClientSecrets prepareClientSecrets() throws IOException {
        return GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(GoogleOauth2Helper.class.getResourceAsStream("/client_secrets.json")));
    }

    public static GoogleAuthorizationCodeFlow prepareAuthorizationCodeFlow() throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, prepareClientSecrets(), SCOPES).build();
        return flow;
    }

    public static Credential prepareCredential(GoogleAuthorizationCodeFlow flow, String code) throws IOException {
        TokenResponse response = flow.newTokenRequest(code).setRedirectUri("http://www.air-xpress.com:8080/api/googleOauth2/callback").execute();
        return flow.createAndStoreCredential(response, "user");
    }

    public static String prepareRedirectUrl(GoogleAuthorizationCodeFlow flow) {
        return flow.newAuthorizationUrl().setRedirectUri("http://www.air-xpress.com:8080/api/googleOauth2/callback").toString();
    }
}
