pragma solidity ^0.4.21;

contract SupplyChain {

    /*
     * loan handle
     * data struct to record and mapping to store
     */
    struct Loan {
        uint loanID;
        // mark the if the host is the lender or the borrower
        bool isLender;
        // address of the other
        address other;
        // the amount of the loan deal
        uint amount;
        // due date to closed the loan
        uint duedate;
        // flag to mark if the loan is finished
        bool closed;
    }

    // the third-party bank
    address public bank;
    // balances mapping to record balance of users
    mapping (address => uint) public balances;
    // credits mapping to record credit of users
    mapping (address => uint) public credits;
    // loans mapping to record 
    mapping (address => mapping (uint => Loan)) public loans;
    // loans mapping index of everyone
    mapping (address => uint) public loanIndexs;

    // event to record and store transfer
    event Sent(address from, address to, uint amount);
    // event to record and store loan
    event LoanEvent(uint loanID, address lender, address borrower, uint amount, uint duedate);

    /*
     * to assign global thrid-party bank
     */
    constructor() {
        bank = msg.sender;
    }

    /*
     * getters
     */
    function getBalances() constant public returns(uint) {
        return balances[msg.sender];
    }
    function getCredits() constant public returns(uint) {
        return credits[msg.sender];
    }

    /*
     * to issue fund to a user
     */
    function init(address receiver, uint amount) public {
        // only the bank have the permission to issue
        if (msg.sender != bank) return; 
        balances[receiver] += amount;
        loanIndexs[receiver] = 0;
        credits[receiver] = 65;
    }

    /*
     * issue transfer from one to one
     * sender must have balance more than the amount
     */
    function transferMoney(address receiver, uint amount) public {
        if (balances[msg.sender] < amount) return;
        balances[msg.sender] -= amount;
        balances[receiver] += amount;
        emit Sent(msg.sender, receiver, amount);
    }

    /*
     * issue loan from one to one
     */
    function loan(address borrower, uint amount, uint dueday) public {
        if (credits[borrower] < 60 || balances[msg.sender] < amount) return;
        balances[borrower] += amount;
        balances[msg.sender] -= amount;
        uint tempID = now;
        uint tempDue = dueday * 86400000 + now;
        Loan memory temp1 = Loan(tempID, true, borrower, amount, tempDue, false);
        Loan memory temp2 = Loan(tempID, false, msg.sender, amount, tempDue, false);
        loans[msg.sender][loanIndexs[msg.sender]++] = temp1;
        loans[borrower][loanIndexs[borrower]++] = temp2;
        emit LoanEvent(tempID, msg.sender, borrower, amount, tempDue);
    }

    /*
     * transfer loan to others
     * sender must be the lender and the loan is not closed
     */
    function transferLoan(uint loanIndex, address receiver, uint amount) {
        Loan memory temp = loans[msg.sender][loanIndex];
        if (temp.closed || !temp.isLender || credits[temp.other] < 65 || temp.duedate < now
            || temp.amount < amount || balances[receiver] < amount) return;

        address borrower = temp.other;
        uint borrowerLoanIndex = 0xffffffff;
        for (uint i = 0; i < loanIndexs[borrower]; ++i) {
            if (loans[borrower][i].loanID == temp.loanID) {
                borrowerLoanIndex = i;
                break;
            }
        }
        if (borrowerLoanIndex == 0xffffffff) return;
        loans[borrower][borrowerLoanIndex].amount -= amount;
        if (loans[borrower][borrowerLoanIndex].amount == 0) {
            loans[borrower][borrowerLoanIndex].closed = true;
        }
        loans[msg.sender][loanIndex].amount -= amount;
        if (loans[msg.sender][loanIndex].amount == 0) {
            loans[msg.sender][loanIndex].closed = true;
        }
        balances[msg.sender] += amount;
        balances[receiver] -= amount;
        uint tempID = now;
        uint oridue = loans[msg.sender][loanIndex].duedate;
        Loan memory temp1 = Loan(tempID, true, borrower, amount, oridue, false);
        Loan memory temp2 = Loan(tempID, false, receiver, amount, oridue, false);
        loans[receiver][loanIndexs[receiver]++] = temp1;
        loans[borrower][loanIndexs[borrower]++] = temp2;
        emit LoanEvent(tempID, receiver, borrower, amount, oridue);
    }

    /*
     * pay loan and judge if closed
     */
    function payLoan(uint loanIndex, uint amount) {
        // cache borrower loan
        Loan memory temp = loans[msg.sender][loanIndex];
        // judging
        if (temp.isLender || temp.closed || temp.amount < amount || balances[msg.sender] < amount) return;
        // get lender
        address lender = loans[msg.sender][loanIndex].other;
        // get lender loan index
        uint lenderLoanIndex = 0xffffffff;
        for (uint i = 0; i < loanIndexs[lender]; ++i) {
            if (loans[lender][i].loanID == temp.loanID) {
                lenderLoanIndex = i;
                break;
            }
        }
        if (lenderLoanIndex == 0xffffffff) return;

        // update loan information and balances
        balances[msg.sender] -= amount;
        balances[lender] += amount;
        loans[msg.sender][loanIndex].amount -= amount;
        if (loans[msg.sender][loanIndex].amount == 0) {
            loans[msg.sender][loanIndex].closed = true;
        }
        loans[lender][lenderLoanIndex].amount = loans[msg.sender][loanIndex].amount;
        loans[lender][lenderLoanIndex].closed = loans[msg.sender][loanIndex].closed;

        // update credit of borrower:
        //   in-time pay: credit increase
        //   deferred pay: credit decrease
        if (loans[msg.sender][loanIndex].closed) {
            if (loans[msg.sender][loanIndex].duedate > now) {
                credits[msg.sender] += 1;
            } else {
                credits[msg.sender] -= 1;
            }
        }
    }

    function finacing(uint amount, uint dueday) {
        // calculating total
        uint totalLoan = 0;
        for (uint i = 0; i < loanIndexs[msg.sender]; ++i) {
            if (loans[msg.sender][i].isLender) {
                totalLoan += loans[msg.sender][i].amount;
            }
        }
        if (amount < amount || credits[msg.sender] < 65) return;

        balances[msg.sender] += amount;
        uint tempID = now;
        uint tempDue = dueday * 1 days + now;
        Loan memory temp = Loan(tempID, false, bank, amount, tempDue, false);
        loans[msg.sender][loanIndexs[msg.sender]++] = temp;
        emit LoanEvent(tempID, bank, msg.sender, amount, tempDue);
    }
}
