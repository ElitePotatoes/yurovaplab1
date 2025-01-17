package tech.reliab.course.bank.database.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T, K> {
    T get(K id);

    List<T> getAll();

    void insert(T t);

    void update(T t);

    void delete(K id);

    void deleteAll();
}