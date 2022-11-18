package org.msftoutlookoauth2;// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

class ClientCredentialGrant {

    public static void main(String args[]) throws Exception{

        ClientCredential clientCredential = setUpSampleData();

        try {
            ConfidentialClientApplication app = buildConfidentialClientObject(clientCredential);
            IAuthenticationResult result = getAccessTokenByClientCredentialGrant(clientCredential.getScope(), app);
            //String usersListFromGraph = getUsersListFromGraph(result.accessToken());
            //String token = "";
            connectToOutlook(clientCredential, result.accessToken());
        } catch(Exception ex){
            System.out.println("Oops! We have an exception of type - " + ex.getClass());
            System.out.println("Exception message - " + ex.getMessage());
            throw ex;
        }
    }

    private static void connectToOutlook(ClientCredential clientCredential, String token) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.imap.ssl.enable", "true"); // required for Gmail
        props.put("mail.imap.sasl.enable", "true");
        props.put("mail.imap.sasl.mechanisms", "XOAUTH2");
        props.put("mail.imap.auth.login.disable", "true");
        props.put("mail.imap.auth.plain.disable", "true");
        props.put("mail.debug", "true");
        props.put("mail.debug.oauth", "true");
        String username = clientCredential.getUserName();

        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");
        store.connect("outlook.office365.com", 993, username, token);
        System.out.println("isConnected = " + store.isConnected());
        System.out.println("default folder = " + store.getDefaultFolder());
    }

    private static ConfidentialClientApplication buildConfidentialClientObject(ClientCredential clientCredential) throws Exception {
        
    	// Load properties file and set properties used throughout the sample
        return ConfidentialClientApplication.builder(
                clientCredential.getClientId(),
                ClientCredentialFactory.createFromSecret(clientCredential.getSecret()))
                .authority(clientCredential.getAuthority())
                .build();		        
    }

    private static IAuthenticationResult getAccessTokenByClientCredentialGrant(String scope, ConfidentialClientApplication app) throws Exception {
    	
    	// With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
        // application permissions need to be set statically (in the portal), and then granted by a tenant administrator
        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                Collections.singleton(scope))
                .build();
    	
        CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
        return future.get();
    }

    private static String getUsersListFromGraph(String accessToken) throws IOException {
        URL url = new URL("https://graph.microsoft.com/v1.0/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Accept","application/json");

        int httpResponseCode = conn.getResponseCode();
        if(httpResponseCode == HTTPResponse.SC_OK) {

            StringBuilder response;
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))){

                String inputLine;
                response = new StringBuilder();
                while (( inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } else {
            return String.format("Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
        }
    }

    /**
     * Helper function unique to this sample setting. In a real application these wouldn't be so hardcoded, for example
     * different users may need different authority endpoints or scopes
     */
    private static ClientCredential setUpSampleData() throws IOException {
        // Load properties file and set properties used throughout the sample
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));

        return new ClientCredentialBuilder()
                .setAuthority(properties.getProperty("AUTHORITY"))
                .setClientId(properties.getProperty("CLIENT_ID"))
                .setSecret(properties.getProperty("SECRET"))
                .setScope(properties.getProperty("SCOPE"))
                .setUserName(properties.getProperty("USER_NAME"))
                .createClientCredential();
    }
}
