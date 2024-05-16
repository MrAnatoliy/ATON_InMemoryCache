package org.example;

import org.example.entity.Account;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleRecordDataBase implements IDataBase<Account> {

    private Map<Long, Account> data = new HashMap<>();

    private Map<String, Set<Long>> nameIndex = new HashMap<>();
    private Map<Double, Set<Long>> valueIndex = new HashMap<>();

    @Override
    public boolean add(Account objectToAdd) {
        try {
            addRecord(objectToAdd);
            return  true;
        } catch (Exception e) {
            System.err.println("Error occurred while adding record : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean edit(Long index, Account editedObject) {
        try {
            if(!Objects.equals(index, editedObject.getAccount())) {
                throw new Exception("index are differ");
            }
            removeRecord(data.get(index));
            addRecord(editedObject);
            return true;
        } catch (Exception e) {
            System.err.println("Error occurred while editing record (" + data.get(index).toString() + ") : " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Long index) {
        try {
            removeRecord(data.get(index));
            //TODO remove key from index after removing it become empty
            data.remove(index);
            return true;
        } catch (Exception e) {
            System.err.println("Error occurred while deleting record (" + index + " - " + data.get(index).toString() + ") : " + e.getMessage());
            return false;
        }
    }

    @Override
    public Set<Account> findByField(String fieldName, Object target) {
        Set<Account> accountSet = new HashSet<>();
        return switch (fieldName) {
            case ("account") -> {
                accountSet.add(data.get((long) target));
                yield accountSet;
            }
            case "name" -> getRecordByIndex(nameIndex, (String) target);
            case "value" -> getRecordByIndex(valueIndex, (Double) target);
            default -> null;
        };
    }

    private void addRecord(Account newRecord) {
        data.put(newRecord.getAccount(), newRecord);
        addToIndex(nameIndex, newRecord.getName(), newRecord.getAccount());
        addToIndex(valueIndex, newRecord.getValue(), newRecord.getAccount());
    }

    private void removeRecord(Account record) {
        removeFromIndex(nameIndex, record.getName(), record.getAccount());
        removeFromIndex(valueIndex, record.getValue(), record.getAccount());
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
