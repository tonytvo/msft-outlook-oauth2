package org.msftoutlookoauth2;

public class ClientCredential {
    private final String authority;
    private final String clientId;
    private final String secret;
    private final String scope;
    private final String userName;

    public ClientCredential(String authority,
                            String clientId,
                            String secret,
                            String scope,
                            String userName) {
        this.authority = authority;
        this.clientId = clientId;
        this.secret = secret;
        this.scope = scope;
        this.userName = userName;
    }

    public String getAuthority() {
        return authority;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSecret() {
        return secret;
    }

    public String getScope() {
        return scope;
    }

    public String getUserName() {
        return userName;
    }
}
