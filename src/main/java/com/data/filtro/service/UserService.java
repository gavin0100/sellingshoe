package com.data.filtro.service;

import com.data.filtro.exception.AccountNameExistException;
import com.data.filtro.exception.AuthenticationAccountException;
import com.data.filtro.exception.PasswordDoNotMatchException;
import com.data.filtro.exception.UserNotFoundException;
import com.data.filtro.model.Account;
import com.data.filtro.model.Cart;
import com.data.filtro.model.DTO.UserDTO;
import com.data.filtro.model.User;
import com.data.filtro.model.UserPermission;
import com.data.filtro.repository.AccountRepository;
import com.data.filtro.repository.UserPermissionRepository;
import com.data.filtro.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.ParseException;
import java.util.Date;
import java.util.List;


@Service
public class UserService implements UserDetailsService {
    public UserService() {
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    CartService cartService;

    public User getUserById(int id) {
        return userRepository.findUserById(id);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public User getUserByName(String name) {
        return userRepository.findByUserName(name);
    }

    public Page<User> getAllPaging(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> getAllPagingUser(Pageable pageable) {
        return userRepository.findAllUser(pageable);
    }

    public Page<User> getAllPagingStaff(Pageable pageable) {
        return userRepository.findAllStaff(pageable);
    }


    public void registerUser(String userName, String accountName, String email, String password, String repeatPassword) {

        if (checkUserName(accountName)) {
            throw new AccountNameExistException("Tên tài khoản đã được đặt");
        }

        if (!password.equals(repeatPassword)) {
            throw new PasswordDoNotMatchException("Không đúng mật khẩu !");
        }

        User user = new User();
        user.setName(userName);
        user.setEmail(email);
        userRepository.save(user);
        user.setAccountName(accountName);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashPassword = passwordEncoder.encode(password);
        user.setPassword(hashPassword);
        user.setCreatedDate(new Date());
        UserPermission userPermission = userPermissionRepository.findByPermissionId(4);
        user.setUserPermission(userPermission);

        userRepository.save(user);
        Cart cart = cartService.createCart(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }




    public void create(UserDTO user) {
        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setDob(user.getDob());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setCity(user.getCity());
        newUser.setZip(user.getZip());
        newUser.setSex(user.getSex());
        newUser.setStatus(1);
        newUser.setAddress(user.getAddress());
        newUser.setAccountName(user.getAccountName());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashPassword = passwordEncoder.encode(user.getPassword());
        newUser.setPassword(hashPassword);


        newUser.setCreatedDate(new Date());
        UserPermission userPermission = userPermissionRepository.findByPermissionId(user.getUserPermissionId());
        newUser.setUserPermission(userPermission);
        userRepository.save(newUser);
    }


    public void update(UserDTO user) {
        User newUser = getByUserId(user.getId());
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setAddress(user.getAddress());
        newUser.setPhoneNumber(user.getPhoneNumber());
        newUser.setCity(user.getCity());
        newUser.setZip(user.getZip());
        newUser.setDob(user.getDob());
        newUser.setSex(user.getSex());
        newUser.setStatus(user.getStatus());
        newUser.setAccountName(user.getAccountName());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashPassword = passwordEncoder.encode(user.getPassword());
        newUser.setPassword(hashPassword);

        UserPermission userPermission = userPermissionRepository.findByPermissionId(user.getUserPermissionId());
        newUser.setUserPermission(userPermission);
        userRepository.save(newUser);
    }

        public void updateUser(User newUser) throws NotFoundException, ParseException {
            User user = userRepository.findById(newUser.getId()).get();
            if (user == null) {
                throw new NotFoundException("Không tìm thấy tài khoản thích hơp!");
            }

            user.setId(newUser.getId());

            if (newUser.getName() != null) {
                user.setName(newUser.getName());
            }

            if (newUser.getEmail() != null) {
                user.setEmail(newUser.getEmail());
            }
            if (newUser.getAddress() != null) {
                user.setAddress(newUser.getAddress());
            }
            if (newUser.getZip() != null) {
                user.setZip(newUser.getZip());
            }
            if (newUser.getPhoneNumber() != null) {
                user.setPhoneNumber(newUser.getPhoneNumber());
            }
            if (newUser.getDob() != null) {
                user.setDob(newUser.getDob());
            }
            if (newUser.getCity() != null) {
                user.setCity(newUser.getCity());
            }
            if (newUser.getSex() != null) {
                user.setSex(newUser.getSex());
            }

            if (newUser.getAccountName() != null) {
                user.setAccountName(newUser.getAccountName());
            }
            if (newUser.getPassword() != null) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashPassword = passwordEncoder.encode(newUser.getPassword());
                user.setPassword(hashPassword);
            }

            if (newUser.getUserPermission() != null) {
                user.setUserPermission(newUser.getUserPermission());
            }


            userRepository.save(user);

        }

    public User getByUserId(int id) {
        return userRepository.findUserById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String accountName) throws UsernameNotFoundException {
        return userRepository.findByAccountName(accountName);
    }


//    public void sendMail(String userMailAdress, String subject, String message) {
//        SimpleMailMessage mail = new SimpleMailMessage();
//        JavaMailSender javaMailSender = new JavaMailSenderImpl();
//        mail.setTo(userMailAdress);
//        mail.setSubject(subject);
//        mail.setText(message);
//        mail.setFrom("phutv1990@gmail.com");
//        javaMailSender.send(mail);
//    }


//    public Page<Account> getAllPaging(Pageable pageable) {
//        return accountRepository.findAll(pageable);
//    }

    public User getUserByAccountName(String accountName) {
        return userRepository.findAccountByName(accountName);
    }

//    public User getAccountById(int id) {
//        return accountRepository.findById(id);
//    }


    public User getByPasswordResetToken(String token) {
        return userRepository.findByPasswordResetToken(token);
    }

//    public User getByEmail(String email){
//        return userRepository.findByEmail(email);
//    }

    public List<User> getActiveUser(int status){
        return userRepository.activeUsers(status);
    }

    public List<User> getUserAccount(int roleNumber){
        return userRepository.userUsers(roleNumber);
    }

    public void updateResetPasswordToken(String token, String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            user.setPasswordResetToken(token);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException("User not found with the email address: " + email);
        }
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashPassword);
        user.setPasswordResetToken(null);
        userRepository.save(user);
    }

    public boolean checkUserName(String accountName) {
        User existingUser = getUserByAccountName(accountName.trim());
        if (existingUser != null)
            return true;
        else
            return false;
    }

    public User authenticateUser(String accountName, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User tempUser;
        try{
            tempUser = getUserByAccountName(accountName.trim());
            if (tempUser.getUserPermission().getPermissionId() != 4){
                throw new AuthenticationAccountException("Incorrect Account");
            }
            if (tempUser != null) {
                if (passwordEncoder.matches(password, tempUser.getPassword())) {
                    User authenticateAccount = userRepository.authenticate(accountName, tempUser.getPassword());
                    return authenticateAccount;
                } else {
//                System.out.println("sai mat khau");
                    throw new AuthenticationAccountException("Incorrect Password!");
                }
            } else {
                throw new AuthenticationAccountException("Incorrect AccountName!");
            }
        } catch (Exception exception){
            throw new AuthenticationAccountException("Incorrect Account");
        }
    }

    public User authenticateAdmin(String accountName, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User tempUser = getUserByAccountName(accountName.trim());
        if (tempUser != null) {
            if (passwordEncoder.matches(password, tempUser.getPassword())) {
                return userRepository.authenticateAdmin(accountName, tempUser.getPassword());
            } else {
                throw new AuthenticationAccountException("Incorrect Password!");
            }
        } else {
            throw new AuthenticationAccountException("Incorrect AccountName!");
        }
    }

    public void changePassword(User user, String currentPassword, String newPassword, String repeatPassword) throws NotFoundException {
        if (user == null) {
            throw new NotFoundException("Account not found!");
        }
        if (!newPassword.equals(repeatPassword)) {
            throw new PasswordDoNotMatchException("Incorrrect password !");
        }
        String userPassword = user.getPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(currentPassword, userPassword)) {
            String hashPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashPassword);
            userRepository.save(user);
        } else {
            throw new AuthenticationAccountException("Incorrrect password!");
        }

    }



    public void updateRole(User user){
        userRepository.save(user);
    }
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    public List<User> getAppropriateAccountForUser() {
        return userRepository.findAppropriateUserForUser();
    }

    public List<User> getEligibleAccountForStaff() {
        return userRepository.findEligibleUserForStaff();
    }


}
