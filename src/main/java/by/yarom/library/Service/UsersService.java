package by.yarom.library.Service;

import by.yarom.library.Entity.Users;

import java.util.List;

public interface UsersService {

     void addUser(Users user);

     void updateUser(Users user);

     void deleteUser(int id);

     Users getUserByLogin(String login);

     Users getUserByEmail(String email);

     List<Users> listUsers(Integer page);

     Long countFindUsers();
}
