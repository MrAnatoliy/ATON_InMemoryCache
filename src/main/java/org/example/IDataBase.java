package org.example;

import java.util.Set;

interface IDataBase<T> {
    public boolean add(T objectToAdd);
    public boolean edit(Long index, T editedObject);
    public  boolean delete(Long index);

    public Set<T> findByField(String fieldName, Object target);
}
