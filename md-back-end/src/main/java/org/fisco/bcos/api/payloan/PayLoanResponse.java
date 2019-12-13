package org.fisco.bcos.api.payloan;

import java.math.BigInteger;

public class PayLoanResponse {
    public String status;
    public BigInteger balance;
    public BigInteger credit;

    public PayLoanResponse(String status, BigInteger balance, BigInteger credit) {
        this.status = status;
        this.balance = balance;
        this.credit = credit;
    }
}