package course.spring.hospitalinformationsystem.service.impl;

import course.spring.hospitalinformationsystem.dao.DecursusRepository;
import course.spring.hospitalinformationsystem.dao.UserRepository;
import course.spring.hospitalinformationsystem.dao.WardRepository;
import course.spring.hospitalinformationsystem.entity.Decursus;
import course.spring.hospitalinformationsystem.entity.User;
import course.spring.hospitalinformationsystem.entity.Ward;
import course.spring.hospitalinformationsystem.exception.InvalidEntityDataException;
import course.spring.hospitalinformationsystem.exception.MethodNotAllowedException;
import course.spring.hospitalinformationsystem.exception.NonExistingEntityException;
import course.spring.hospitalinformationsystem.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepo;
    private WardRepository wardRepo;
    private DecursusRepository decursusRepo;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, WardRepository wardRepo, DecursusRepository decursusRepo) {
        this.userRepo = userRepo;
        this.wardRepo = wardRepo;
        this.decursusRepo = decursusRepo;
    }

    /**
     * @return Collection of all Users in the repository
     */
    @Override
    @Transactional(readOnly = true)
    @PostFilter("filterObject.id == authentication.principal.id or hasRole('ADMINISTRATOR')")
    public Collection<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * @param id of the User that will be returned
     * @return User with param id
     * @throws NonExistingEntityException if User with param ID is not present in the repository
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMINISTRATOR')")
    public User getUserById(Long id) throws NonExistingEntityException {
        return userRepo.findById(id).orElseThrow(() -> new NonExistingEntityException(
                String.format("User with id='%d' does not exist.", id)));
    }

    /**
     * @param username of the User that will be returned
     * @return User with param username
     * @throws NonExistingEntityException if User with param username is not present in the repository
     */
    @Override
    public User getUserByUsername(String username) throws NonExistingEntityException {
        return userRepo.findByUsername(username).orElseThrow(() -> new NonExistingEntityException(
                String.format("User with username='%s' does not exist.", username)
        ));
    }

    /**
     * @param user that will be added to the repository
     * @return the created User entity
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    public User addUser(User user) throws InvalidEntityDataException {
        user.setId(null);
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new InvalidEntityDataException(String.format
                    ("User with username ='%s' already exists.", user.getUsername()));
        }
        if (userRepo.findByEmailAddress(user.getEmailAddress()).isPresent()) {
            throw new InvalidEntityDataException(String.format
                    ("User with email address='%s' already exists.", user.getEmailAddress()));
        }
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreated(LocalDateTime.now());
        user.setModified(LocalDateTime.now());
        User savedUser = userRepo.save(user);
        savedUser.setUsername(user.getLastName().concat(user.getFirstName().substring(0, 1).concat(String.valueOf(user.getId()))));
        return userRepo.save(savedUser);

    }

    /**
     * @param user that will be updated
     * @return the updated User
     * @throws NonExistingEntityException in case the input User's ID does not match any existing User in the repository
     * @throws InvalidEntityDataException in case of invalid input data
     */
    @Override
    public User updateUser(User user) throws NonExistingEntityException, InvalidEntityDataException {
        User oldUser = getUserById(user.getId());
        if (!user.getUsername().equals(oldUser.getUsername()) && userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new InvalidEntityDataException(String.format
                    ("Username ='%s' can not be changed", user.getUsername()));
        }
        if (!user.getEmailAddress().equals(oldUser.getEmailAddress()) && userRepo.findByEmailAddress(user.getEmailAddress()).isPresent()) {
            throw new InvalidEntityDataException(String.format
                    ("Email address ='%s' can not be changed", user.getEmailAddress()));
        }
        user.setCreated(oldUser.getCreated());
        user.setModified(LocalDateTime.now());
        return userRepo.save(user);
    }

    /**
     * @param id of the User that will be deleted
     * @return the User that was deleted
     * @throws NonExistingEntityException if User with param id is not present in the repository
     */
    @Override
    public User deleteUserById(Long id) throws NonExistingEntityException {
        User oldUser = getUserById(id);
        List<Decursus> decursusList = decursusRepo.findAll();
        decursusList.stream().forEach(d -> {
            if (d.getAddedBy().equals(oldUser)) {
                throw new MethodNotAllowedException("User cannot be deleted");
            }
        });
        userRepo.deleteById(id);
        updateWardStaffList(oldUser);
        return oldUser;
    }

    /**
     * @return count of all Users in the repository
     */
    @Override
    public long count() {
        return userRepo.count();
    }

    private void updateWardStaffList(User oldUser) {
        List<Ward> wards = wardRepo.findAll();
        for (int i = 0; i < wards.size(); i++) {
            List<User> staff = new ArrayList<>(wards.get(i).getStaff());
            if (staff.contains(oldUser)) {
                staff.remove(oldUser);
                wards.get(i).setStaff(new HashSet<>(staff));
                wards.get(i).setModified(LocalDateTime.now());
                wardRepo.save(wards.get(i));
            }
        }
    }
}
