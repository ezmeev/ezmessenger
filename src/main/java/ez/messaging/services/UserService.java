package ez.messaging.services;

import ez.messaging.data.User;

public class UserService {

    public UserService() {
    }

    public User getUser(String identity) {
        return new User(identity);
    }
}
