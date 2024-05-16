package org.example;

import org.example.entity.Account;
import org.example.exceptions.InvalidIndexException;
import org.example.exceptions.RecordAlreadyExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class SimpleRecordDataBaseTest {
    private IDataBase<Account> dataBase;

    @BeforeEach
    void setupDataBase() {
        dataBase = new SimpleRecordDataBase();
    }

    // Add method tests --------------------------------------------

    @Test
    void add_AccountWithValidData_DoesNotThrow() {

        Account newAccount = new Account(1L, "name", 0.5);

        Assertions.assertDoesNotThrow(() -> dataBase.add(newAccount));

        Set<Account> receivedAccounts = dataBase.findByField("account", 1L);
        Assertions.assertEquals(1, receivedAccounts.size());

        Account firstAccount = receivedAccounts.iterator().next();
        Assertions.assertEquals(firstAccount, newAccount);

        Set<Account> receivedByName  = dataBase.findByField("name", "name");
        Set<Account> receivedByValue = dataBase.findByField("value", 0.5);

        Assertions.assertEquals(receivedByName.size(), 1);
        Assertions.assertEquals(receivedByName.iterator().next(), newAccount);

        Assertions.assertEquals(receivedByValue.size(), 1);
        Assertions.assertEquals(receivedByValue.iterator().next(), newAccount);

    }

    @Test
    void add_AccountIsNull_ExceptionThrown() {
        Assertions.assertThrows(NullPointerException.class, () -> dataBase.add(null));
    }

    @Test
    void add_AccountIdIsNull_ExceptionThrown() {
        Account nullIdAccount = new Account(null, "name", 0.5);
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.add(nullIdAccount));
    }

    @Test
    void add_AccountIdIsZero_ExceptionThrown() {
        Account zeroIdAccount = new Account(0L, "name", 0.5);
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.add(zeroIdAccount));
    }

    @Test
    void add_AccountIdIsNegative_ExceptionThrown() {
        Account negativeIdAccount = new Account(-1L, "name", 0.5);
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.add(negativeIdAccount));
    }

    @Test
    void add_AccountAlreadyExist_ExceptionThrown() {
        Account first = new Account(1L, "name", 0.5);
        Account second = new Account(1L, "name", 8.6);

        Assertions.assertThrows(RecordAlreadyExistException.class, () -> {
            dataBase.add(first);
            dataBase.add(second);
        });
    }

    // Delete method tests --------------------------------------------

    @Test
    void delete_ValidIndex_DoesNotThrow() {
        Account newAccount = new Account(1L, "name", 0.5);
        dataBase.add(newAccount);
        Assertions.assertDoesNotThrow(() -> dataBase.delete(1L));

        Set<Account> receivedByName  = dataBase.findByField("name", "name");
        Set<Account> receivedByValue = dataBase.findByField("value", 0.5);

        Assertions.assertEquals(receivedByName.size(), 0);
        Assertions.assertEquals(receivedByValue.size(), 0);
    }

    @Test
    void delete_NegativeIndex_ExceptionThrown() {
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.delete(-1L));
    }
    @Test
    void delete_ZeroIndex_ExceptionThrown() {
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.delete(0L));
    }

    @Test
    void delete_RecordDoesNotExist_ExceptionThrown() {
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.delete(1L));
    }

    // Edit method tests --------------------------------------------

    @Test
    void edit_NormalUseCase_DoesNotThrow() {
        Account newAccount = new Account(1L, "name", 0.5);
        dataBase.add(newAccount);
        Account editedAccount = new Account(1L, "name_b", 0.75);
        Assertions.assertDoesNotThrow(() -> dataBase.edit(1L, editedAccount));

        Set<Account> receivedByName = dataBase.findByField("name", "name_b");
        Set<Account> receivedByValue = dataBase.findByField("value", 0.75);

        Assertions.assertEquals(receivedByName.size(), 1);
        Assertions.assertEquals(receivedByName.iterator().next(), editedAccount);

        Assertions.assertEquals(receivedByValue.size(), 1);
        Assertions.assertEquals(receivedByValue.iterator().next(), editedAccount);
    }

    @Test
    void edit_NegativeIndex_ExceptionThrown() {
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.edit(-1L, new Account()));
    }

    @Test
    void edit_ZeroIndex_ExceptionThrown() {
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.edit(0L, new Account()));
    }

    @Test
    void edit_RecordDoesNotExist_ExceptionThrown() {
        Assertions.assertThrows(InvalidIndexException.class, () -> dataBase.edit(1L, new Account()));
    }

    // Find method tests --------------------------------------------

    @Test
    void findById_NormalUseCase_Account() {
        Account newAccount = new Account(1L, "name", 0.5);
        dataBase.add(newAccount);

        Account received = dataBase.findById(1L);
        Assertions.assertEquals(received, newAccount);
    }

    @Test
    void findById_NonExistingIndex_null() {
        Account result = dataBase.findById(1L);
        Assertions.assertNull(result);
    }

    @Test
    void findByField_NormalUseCaseAccountField_Account() {
        Account newAccount = new Account(1L, "name", 0.5);
        Set<Account> accountSet = new HashSet<>();
        accountSet.add(newAccount);
        dataBase.add(newAccount);

        Set<Account> result = dataBase.findByField("account", 1L);
        Assertions.assertEquals(result,accountSet);
    }

    @Test
    void findByField_NormalUseCaseNameField_Account() {
        Set<Account> accountSet = new HashSet<>();
        accountSet.add(new Account(1L, "name", 0.5));
        accountSet.add(new Account(2L, "name", 0.6));
        accountSet.add(new Account(3L, "name", 0.7));

        dataBase.add(new Account(1L, "name", 0.5));
        dataBase.add(new Account(2L, "name", 0.6));
        dataBase.add(new Account(3L, "name", 0.7));
        dataBase.add(new Account(4L, "name_b", 0.7));

        Set<Account> result = dataBase.findByField("name", "name");
        Assertions.assertEquals(result,accountSet);
    }

    @Test
    void findByField_NormalUseCaseValueField_Account() {
        Set<Account> accountSet = new HashSet<>();
        accountSet.add(new Account(1L, "name", 0.5));
        accountSet.add(new Account(2L, "name_2", 0.5));
        accountSet.add(new Account(3L, "name_3", 0.5));

        dataBase.add(new Account(1L, "name", 0.5));
        dataBase.add(new Account(2L, "name_2", 0.5));
        dataBase.add(new Account(3L, "name_3", 0.5));
        dataBase.add(new Account(4L, "name_4", 0.6));

        Set<Account> result = dataBase.findByField("value", 0.5);
        Assertions.assertEquals(result,accountSet);
    }

    @Test
    void findByField_WrongFieldName_ExceptionThrown() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dataBase.findByField("aboba", 1));
    }

    @Test
    void findByField_WrongAccountTargetType_Exception() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dataBase.findByField("account", "Hi :D"));
    }

    @Test
    void findByField_WrongNameTargetType_Exception() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dataBase.findByField("name", 8));
    }

    @Test
    void findByField_WrongValueTargetType_Exception() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dataBase.findByField("value", "what?"));
    }


}
