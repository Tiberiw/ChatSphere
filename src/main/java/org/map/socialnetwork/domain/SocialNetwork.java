package org.map.socialnetwork.domain;

import org.map.socialnetwork.repository.Repository;
import org.map.socialnetwork.repository.database.FriendshipDatabaseRepository;
import org.map.socialnetwork.repository.database.UserDatabaseRepository;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.UserService;
import org.map.socialnetwork.ui.ConsoleUI;
import org.map.socialnetwork.validator.FriendshipValidator;
import org.map.socialnetwork.validator.UserValidator;

public class SocialNetwork {

    private static SocialNetwork instance = null;
    UserService userService;
    FriendshipService friendshipService;
    ConsoleUI consoleUI;

    /// TODO IMPLEMENT SECOND INNER JOIN
    /// TODO IMPLEMENT MODIFY FRIENDSHIP

    private SocialNetwork() {

        String url = "jdbc:postgresql://localhost:5432/social_network";
        String username = "postgres";
        String password = "2103";
        Repository<Long, User> userRepository = new UserDatabaseRepository(new UserValidator());
        Repository<Pair<Long, Long>, Friendship> friendshipRepository = new FriendshipDatabaseRepository(new FriendshipValidator(), userRepository);
        userService = new UserService(userRepository);
        friendshipService = new FriendshipService(friendshipRepository, userRepository);
        consoleUI  = new ConsoleUI(userService,friendshipService);
    }

    public static SocialNetwork getInstance() {
       if(instance == null)
           instance = new SocialNetwork();

       return instance;
    }

    public void run() {
        consoleUI.run();
    }




}
