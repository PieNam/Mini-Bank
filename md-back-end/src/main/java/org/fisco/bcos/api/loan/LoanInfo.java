package org.fisco.bcos.api.loan;

import java.math.BigInteger;

public class LoanInfo {
    public BigInteger loan_index;
    public BigInteger loan_ID;
    public Boolean is_lender;
    public String other;
    public BigInteger amount;
    public BigInteger due_date;
    public Boolean is_closed;

    public LoanInfo(BigInteger loan_index, BigInteger loan_ID, Boolean is_lender, String other, BigInteger amount, BigInteger due_date, Boolean is_closed) {
        this.loan_index = loan_index;
        this.loan_ID = loan_ID;
        this.is_lender = is_lender;
        this.other = other;
        this.amount = amount; 
        this.due_date = due_date;
        this.is_closed = is_closed;
    }
}