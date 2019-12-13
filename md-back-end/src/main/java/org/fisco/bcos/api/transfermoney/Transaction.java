package org.fisco.bcos.api.transfermoney;

import java.math.BigInteger;

public class Transaction {
    public String status;
    public BigInteger balance;

    public Transaction(String status, BigInteger balance) {
        this.status = status;
        this.balance = balance;
    }
}