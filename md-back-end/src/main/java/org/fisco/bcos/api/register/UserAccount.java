package org.fisco.bcos.api.register;

public class UserAccount {
    public final String status;
    public final String address;
    public final String private_key;

    public UserAccount(String status, String private_key, String address) {
        this.status = status;
        this.private_key = private_key;
        this.address = address;
    }
}