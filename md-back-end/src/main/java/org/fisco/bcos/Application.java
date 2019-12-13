package org.fisco.bcos;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.fisco.bcos.api.financing.*;
import org.fisco.bcos.api.getter.*;
import org.fisco.bcos.api.loan.*;
import org.fisco.bcos.api.payloan.*;
import org.fisco.bcos.api.register.*;
import org.fisco.bcos.api.transferloan.*;
import org.fisco.bcos.api.transfermoney.*;

import org.fisco.bcos.contracts.*;
import org.fisco.bcos.constants.GasConstants;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.*;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableConfigurationProperties
public class Application {
    @Autowired private Web3j web3j;

    private final String root_private_key = "35456e57d95461e7c8084ea4e68be079727b3a330b84f1d0a1e46dfb2b2966a4";
    private final String root_contract_address = "0xb907dd1d1334e48b8bf048281fa7126ef5f7f966";
    private final Credentials root_account = Credentials.create(root_private_key);

    public SupplyChain getRootContract() throws Exception {
        SupplyChain res = SupplyChain.load(this.root_contract_address, web3j, this.root_account, new StaticGasProvider(GasConstants.GAS_PRICE, GasConstants.GAS_LIMIT));
        return res;
    }

    public SupplyChain getContract(String private_key) throws Exception {
        Credentials c_res = Credentials.create(private_key);
        SupplyChain res = SupplyChain.load(this.root_contract_address, web3j, c_res, new StaticGasProvider(GasConstants.GAS_PRICE, GasConstants.GAS_LIMIT));
        return res;
    }
    
    @PostMapping("/register")
    public UserAccount registerHandler(@RequestBody UserAccountRequest request) throws Exception {
        Credentials user_account = GenCredential.create();
        String user_private_key = user_account.getEcKeyPair().getPrivateKey().toString(16);
        String user_address = user_account.getAddress();
        TransactionReceipt r = getRootContract().init(user_address, BigInteger.valueOf(request.balance)).send();
        System.out.println("**********init receipt: " + r.toString() + "**********");
        return new UserAccount("success", user_private_key, user_address);
    }

    @PostMapping("/get_user_info")
    public UserInfo getBalanceCreditHandler(@RequestBody GetRequest request) throws Exception {
        System.out.println("**********request private key:" + request.private_key + "**********");
        SupplyChain contract = getContract(request.private_key);
        Credentials user_account = GenCredential.create(request.private_key);
        String user_address = user_account.getAddress();
        BigInteger balance_res = contract.getBalances().send();
        BigInteger credit_res = contract.getCredits().send();
        if (credit_res.compareTo(BigInteger.ZERO) == 0) {
            return new UserInfo("fail", "", BigInteger.ZERO, BigInteger.ZERO);
        }
        return new UserInfo("success", user_address, balance_res, credit_res);
    }

    @PostMapping("/transfer_money")
    public Transaction transferMoneyHandler(@RequestBody TransferRequest request) throws Exception {
        System.out.println("**********request user private key:" + request.private_key + "**********");
        System.out.println("**********request recipient address:" + request.recipient + "**********");
        SupplyChain contract = getContract(request.private_key);
        contract.transferMoney(request.recipient, BigInteger.valueOf(request.amount)).send();
        BigInteger balance_update = contract.getBalances().send();
        return new Transaction("success", balance_update);
    }

    @PostMapping("/loan")
    public Transaction loanHandler(@RequestBody LoanRequest request) throws Exception {
        System.out.println("**********request user private key:" + request.private_key + "**********");
        System.out.println("**********request recipient address:" + request.recipient + "**********");
        SupplyChain contract = getContract(request.private_key);
        contract.loan(request.recipient, BigInteger.valueOf(request.amount), BigInteger.valueOf(request.due_day)).send();
        BigInteger balance_update = contract.getBalances().send();
        return new Transaction("success", balance_update);
    }

    @PostMapping("/get_loans")
    public LoansInfo getLoansHandler(@RequestBody GetRequest request) throws Exception {
        System.out.println("**********request user private key:" + request.private_key + "**********");
        SupplyChain contract = getContract(request.private_key);
        Credentials user_account = Credentials.create(request.private_key);
        String user_address = user_account.getAddress();
        BigInteger loan_num = contract.loanIndexs(user_address).send();
        List<LoanInfo> loans = new ArrayList<>();
        System.out.println("loan_num: " + loan_num.toString());
        for (BigInteger i = BigInteger.valueOf(0); i.compareTo(loan_num) < 0; i = i.add(BigInteger.ONE)) {
            System.out.println("i: " + i.toString());
            Tuple6<BigInteger,Boolean,String,BigInteger,BigInteger,Boolean> tuple = contract.loans(user_address, i).send();
            LoanInfo single_loan = new LoanInfo(i, tuple.getValue1(), tuple.getValue2(), tuple.getValue3(), tuple.getValue4(), tuple.getValue5(), tuple.getValue6());
            loans.add(single_loan);
            // res += loan_info.loan_index + " " + loan_info.loan_ID + " " + loan_info.is_lender + " " + loan_info.other + " " + loan_info.amount + " " + loan_info.due_date + " " + loan_info.is_closed + "\n";
            // res += "loan_index " + i.toString();
            // res += " loan_ID " + tuple.getValue1();
            // res += " is_lender " + (tuple.getValue2() ? "true" : "false");
            // res += " other " + tuple.getValue3();
            // res += " amount " + tuple.getValue4();
            // res += " due_date " + tuple.getValue5();
            // res += " is_closed " + tuple.getValue6() + "\n";
            // System.out.println("i: " + i.toString() + " res: " + res);
        }
        return new LoansInfo("success", loans);
    }

    @PostMapping("/pay_loan")
    public PayLoanResponse payLoanHandler(@RequestBody PayLoanRequest request) throws Exception {
        System.out.println("**********request user private key:" + request.private_key + "**********");
        SupplyChain contract = getContract(request.private_key);
        contract.payLoan(BigInteger.valueOf(request.loan_index), BigInteger.valueOf(request.amount)).send();
        BigInteger balance_update = contract.getBalances().send();
        BigInteger credit_update = contract.getCredits().send();
        return new PayLoanResponse("success", balance_update, credit_update);
    }

    @PostMapping("/transfer_loan")
    public Transaction transferLoanHandler(@RequestBody TransferLoanRequest request) throws Exception {
        System.out.println("**********request user private key:" + request.private_key + "**********");
        SupplyChain contract = getContract(request.private_key);
        contract.transferLoan(BigInteger.valueOf(request.loan_index), request.recipient, BigInteger.valueOf(request.amount)).send();
        BigInteger balance_update = contract.getBalances().send();
        return new Transaction("success", balance_update);
    }

    @PostMapping("/financing")
    public Transaction financingHanlder(@RequestBody FinancingRequest request) throws Exception {
        System.out.println("**********request user private key:" + request.private_key + "**********");
        SupplyChain contract = getContract(request.private_key);
        contract.finacing(BigInteger.valueOf(request.amount), BigInteger.valueOf(request.due_day)).send();
        BigInteger balance_update = contract.getBalances().send();
        return new Transaction("success", balance_update);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        System.out.println("========== application begin to run, listening... ==========");
    }
}
