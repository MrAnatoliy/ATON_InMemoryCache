package org.example;

import org.example.entity.Account;

import java.util.Set;

public class Main {
    public static void main(String[] args) {
        IDataBase<Account> dataBase = new SimpleRecordDataBase();

        dataBase.add(new Account(1L, "A", .35));
        dataBase.add(new Account(2L, "B", .34));
        dataBase.add(new Account(3L, "B", .35));
        dataBase.add(new Account(4L, "A", .34));
        dataBase.add(new Account(5L, "C", .35));
        dataBase.add(new Account(6L, "D", .35));

        dataBase.edit(6L, new Account(6L, "E", .46));
        dataBase.delete(6L);

        Set<Account> accountSet;

        accountSet = dataBase.findByField("account", 4L);
        accountSet = dataBase.findByField("name", "A");
        accountSet = dataBase.findByField("value", .35);

        dataBase.findByField("name", 4L);

        System.out.println("end");
    }
}