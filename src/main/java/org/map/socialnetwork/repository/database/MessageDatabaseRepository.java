package org.map.socialnetwork.repository.database;

import org.map.socialnetwork.domain.Message;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.repository.ConnectionManager;
import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.sql.Types.BIGINT;

public class MessageDatabaseRepository implements Repository<Long, Message> {

    private final Repository<Long, User> userRepository;
    private final Validator<Message> validator;

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


            preparedStatement1.setLong(1, entity.getSender().getID());
            preparedStatement1.setString(2, entity.getMessage());
            preparedStatement1.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            if( entity.getReplyTo() == null) {
                preparedStatement1.setNull(4, BIGINT);
            }else {
                preparedStatement1.setLong(4, ( entity.getReplyTo().getID()));
            }


            int response = preparedStatement1.executeUpdate();

            //insert in received_messages table
            if(response == 1) {
                ResultSet resultSet1 = preparedStatement2.executeQuery();
                if(resultSet1.next()) {

                    //Get ID of inserted message
                    long messageID = resultSet1.getLong("id");

                    //Add in received_messages(m:n)
                    preparedStatement3.setLong(1, messageID);

                    for(User user : entity.getReceivers()) {
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
        try( PreparedStatement preparedStatement = ConnectionManager.getInstance()
                .getConnection()
                .prepareStatement(selectSQLStatement);) {

            preparedStatement.setLong(1, messageID);
            ResultSet resultSet = preparedStatement.executeQuery();

            return getResultSet(resultSet).stream().findFirst();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Iterable<Message> findAll() {

        String selectSQLStatement = "select * from messages;";
        try(PreparedStatement preparedStatement = ConnectionManager.getInstance()
                .getConnection()
                .prepareStatement(selectSQLStatement);) {

            ResultSet resultSet = preparedStatement.executeQuery();
            return getResultSet(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Message> update(Message entity) {

        String SQLStatement = "update messages set message=?, date=? where message_id=?;";

        try(PreparedStatement preparedStatement = ConnectionManager
                .getInstance()
                .getConnection()
                .prepareStatement(SQLStatement)) {

            preparedStatement.setString(1, entity.getMessage());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(entity.getFormattedDate()));
            preparedStatement.setLong(3, entity.getID());

            int result = preparedStatement.executeUpdate();
            return result == 1 ? Optional.of(entity) : Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Message> delete(Long messageID) {
        String SQLStatement = "delete from messages where message_id=?;";
        try(PreparedStatement preparedStatement = ConnectionManager.getInstance()
                .getConnection()
                .prepareStatement(SQLStatement);) {
            preparedStatement.setLong(1, messageID);
            Optional<Message> message = findOne(messageID);

            int response = 0;
            if(message.isPresent()) {
                response = preparedStatement.executeUpdate();
            }
            return response == 1 ? message : Optional.empty();
        }catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Set<Message> getResultSet(ResultSet resultSet) throws SQLException {

        String SQLStatement = "select to_user_id from received_messages where message_id=?;";

        Set<Message> messages = new HashSet<>();

        while(resultSet.next()) {
            Long messageID = resultSet.getLong("message_id");
            Long from_user_id = resultSet.getLong("from_user");
            Optional<User> fromUser = userRepository.findOne(from_user_id);
            if(fromUser.isPresent()) {
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                long reply = resultSet.getLong("reply");
                Optional<Message> replyMessageOpt = reply == 0 ? Optional.empty() : findOne(reply);
                try(PreparedStatement preparedStatement = ConnectionManager.getInstance().getConnection().prepareStatement(SQLStatement);) {
                    preparedStatement.setLong(1, messageID);
                    ResultSet partialResultSet = preparedStatement.executeQuery();
                    messages.add(new Message(messageID, fromUser.get(), getPartialResultSet(partialResultSet), message, date, replyMessageOpt.orElse(null)));
                }catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }


        }

        return messages;
    }

    private List<User> getPartialResultSet(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while(resultSet.next()) {
            Long userId = resultSet.getLong("to_user_id");
            Optional<User> user = userRepository.findOne(userId);
            user.ifPresent(users::add);
        }

        return users;
    }
}
