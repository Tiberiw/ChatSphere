package org.map.socialnetwork.repository;

import org.map.socialnetwork.domain.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public interface Repository<ID, E extends Entity<ID>> {

    Optional<E> save(E entity);
    Optional<E> findOne(ID id);
    Iterable<E> findAll();
    Optional<E> update(E entity);
    Optional<E> delete(ID id);
    Set<E> getResultSet(ResultSet resultSet) throws SQLException;

}
