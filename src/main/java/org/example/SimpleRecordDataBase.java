package org.example;

import org.example.entity.Account;
import org.example.exceptions.InvalidIndexException;
import org.example.exceptions.RecordAlreadyExistException;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleRecordDataBase implements IDataBase<Account> {

    private final Map<Long, Account> data = new HashMap<>();

    private final Map<String, Set<Long>> nameIndex = new HashMap<>();
    private final Map<Double, Set<Long>> valueIndex = new HashMap<>();

    @Override
    public void add(Account objectToAdd) throws NullPointerException, InvalidIndexException{
        if(objectToAdd == null) {
            throw new NullPointerException("Attempted to add a null record to the database");
        }
        Long accountId = objectToAdd.getAccount();
        if(accountId == null || accountId <= 0) {
            throw new InvalidIndexException("Account ID must be a positive non-zero value");
        }

        if(data.containsKey(objectToAdd.getAccount())) {
            throw new RecordAlreadyExistException("Record with this id is already exist : " + objectToAdd.getAccount());
        }

        addRecord(objectToAdd);
    }

    @Override
    public void edit(Long index, Account editedObject) {
        assertIndex(index);
        removeRecord(data.get(index));
        addRecord(editedObject);
    }

    @Override
    public void delete(Long index) {
        assertIndex(index);
        removeRecord(data.get(index));
        data.remove(index);
    }

    @Override
    public Set<Account> findByField(String fieldName, Object target)  throws IllegalArgumentException{
        Set<Account> accountSet = new HashSet<>();
        return switch (fieldName) {
            case ("account") -> {
                if(!(target instanceof Long)) {
                    throw new IllegalArgumentException(
                                    "Excepted instance of " +
                                    Long.class.getName() +
                                    " but received : " +
                                    target.getClass().getName()
                    );
                }
                accountSet.add(data.get((long) target));
                yield accountSet;
            }
            case "name" -> {
                if(!(target instanceof String)) {
                    throw new IllegalArgumentException(
                                    "Excepted instance of " +
                                    String.class.getName() +
                                    " but received : " +
                                    target.getClass().getName()
                    );
                }
                yield  getRecordByIndex(nameIndex, (String) target);
            }
            case "value" -> {
                if(!(target instanceof Double)) {
                    throw new IllegalArgumentException(
                                    "Excepted instance of " +
                                    Double.class.getName() +
                                    " but received : " +
                                    target.getClass().getName()
                    );
                }
                yield getRecordByIndex(valueIndex, (Double) target);
            }
            default -> throw new IllegalArgumentException("Cant find this field : " + fieldName);
        };
    }

    @Override
    public Account findById(Object id) {
        if((long) id <= 0){
            throw new InvalidIndexException("Account ID must be a positive non-zero value");
        }
        return data.get((long) id);
    }

    private void addRecord(Account newRecord) {
        data.put(newRecord.getAccount(), newRecord);
        addToIndex(nameIndex, newRecord.getName(), newRecord.getAccount());
        addToIndex(valueIndex, newRecord.getValue(), newRecord.getAccount());
    }

    private void assertIndex(Long index) throws InvalidIndexException{
        if(index <= 0) {
            throw new InvalidIndexException("Account ID must be a positive non-zero value");
        }

        Account received = findById(index);
        if(received == null){
            throw new InvalidIndexException("Record with this index has not been found : " + index);
        }
    }

    private void removeRecord(Account record) {
        removeFromIndex(nameIndex, record.getName(), record.getAccount());
        removeFromIndex(valueIndex, record.getValue(), record.getAccount());
        if(nameIndex.get(record.getName()).isEmpty()) nameIndex.remove(record.getName());
        if(valueIndex.get(record.getValue()).isEmpty()) valueIndex.remove(record.getValue());
        data.remove(record.getAccount());
    }

    private <T> void addToIndex(Map<T, Set<Long>> indexMap, T index, Long id) {
        indexMap.computeIfAbsent(index, k -> new HashSet<>()).add(id);
    }

    private <T> void removeFromIndex(Map<T, Set<Long>> indexMap, T index, Long id){
        indexMap.get(index).remove(id);
    }

    private <T> Set<Account> getRecordByIndex(Map<T, Set<Long>> indexMap, T index) {
        return indexMap.getOrDefault(index, Set.of())
                .stream()
                .map(data::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
