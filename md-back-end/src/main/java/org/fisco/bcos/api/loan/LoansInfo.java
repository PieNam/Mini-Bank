package org.fisco.bcos.api.loan;

import java.util.List;
import org.fisco.bcos.api.loan.LoanInfo;

public class LoansInfo {
    public String status;
    public List<LoanInfo> loans;

    public LoansInfo(String status, List<LoanInfo> loans) {
        this.status = status;
        this.loans = loans;
    }
}