package org.msftoutlookoauth2;

public class ClientCredentialBuilder {
    private String authority;
    private String clientId;
    private String secret;
    private String scope;
    private String userName;

    public ClientCredentialBuilder setAuthority(String authority) {
        this.authority = authority;
        return this;
    }

    public ClientCredentialBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ClientCredentialBuilder setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public ClientCredentialBuilder setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public ClientCredentialBuilder setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public ClientCredential createClientCredential() {
        return new ClientCredential(authority, clientId, secret, scope, userName);
    }
}