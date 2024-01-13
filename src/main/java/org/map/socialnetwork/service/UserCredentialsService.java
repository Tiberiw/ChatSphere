package org.map.socialnetwork.service;

import org.map.socialnetwork.domain.User;
import org.map.socialnetwork.domain.UserCredentials;
import org.map.socialnetwork.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserCredentialsService {

    Repository<User, UserCredentials> userUserCredentialsRepository;

    public UserCredentialsService(Repository<User, UserCredentials> userUserCredentialsRepository) {
        this.userUserCredentialsRepository = userUserCredentialsRepository;
    }

    public Optional<UserCredentials> saveToRepository(User user, String email, String phoneNumber, String password) {
        Optional<UserCredentials> result = userUserCredentialsRepository.save( new UserCredentials(user,email,phoneNumber,password));

        return result;
    }

    public Optional<UserCredentials> authenticateUser(String email, String password) {

        List<UserCredentials> userCredentials = StreamSupport.stream(userUserCredentialsRepository.findAll().spliterator(), false).toList();

        return userCredentials.stream()
                .filter(userCredentials1 -> userCredentials1.getEmail().equals(email) && userCredentials1.getPassword().equals(password))
                .findFirst();
    }

    public boolean checkAccount(String email) {
        List<UserCredentials> userCredentials = StreamSupport.stream(userUserCredentialsRepository.findAll().spliterator(), false).toList();
        return userCredentials.stream()
                .anyMatch(uc -> uc.getEmail().equals(email));
    }
}
