package org.map.socialnetwork.ui;

import org.map.socialnetwork.domain.Friendship;
import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.exception.RepositoryException;
import org.map.socialnetwork.exception.ValidatorException;
import org.map.socialnetwork.service.FriendshipService;
import org.map.socialnetwork.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConsoleUI {

    private final UserService userService;
    private final FriendshipService friendshipService;
    private final Map<String,Consumer<String[]>> commands;
    Supplier<String> requestGetter;
    Consumer<String> requestHandler;
    private boolean run;
    public ConsoleUI(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.commands = new HashMap<>();
        this.run = true;
        initCommands();
        initRequests();
    }

    private void initCommands() {

        BiFunction<String[], Integer, Boolean> lengthCheck = (params,size) -> params.length == size;

        commands.put("add_user", params -> {
            if(!lengthCheck.apply(params,3)) {
                System.out.println("Provide 2 parameters!");
                return;
            }
            String firstName = params[1];
            String lastName = params[2];

            //Try to add user
            Optional<User> newUser = userService.saveToRepository(firstName,lastName);

            newUser.ifPresent( user -> System.out.println("User created!"));
        });

        commands.put("rm_user", params -> {
            if(!lengthCheck.apply(params,2)) {
                System.out.println("Provide 1 parameter!");
                return;
            }

            String id = params[1];

            Optional<User> oldUser = userService.removeFromRepository(Long.valueOf(id));
            oldUser.ifPresent( usr -> {
                System.out.println(usr);
                System.out.println("User removed");
            });


        });

        commands.put("get_all_users", params -> {
            System.out.println("All Users:");
            userService.getAll().forEach(user -> System.out.println(user.toString()));
        });


        commands.put("add_friendship", params -> {

            String date;
            if(lengthCheck.apply(params,3)) {
               date = "";
            } else if (lengthCheck.apply(params,4)) {
                date = params[3];
            }else {
                System.out.println("Provide 2 parameters!");
                return;
            }

            Long firstId = Long.valueOf(params[1]);
            Long secondId = Long.valueOf(params[2]);


            //Try to add friendship
            Optional<Friendship> newFriendship = friendshipService.saveToRepository(firstId, secondId, date);
            newFriendship.ifPresent( friendship -> {
                System.out.println(friendship);
                System.out.println("Friendship added!");
            });
        });

        commands.put("rm_friendship", params -> {
            if(!lengthCheck.apply(params,3)) {
                System.out.println("Provide 2 parameters!");
                return;
            }

            Long firstId = Long.valueOf(params[1]);
            Long secondId = Long.valueOf(params[2]);

            //Try to remove friendship
            Optional<Friendship> oldFriendship = friendshipService.removeFromRepository(firstId, secondId);

            oldFriendship.ifPresent(friendship -> {
                System.out.println(friendship);
                System.out.println("Friendship removed!");
            });

        });

        commands.put("mod_friendship", params -> {
            if(!lengthCheck.apply(params,4)) {
                System.out.println("Provide 3 parameters!");
                return;
            }
            Long firstId = Long.valueOf(params[1]);
            Long secondId = Long.valueOf(params[2]);
            String date = params[3];
            Optional<Friendship> friendship = friendshipService.updateToRepository(firstId, secondId, date);
            friendship.ifPresent( oldFr -> System.out.println("Friendship updated!"));
        });

        commands.put("get_all_friendships", params -> {
            System.out.println("All Friendships:");
            friendshipService.getAll().forEach(friendship -> System.out.println(friendship.toString()));
        });

        ///TODO ADD FEATURE AFTER DB Repo
        commands.put("get_fr_user", params -> {
            if(!lengthCheck.apply(params,2)) {
                System.out.println("Provide 1 parameter!");
                return;
            }
            Long userId = Long.valueOf(params[1]);
            Optional<User> user = userService.getFromRepository(userId);
            friendshipService.getAllFriendsUser(userId).forEach(System.out::println);

        });

        commands.put("get_fr_user_month", params -> {
            if(!lengthCheck.apply(params,3)) {
                System.out.println("Provide 2 parameters!");
                return;
            }
            Long userId = Long.valueOf(params[1]);
            Optional<User> user = userService.getFromRepository(userId);
            friendshipService.getAllFriendsUserMonth(userId, params[2]).forEach(System.out::println);

        });

        commands.put("show_network", params -> System.out.println(friendshipService.showNetwork()));

        commands.put("get_components_nr", params -> System.out.println(friendshipService.getCommunityNumber()));

        commands.put("get_components", params -> System.out.println(friendshipService.getCommunities()));

        commands.put("get_most_social", params -> System.out.println(friendshipService.getMostSocial()));

        commands.put("exit", params -> this.run = false);

    }

    private void initRequests() {
        //Get the commands from the command line
        requestGetter = new Scanner(System.in)::nextLine;

        //Process the command and call the handler function
        requestHandler = command -> {
            //Get command string and split the parameters
            String[] params = command.split("\\s");

            //Validate the command
            Predicate<String> commandValidator = this.commands::containsKey;

            //Execute the function
            if(commandValidator.test(params[0]))
                try {
                    commands.get(params[0]).accept(params);
                }catch(IllegalArgumentException | ValidatorException | RepositoryException e) {
                    System.out.println(e.getMessage());
                }
            else {
                System.out.println("Incorrect command!");
            }
        };
    }



    public void run() {
//        while(run)
//            this.requestHandler.accept(requestGetter.get());
        commands.get("get_all_users").accept(new String[]{"get_all_users"});
    }
}
