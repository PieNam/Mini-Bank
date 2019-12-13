package org.fisco.bcos;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.apache.tools.ant.taskdefs.SQLExec.Transaction;
import org.fisco.bcos.constants.GasConstants;
import org.fisco.bcos.temp.HelloWorld;
import org.fisco.bcos.temp.SupplyChain;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ContractTest extends BaseTest {

    @Autowired private Web3j web3j;
    @Autowired private Credentials credentials;

    @Test
    public void deployAndCallHelloWorld() throws Exception {
        // deploy contract
        HelloWorld helloWorld =
                HelloWorld.deploy(
                                web3j,
                                credentials,
                                new StaticGasProvider(
                                        GasConstants.GAS_PRICE, GasConstants.GAS_LIMIT))
                        .send();
        if (helloWorld != null) {
            System.out.println("HelloWorld address is: " + helloWorld.getContractAddress());
            // call set function
            helloWorld.set("Hello, World!").send();
            // call get function
            String result = helloWorld.get().send();
            System.out.println(result);
            assertTrue("Hello, World!".equals(result));
        }
    }

    @Test
    public void deployANdCallSupplyChain() throws Exception {
        Credentials bank_account = GenCredential.create();
        String bank_account_address = bank_account.getAddress();
        String bank_account_private_key = bank_account.getEcKeyPair().getPrivateKey().toString(16);
        System.out.println("root privatekey*****"+bank_account_private_key+"*****");
        System.out.println("original address: "+bank_account_address);
        System.out.println("reget    address: "+GenCredential.create(bank_account_private_key).getAddress());
        Credentials user1_account = GenCredential.create();
        String user1_account_address = user1_account.getAddress();
        Credentials user2_account = GenCredential.create();
        String user2_account_address = user2_account.getAddress();
        Credentials user3_account = GenCredential.create();
        String user3_account_address = user3_account.getAddress();

        SupplyChain sc = SupplyChain.deploy(
            web3j, 
            bank_account, 
            new StaticGasProvider(
                GasConstants.GAS_PRICE, 
                GasConstants.GAS_LIMIT)).send();
        if (sc != null) {
            //////////////////// deploy test done
            System.out.println("SupplyChain address is: *****" + sc.getContractAddress() + "*****");


            //////////////////// init test done
            //////////////////// user1 init: 20000000
            //////////////////// user2 init: 19981023
            //////////////////// user3 init: 995000000
            // TransactionReceipt temp1 = sc.init(user1_account_address, BigInteger.valueOf(20000000)).send();
            // TransactionReceipt temp2 = sc.init(user2_account_address, BigInteger.valueOf(19981023)).send();
            // TransactionReceipt temp3 = sc.init(user3_account_address, BigInteger.valueOf(995000000)).send();
            // System.out.println("*****first contract call receipt: \n\ttemp1: " + temp1.toString() + "\n\ttemp2: " + temp2.toString() + "\n\ttemp3: " + temp3.toString());
        }

    //     SupplyChain scu1 = SupplyChain.load(sc.getContractAddress(), web3j, user1_account, new StaticGasProvider(GasConstants.GAS_PRICE, GasConstants.GAS_LIMIT));
    //     if (scu1 != null) {
    //         System.out.println("\n\t*****user1 balance after init: " + scu1.getBalances().send());
    //     }

    //     SupplyChain scu2 = SupplyChain.load(sc.getContractAddress(), web3j, user2_account, new StaticGasProvider(GasConstants.GAS_PRICE, GasConstants.GAS_LIMIT));
    //     if (scu2 != null) {
    //         System.out.println("\n\t*****user2 balance after init: " + scu2.getBalances().send());
    //         TransactionReceipt temp = scu2.transferMoney(user1_account_address, BigInteger.valueOf(1023)).send();
    //         // System.out.println("*****transfer money receipt: " + temp.toString());

    //         //////////////////// transfer test done
    //         //////////////////// user2 transfer 1023 to user1
    //         System.out.println("\n\t*****user1 balance after transfer: " + scu1.getBalances().send());
    //         System.out.println("\n\t*****user2 balance after transfer: " + scu2.getBalances().send());

    //         //////////////////// loan test done
    //         //////////////////// user1 loan 20000000 to user2
    //         TransactionReceipt loan_tr = scu1.loan(user2_account_address, BigInteger.valueOf(20000000), BigInteger.valueOf(10)).send();
    //         // System.out.println("*****loan receipt: " + loan_tr.toString());

    //         System.out.println("\n\t*****user1 balance after loan: " + scu1.getBalances().send());
    //         System.out.println("\n\t*****user2 balance after loan: " + scu2.getBalances().send());

    //         //////////////////// get loans test done
    //         //////////////////// param1 user_account_address, param2 index
    //         System.out.println("\t**********check loans: " + sc.loans(user1_account_address, BigInteger.valueOf(0)).send().toString());
    //         //////////////////// **********check loans: Tuple6{value1=1576050328445, value2=true, value3=0x101be9d9fa84c90a4c4ee9e6f65e82bc8ab31080, value4=20000000, value5=87976050328445, value6=false}

    //         //////////////////// payLoan test done
    //         //////////////////// user2 payloan 10000000 back to user1
    //         TransactionReceipt pay_tr = scu2.payLoan(BigInteger.valueOf(0), BigInteger.valueOf(10000000)).send();
    //         // System.out.println("*****payloan receipt: " + pay_tr.toString());

    //         System.out.println("\n\t*****user1 balance after loan: " + scu1.getBalances().send());
    //         System.out.println("\n\t*****user2 balance after loan: " + scu2.getBalances().send());
    //         System.out.println("\t**********check loans after pay: " + sc.loans(user1_account_address, BigInteger.valueOf(0)).send().toString());
    //     }
        
    //     SupplyChain scu3 = SupplyChain.load(sc.getContractAddress(), web3j, user3_account, new StaticGasProvider(GasConstants.GAS_PRICE, GasConstants.GAS_LIMIT));
    //     if (scu3 != null) {
    //         //////////////////// transferLoan test done
    //         //////////////////// user1 transferLoan 5000000 to user3
    //         System.out.println("\n\t*****user3 balance after init: " + scu3.getBalances().send());
    //         TransactionReceipt temp = scu1.transferLoan(BigInteger.valueOf(0), user3_account_address, BigInteger.valueOf(5000000)).send();
    //         // System.out.println("*****transfer loan receipt: " + temp.toString());

    //         System.out.println("\t**********check loans after transferloan: \n\t\tuser1:" + sc.loans(user1_account_address, BigInteger.valueOf(0)).send().toString() + "\n\t\tuser3:" + sc.loans(user3_account_address, BigInteger.valueOf(0)).send().toString());
    //         System.out.println("\n\t*****user3 balance after transfer loan: " + scu3.getBalances().send());

    //         //////////////////// financing test done
    //         //////////////////// user3 ask for financing to get 5000000
    //         TransactionReceipt f_rt = scu3.finacing(BigInteger.valueOf(1000000), BigInteger.valueOf(365)).send();
    //         // System.out.println("*****transfer loan receipt: " + f_rt.toString());
    //         System.out.println("\n\t*****user3 balance after financing: " + scu3.getBalances().send());

    //         System.out.println("\t**********check loans after financing: \n\t\tuser3 loans 1:" + sc.loans(user3_account_address, BigInteger.valueOf(1)).send().toString());
    //     }
    }
}
