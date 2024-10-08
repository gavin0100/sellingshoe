package com.data.filtro.service;

//@Service
public class AccountService {
//    private final UserService userService;
//    @Autowired
//    AccountRepository accountRepository;
//
//
//    @Autowired
//    public AccountService(UserService userService) {
//        this.userService = userService;
//    }
//
//    public Page<Account> getAllPaging(Pageable pageable) {
//        return accountRepository.findAll(pageable);
//    }
//
//    public Account getAccountByName(String accountName) {
//        return accountRepository.findAccountByName(accountName);
//    }
//
//    public Account getAccountById(int id) {
//        return accountRepository.findById(id);
//    }
//
//
//    public Account getByPasswordResetToken(String token) {
//        return accountRepository.findByPasswordResetToken(token);
//    }
//
//    public Account getbyEmail(String email){
//        return accountRepository.findByEmail(email);
//    }
//
//    public List<Account> getActiveAccount(int status){
//        return accountRepository.activeAccounts(status);
//    }
//
//    public List<Account> getUserAccount(int roleNumber){
//        return accountRepository.userAccounts(roleNumber);
//    }
//
//    public void updateResetPasswordToken(String token, String email) {
//        Account account;
//        User user = userService.getUserByEmail(email);
//        if (user != null) {
//            account = getAccountByName(user.getAccount().getAccountName());
//            account.setPasswordResetToken(token);
//            accountRepository.save(account);
//        } else {
//            throw new UserNotFoundException("User not found with the email address: " + email);
//        }
//    }
//
//    public void updatePassword(Account account, String newPassword) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String hashPassword = passwordEncoder.encode(newPassword);
//        account.setPassword(hashPassword);
//        account.setPasswordResetToken(null);
//        accountRepository.save(account);
//    }
//
//    public boolean checkAccountName(String accountName) {
//        Account existingAccount = getAccountByName(accountName.trim());
//        if (existingAccount != null)
//            return true;
//        else
//            return false;
//    }
//
//    public Account authenticateUser(String accountName, String password) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        Account tempAccount;
//        try{
//            tempAccount = getAccountByName(accountName.trim());
//            if (tempAccount.getRoleNumber() != 3){
//                throw new AuthenticationAccountException("Incorrect Account");
//            }
//            if (tempAccount != null) {
//                if (passwordEncoder.matches(password, tempAccount.getPassword())) {
//                    Account authenticateAccount = accountRepository.authenticate(accountName, tempAccount.getPassword());
//                    return authenticateAccount;
//                } else {
////                System.out.println("sai mat khau");
//                    throw new AuthenticationAccountException("Incorrect Password!");
//                }
//            } else {
//                throw new AuthenticationAccountException("Incorrect AccountName!");
//            }
//        } catch (Exception exception){
//            throw new AuthenticationAccountException("Incorrect Account");
//        }
//    }
//
//    public Account authenticateAdmin(String accountName, String password) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        Account tempAccount = getAccountByName(accountName.trim());
//        if (tempAccount != null) {
//            if (passwordEncoder.matches(password, tempAccount.getPassword())) {
//                return accountRepository.authenticateAdmin(accountName, tempAccount.getPassword());
//            } else {
//                throw new AuthenticationAccountException("Incorrect Password!");
//            }
//        } else {
//            throw new AuthenticationAccountException("Incorrect AccountName!");
//        }
//    }
//
//    public void changePassword(Account account, String currentPassword, String newPassword, String repeatPassword) throws NotFoundException {
//        if (account == null) {
//            throw new NotFoundException("Account not found!");
//        }
//        if (!newPassword.equals(repeatPassword)) {
//            throw new PasswordDoNotMatchException("Incorrrect password !");
//        }
//        String userPassword = account.getPassword();
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        if (passwordEncoder.matches(currentPassword, userPassword)) {
//            String hashPassword = passwordEncoder.encode(newPassword);
//            account.setPassword(hashPassword);
//            accountRepository.save(account);
//        } else {
//            throw new AuthenticationAccountException("Incorrrect password!");
//        }
//
//    }
//
//    public void create(Account account) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        Account newAccount = new Account();
//        newAccount.setAccountName(account.getAccountName());
//        newAccount.setPassword(passwordEncoder.encode(account.getPassword()));
//        newAccount.setCreatedDate(new Date());
//        newAccount.setRoleNumber(account.getRoleNumber());
//        newAccount.setStatus(account.getStatus());
//        accountRepository.save(newAccount);
//    }
//
//    public void update(Account account) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        Account newAccount = getAccountById(account.getId());
//        newAccount.setAccountName(account.getAccountName());
//        if (!account.getPassword().isEmpty()) { // check if password has been changed
//            String encodedPassword = passwordEncoder.encode(account.getPassword());
//            newAccount.setPassword(encodedPassword);
//        }
//        newAccount.setCreatedDate(new Date());
//        newAccount.setRoleNumber(account.getRoleNumber());
//        newAccount.setStatus(account.getStatus());
//        accountRepository.save(newAccount);
//    }
//
//    public void updateRole(Account account){
//        accountRepository.save(account);
//    }
//    public void delete(int id) {
//        accountRepository.deleteById(id);
//    }
//
//    public List<Account> getAppropriateAccountForUser() {
//        return accountRepository.findAppropriateAccountForUser();
//    }
//
//    public List<Account> getEligibleAccountForStaff() {
//        return accountRepository.findEligibleAccountForStaff();
//    }


}
