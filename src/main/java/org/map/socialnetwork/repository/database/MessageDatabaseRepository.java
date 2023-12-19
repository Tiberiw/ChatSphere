package org.map.socialnetwork.repository.database;

import org.map.socialnetwork.domain.Friendship;
import org.map.socialnetwork.domain.Message;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.repository.ConnectionManager;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.validator.Validator;

import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.sql.Types.BIGINT;

public class MessageDatabaseRepository implements Repository<Long, Message> {

    private final Validator<Message> validator;

    private final Repository<Long, User> userRepository;

    public MessageDatabaseRepository(Validator<Message> validator, Repository<Long, User> userRepository) {
        this.validator = validator;
        this.userRepository = userRepository;
    }


    @Override
    public Optional<Message> save(Message entity) {
        if (entity == null)
            throw new IllegalArgumentException("Invalid Entity");

        validator.validate(entity);
        //The insert command
        String insertSQLStatement = "insert into messages(from_user, message, date, reply) values (?,?,?,?);";
        String selectIDSQLStatement = "select max(message_id) as id from messages;";
        String insertSQLStatement2 = "insert into received_messages(message_id,to_user_id) values(?,?);";

        try(
        PreparedStatement preparedStatement1 = ConnectionManager.getInstance().getConnection().prepareStatement(insertSQLStatement);
        PreparedStatement preparedStatement2 = ConnectionManager.getInstance().getConnection().prepareStatement(selectIDSQLStatement);
        PreparedStatement preparedStatement3 = ConnectionManager.getInstance().getConnection().prepareStatement(insertSQLStatement2)) {


            preparedStatement1.setLong(1, entity.getFrom().getID());
            preparedStatement1.setString(2, entity.getMessage());
            preparedStatement1.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            if( entity.getReply() == null) {
                preparedStatement1.setNull(4, BIGINT);
            }else {
                preparedStatement1.setLong(4, ( entity.getReply().getID()));
            }


            int response = preparedStatement1.executeUpdate();
            if(response == 1) {
                ResultSet resultSet1 = preparedStatement2.executeQuery();
                if(resultSet1.next()) {
                    //Get ID of inserted message
                    Long messageID = resultSet1.getLong("id");

                    //Add in received_messages(m:n)
                    preparedStatement3.setLong(1, messageID);
                    for(User user : entity.getTo()) {
                        preparedStatement3.setLong(2, user.getID());
                        preparedStatement3.executeUpdate();
                    }

                    return Optional.of(entity);

                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }

    @Override
    public Optional<Message> findOne(Long messageID) {

        String selectSQLStatement = "select * from messages where message_id=?;";
        String selectSQLStatement2 = "select to_user_id from received_messages where message_id=?";
        try(
        PreparedStatement preparedStatement1 = ConnectionManager.getInstance().getConnection().prepareStatement(selectSQLStatement);
        PreparedStatement preparedStatement2 = ConnectionManager.getInstance().getConnection().prepareStatement(selectSQLStatement2)) {

            preparedStatement1.setLong(1, messageID);
            ResultSet resultSet = preparedStatement1.executeQuery();
            if(resultSet.next()) {
                Long from_user_id = resultSet.getLong("from_user");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                long reply = resultSet.getLong("reply");
                Optional<User> fromUser = userRepository.findOne(from_user_id);
                Optional<Message> replyMessageOpt = reply == 0 ? Optional.empty() : findOne(reply);
                if( fromUser.isPresent()) {
                    preparedStatement2.setLong(1, messageID);
                    ResultSet resultSet2 = preparedStatement2.executeQuery();
                    List<User> toUsers = new ArrayList<>();
                    while(resultSet2.next()) {
                        Long userId = resultSet2.getLong("to_user_id");
                        Optional<User> user = userRepository.findOne(userId);
                        user.ifPresent(toUsers::add);
                    }
                    return Optional.of(new Message(messageID, fromUser.get(), toUsers, message, date,  replyMessageOpt.orElse(null)));
                }


            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();

        String selectSQLStatement = "select * from messages;";
        String selectSQLStatement2 = "select to_user_id from received_messages where message_id=?";


        try(PreparedStatement preparedStatement1 = ConnectionManager.getInstance().getConnection().prepareStatement(selectSQLStatement);
            PreparedStatement preparedStatement2 = ConnectionManager.getInstance().getConnection().prepareStatement(selectSQLStatement2)) {

            ResultSet resultSet = preparedStatement1.executeQuery();

            while(resultSet.next()) {

                Long messageID = resultSet.getLong("message_id");
                Long from_user_id = resultSet.getLong("from_user");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                long reply = resultSet.getLong("reply");
                Optional<User> fromUser = userRepository.findOne(from_user_id);
                Optional<Message> replyMessageOpt = reply == 0 ? Optional.empty() : findOne(reply);
                if( fromUser.isPresent()) {
                    preparedStatement2.setLong(1, messageID);
                    ResultSet resultSet2 = preparedStatement2.executeQuery();
                    List<User> toUsers = new ArrayList<>();
                    while(resultSet2.next()) {
                        Long userId = resultSet2.getLong("to_user_id");
                        Optional<User> user = userRepository.findOne(userId);
                        user.ifPresent(toUsers::add);
                    }
                    messages.add(new Message(messageID, fromUser.get(), toUsers, message, date,  replyMessageOpt.orElse(null)));
                }





            }

            return messages;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }





    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Set<Message> getResultSet(ResultSet resultSet) throws SQLException {
        return null;
    }
}
