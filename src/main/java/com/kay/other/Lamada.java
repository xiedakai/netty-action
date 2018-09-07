package com.kay.other;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Lamada {


    public static void main(String[] args) {
        //note
        Account account1=new Account();
        account1.setBalance(new BigDecimal(100));
        Account account2=new Account();
        account2.setBalance(new BigDecimal(200));
        List<Account> list=new ArrayList<Account>();
        list.add(account1);
        list.add(account2);
        System.out.println(
        list.stream().map(s->s.getBalance().add(new BigDecimal(9))).reduce(BigDecimal.ZERO,BigDecimal::add)
            );
        System.out.println("list:"+list.toString());
    }

}
