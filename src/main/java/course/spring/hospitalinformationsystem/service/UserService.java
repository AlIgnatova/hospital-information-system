package course.spring.hospitalinformationsystem.service;

import course.spring.hospitalinformationsystem.entity.User;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();
    User getUserById(Long id) throws NonExistingEntityException;
    User getUserByUsername(String username) throws NonExistingEntityException;
    User addUser(User user) throws InvalidEntityDataException;
    User updateUser(User user) throws NonExistingEntityException, InvalidEntityDataException;
    User deleteUserById(Long id) throws NonExistingEntityException;
    long count();
}
