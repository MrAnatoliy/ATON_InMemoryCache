package org.example;

import java.util.Set;

interface IDataBase<T> {
    void add(T objectToAdd);
    void edit(Long index, T editedObject);
    void delete(Long index);

    Set<T> findByField(String fieldName, Object target);
    T findById(Object id);
}
