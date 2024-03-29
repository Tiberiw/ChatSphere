package org.map.socialnetwork.repository.database;

import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.repository.ConnectionManager;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.validator.Validator;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDatabaseRepository implements Repository<Long, User> {
    
    private final Validator<User> validator;


    public UserDatabaseRepository(Validator<User> validator) {
        this.validator = validator;
    }
    @Override
    public Optional<User> save(User entity) {

        if (entity == null)
            throw new IllegalArgumentException("Invalid Entity");

        validator.validate(entity);

        //The insert command
        String insertSQLStatement = "insert into users(first_name,last_name) values (?,?);";

        String SQLSelectStatement = "select max(user_id) as id from users;";


        //Get the connection to database
        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(insertSQLStatement);
            PreparedStatement preparedStatement2 = ConnectionManager
                    .getInstance()
                    .getConnection()
                .prepareStatement(SQLSelectStatement)) {
            //Set the string
            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2,entity.getLastName());

            //Get the response from Database
            int response = preparedStatement.executeUpdate();

            if(response == 1) {
                ResultSet resultSet = preparedStatement2.executeQuery();
                if(resultSet.next()) {
                    long user_id = resultSet.getLong("id");
                    Optional<User> insertedUser = findOne(user_id);

                    if(insertedUser.isPresent()) {
                        return insertedUser;
                    }
                }

            }
            return Optional.empty();



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<User> findOne(Long id) {

        String selectSQLStatement = "select * from users where user_id=?;";
        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(selectSQLStatement)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return getResultSet(resultSet).stream().findFirst();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Iterable<User> findAll() {

        String sqlStatement = "select * from users;";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)
        ) {

            ResultSet resultSet = preparedStatement.executeQuery();

            return getResultSet(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> update(User entity) {
        String alterSQLStatement = "update users set first_name = ?, last_name = ? where user_id = ?;";
        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(alterSQLStatement)) {

            preparedStatement.setString(1, entity.getFirstName());
            preparedStatement.setString(2, entity.getLastName());
            preparedStatement.setLong(3, entity.getID());

            int response = preparedStatement.executeUpdate();
            return response == 1 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public Optional<User> delete(Long id) {
        String deleteSQLStatement = "delete from users where user_id=?;";
        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(deleteSQLStatement)) {
            preparedStatement.setLong(1, id);
            Optional<User> user = findOne(id);
            int response = 0;
            if(user.isPresent()) {
                response = preparedStatement.executeUpdate();
            }
            return response == 1 ? user : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<User> getResultSet(ResultSet resultSet) throws SQLException {

        Set<User> users = new HashSet<>();


        while(resultSet.next()) {
            Long id = resultSet.getLong("user_id");
            String first_name = resultSet.getString("first_name");
            String last_name = resultSet.getString("last_name");
            User user = new User(id,first_name,last_name);
            users.add(user);
        }

        return users;

    }

}
