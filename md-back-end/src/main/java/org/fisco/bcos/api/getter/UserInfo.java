package org.fisco.bcos.api.getter;

import java.math.BigInteger;

public class UserInfo {
    public String status;
    public String address;
    public BigInteger balance;
    public BigInteger credit;

    public UserInfo(String status, String address, BigInteger balance, BigInteger credit) {
        this.status = status;
        this.address = address;
        this.balance = balance;
        this.credit = credit;
    }
}