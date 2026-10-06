package com.bmitracker.dao;

import java.util.List;

/**
 * Generic CRUD contract.
 * Demonstrates ABSTRACTION and the SOLID principles:
 *  - Interface Segregation: a small, focused contract (only 5 methods).
 *  - Dependency Inversion: higher layers (service, GUI) depend on this
 *    abstraction, not on a concrete DAO implementation, so the storage
 *    technology (JDBC/SQLite here) can be swapped without touching them.
 *
 * @param <T> the entity type this DAO manages
 */
public interface Crud<T> {
    boolean create(T item);
    T read(int id);
    List<T> readAll();
    boolean update(T item);
    boolean delete(int id);
}
