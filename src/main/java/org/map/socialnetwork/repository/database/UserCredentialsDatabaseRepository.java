package org.map.socialnetwork.repository.database;

import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.domain.UserCredentials;
import org.map.socialnetwork.repository.ConnectionManager;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.validator.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserCredentialsDatabaseRepository implements Repository<User, UserCredentials> {

    private final Validator<UserCredentials> validator;
    private final Repository<Long, User> userRepository;

    public UserCredentialsDatabaseRepository(Validator<UserCredentials> validator, Repository<Long, User> userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }
    @Override
    public Optional<UserCredentials> save(UserCredentials entity) {

        if(entity == null) {
            throw new IllegalArgumentException("Invalid entity");
        }

        validator.validate(entity);

        String SQLStatement = "insert into user_credentials values (?, ?, ?, ?);";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(SQLStatement)) {

            preparedStatement.setLong(1, entity.getID().getID());
            preparedStatement.setString(2, entity.getEmail());
            preparedStatement.setString(3, entity.getPhoneNumber());
            preparedStatement.setString(4, entity.getPassword());

            int response = preparedStatement.executeUpdate();
            return response == 1 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserCredentials> findOne(User user) {

        String selectSQLStatement = "select * from user_credentials where user_id=?;";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(selectSQLStatement)) {
            preparedStatement.setLong(1, user.getID());

            ResultSet resultSet = preparedStatement.executeQuery();

            return getResultSet(resultSet).stream().findFirst();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<UserCredentials> findAll() {

        String sqlStatement = "select * from user_credentials;";

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
    public Optional<UserCredentials> update(UserCredentials entity) {
        String SQLStatement = "update user_credentials set email = ?, phone_number = ?, password = ? where user_id = ?;";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(SQLStatement)) {

            preparedStatement.setString(1, entity.getEmail());
            preparedStatement.setString(2, entity.getPhoneNumber());
            preparedStatement.setString(3, entity.getPassword());
            preparedStatement.setLong(4, entity.getID().getID());

            int response = preparedStatement.executeUpdate();
            return response == 1 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserCredentials> delete(User user) {
        String SQLStatement = "delete from user_credentials where user_id = ?";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(SQLStatement)) {
            preparedStatement.setLong(1, user.getID());

            Optional<UserCredentials> userCredentials = findOne(user);
            int response = 0;

            if(userCredentials.isPresent()) {
                response = preparedStatement.executeUpdate();

            }

            return response == 1 ? userCredentials : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<UserCredentials> getResultSet(ResultSet resultSet) throws SQLException {

        Set<UserCredentials> userCredentials = new HashSet<>();

        while(resultSet.next()) {
            Long id = resultSet.getLong("user_id");
            String email = resultSet.getString("email");
            String phone_number = resultSet.getString("phone_number");
            String password = resultSet.getString("password");
            Optional<User> user = userRepository.findOne(id);
            user.ifPresent(usr -> userCredentials.add(new UserCredentials(usr,email,phone_number,password)));
        }


        return  userCredentials;
    }
}
