package org.map.socialnetwork.repository.paging;

import org.map.socialnetwork.domain.Entity;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.repository.ConnectionManager;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.repository.database.UserDatabaseRepository;
import org.map.socialnetwork.repository.paging.Page;
import org.map.socialnetwork.validator.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

public class PagingRepository<ID, E extends Entity<ID> > implements Repository<ID, E> {

    private final String tableName;
    private final Repository<ID, E> baseRepository;
    private Page<E> currentPage;
    private Page<E> nextPage;
    private Page<E> previousPage;

    public PagingRepository(Repository<ID,E> baseRepository, String tableName) {
        this.baseRepository = baseRepository;
        this.tableName = tableName;

        int pageSize = getNumberOfEntities();
        if(pageSize == 0) {
            pageSize = 100;
        }

        currentPage = new Page<>(1, pageSize);
        nextPage = new Page<>(2, pageSize);
        previousPage = new Page<>(0, pageSize);
        managePages();
    }


    @Override
    public Optional<E> save(E entity) {
        return baseRepository.save(entity);
    }

    @Override
    public Optional<E> findOne(ID id) {
        return baseRepository.findOne(id);
    }

    @Override
    public Iterable<E> findAll() {
        managePages();
        return currentPage.getContent();
    }

    @Override
    public Optional<E> update(E entity) {
        return baseRepository.update(entity);
    }

    @Override
    public Optional<E> delete(ID id) {
        return baseRepository.delete(id);
    }

    @Override
    public Set<E> getResultSet(ResultSet resultSet) throws SQLException {
        return baseRepository.getResultSet(resultSet);
    }


    public int getNumberOfEntities() {
        //TODO use SQL commands
        return StreamSupport.stream(baseRepository.findAll().spliterator(), true).toList().size();
    }

    public int getNumberOfPages() {

        int nrOfPages = getNumberOfEntities() / currentPage.getPageSize();

        return getNumberOfEntities() % currentPage.getPageSize() == 0 ? nrOfPages : nrOfPages + 1;

    }

    public void setPageSize(int pageSize) {
        currentPage = new Page<>(currentPage.pageNumber, pageSize);
        nextPage = new Page<>(nextPage.pageNumber, pageSize);
        previousPage = new Page<>(nextPage.pageNumber, pageSize);
    }

    public int getPageSize() {
        return currentPage.getPageSize();
    }

    public void setPageNumber(int pageNumber) {
        currentPage.setPageNumber(pageNumber);
        nextPage.setPageNumber(pageNumber + 1);
        previousPage.setPageNumber(pageNumber - 1);
    }

    public int getPageNumber() {
        return currentPage.getPageNumber();
    }

    public void managePages() {


        //TODO USE CACHING

        loadPage(currentPage);
        if(previousPage.getPageNumber() >= 1) {
            loadPage(previousPage);
        }

        if(nextPage.getPageNumber() <= getNumberOfPages()) {
            loadPage(nextPage);
        }

    }


    public void loadPage(Page<E> page) {
        String sqlStatement = "select * from " + tableName + " limit ? offset ?";

        try (PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)
        ){
            preparedStatement.setInt(1, page.getPageSize());
            preparedStatement.setInt(2, (page.getPageNumber() - 1) * page.getPageSize());
            ResultSet resultSet = preparedStatement.executeQuery();
            page.setContent(getResultSet(resultSet));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
