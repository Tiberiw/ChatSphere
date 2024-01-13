package org.map.socialnetwork.repository.database;

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

public class FriendshipDatabaseRepository implements Repository<Pair<Long,Long>, Friendship> {

    private final Repository<Long,User> userRepository;

    private final Validator<Friendship> validator;

    public FriendshipDatabaseRepository(Validator<Friendship> validator, Repository<Long,User> userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {

        if(entity == null) {
            return Optional.empty();
        }

        validator.validate(entity);

        //Check if the friendship exists
        Optional<Friendship> friendship = findOne(entity.getID());
        if(friendship.isPresent())
            return Optional.empty();

        //Prepare the sqlStatement to be executed
        String sqlStatement = "insert into friendships(user_id1, user_id2, friends_from) " +
                                "values (?,?,?);";

        //Execute the command and get the result from DB
        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {

            preparedStatement.setLong(1, entity.getFirstUser().getID());
            preparedStatement.setLong(2, entity.getSecondUser().getID());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(entity.getFormattedDate()));

            int result = preparedStatement.executeUpdate();
            return result==1 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Friendship> findOne(Pair<Long, Long> friendshipId) {


        String sqlStatement = "select * from friendships where user_id1 = ? and user_id2 = ?;";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {
            preparedStatement.setLong(1, friendshipId.getFirst());
            preparedStatement.setLong(2, friendshipId.getSecond());

            ResultSet resultSet = preparedStatement.executeQuery();

            return getResultSet(resultSet).stream().findFirst();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Friendship> findAll() {

        String sqlStatement = "select * from friendships;";
        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            return getResultSet(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {

        if(entity == null)
            return Optional.empty();

        validator.validate(entity);

        String sqlStatement = "update friendships set friends_from = ? " +
                                "where user_id1 = ? and user_id2 = ?;";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(entity.getFormattedDate()));
            preparedStatement.setLong(2, entity.getFirstUser().getID());
            preparedStatement.setLong(3, entity.getSecondUser().getID());

            int result = preparedStatement.executeUpdate();
            return result == 1 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Friendship> delete(Pair<Long, Long> friendshipId) {

        String sqlStatement = "delete from friendships where user_id1 = ? and user_id2 = ?;";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(sqlStatement)) {

            preparedStatement.setLong(1, friendshipId.getFirst());
            preparedStatement.setLong(2, friendshipId.getSecond());

            Optional<Friendship> friendship = findOne(friendshipId);


            if(friendship.isPresent()) {
                int result = preparedStatement.executeUpdate();
                return result == 1 ? friendship : Optional.empty();
            }

            return Optional.empty();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Friendship> getResultSet(ResultSet resultSet) throws SQLException {

        Set<Friendship> friendships = new HashSet<>();


        while(resultSet.next()) {
            Long user_id1 = resultSet.getLong("user_id1");
            Long user_id2 = resultSet.getLong("user_id2");
            LocalDateTime date = resultSet.getTimestamp("friends_from").toLocalDateTime();

            Optional<User> user1 = userRepository.findOne(user_id1);
            Optional<User> user2 = userRepository.findOne(user_id2);

            if(user1.isPresent() && user2.isPresent()) {
                Friendship friendship = new Friendship(user1.get(), user2.get(), date);
                friendships.add(friendship);
            }
        }

        return friendships;
    }


}
