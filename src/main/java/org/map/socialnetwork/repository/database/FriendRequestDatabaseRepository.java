package org.map.socialnetwork.repository.database;

import org.map.socialnetwork.domain.FriendRequest;
import org.map.socialnetwork.domain.Friendship;
import org.map.socialnetwork.domain.Pair;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.repository.ConnectionManager;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendRequestDatabaseRepository implements Repository<Long, FriendRequest> {
    private final Repository<Long, User> userRepository;

    private final Validator<FriendRequest> validator;

    public FriendRequestDatabaseRepository(Repository<Long, User> userRepository, Validator<FriendRequest> validator) {

        this.userRepository = userRepository;
        this.validator = validator;
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {

        if(entity == null) {
            System.out.println("Intra aici entity = null");
            return Optional.empty();
        }

        validator.validate(entity);

        //Check if the friendship exists
        if(entity.getID() != null) {
            Optional<FriendRequest> friendRequest = findOne(entity.getID());

            if(friendRequest.isPresent()) {
                System.out.println("Intra ca e deja prezent");
                return Optional.empty();

            }

        }

        //Prepare the sqlStatement to be executed
        String sqlStatement = "insert into friendrequests(user_id1, user_id2, status) " +
                "values (?,?,?);";

        //Execute the command and get the result from DB
        try(PreparedStatement preparedStatement = ConnectionManager
                                                    .getInstance()
                                                    .getConnection()
                                                    .prepareStatement(sqlStatement)) {

            preparedStatement.setLong(1, entity.getFirstUser().getID());
            preparedStatement.setLong(2, entity.getSecondUser().getID());
            preparedStatement.setString(3, entity.getStatus());

            int result = preparedStatement.executeUpdate();
            return result==1 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<FriendRequest> findOne(Long friendRequestID) {
        String sqlStatement = "select * from friendrequests where request_id = ?;";
        try(PreparedStatement preparedStatement = ConnectionManager.getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {
            preparedStatement.setLong(1, friendRequestID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                Long request_id = resultSet.getLong("request_id");

                Long user_id1 = resultSet.getLong("user_id1");
                Long user_id2 = resultSet.getLong("user_id2");

                String status = resultSet.getString("status");

                Optional<User> user1 = userRepository.findOne(user_id1);
                Optional<User> user2 = userRepository.findOne(user_id2);
                //We have to get the users Objects -> make a separate query on the users table in the DB
                if (user1.isPresent() && user2.isPresent()) {
                    FriendRequest friendRequest = new FriendRequest(request_id, user1.get(), user2.get(), status);
                    return Optional.of(friendRequest);
                }
            }

            return Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> friendRequests = new HashSet<>();
        String sqlStatement = "select * from friendrequests;";
        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Long request_id = resultSet.getLong("request_id");
                Long user_id1 = resultSet.getLong("user_id1");
                Long user_id2 = resultSet.getLong("user_id2");

                String status = resultSet.getString("status");

                Optional<User> user1 = userRepository.findOne(user_id1);
                Optional<User> user2 = userRepository.findOne(user_id2);

                if(user1.isPresent() && user2.isPresent()) {
                    FriendRequest friendRequest = new FriendRequest(request_id, user1.get(), user2.get(), status);
                    friendRequests.add(friendRequest);
                }
            }

            return friendRequests;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        if(entity == null)
            return Optional.empty();

        validator.validate(entity);

        String sqlStatement = "update friendrequests set status = ? " +
                "where request_id = ?;";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {

            preparedStatement.setString(1, entity.getStatus());
            preparedStatement.setLong(2, entity.getID());


            int result = preparedStatement.executeUpdate();
            return result == 1 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Long friendRequestID) {
        String sqlStatement = "delete from friendrequests where request_id = ?;";
        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {
            preparedStatement.setLong(1, friendRequestID);

            Optional<FriendRequest> friendRequest = findOne(friendRequestID);


            if(friendRequest.isPresent()) {
                int result = preparedStatement.executeUpdate();
                return result == 1 ? friendRequest : Optional.empty();
            }

            return Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
