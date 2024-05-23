package com.data.filtro.repository;

import com.data.filtro.model.Account;
import com.data.filtro.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    //@Query("select distinct c.user from Cart c")
    //@Query("select distinct c.user from Cart c join fetch User u")
    //@Query("select u from User u inner join User on u.id in (select distinct c.user.id from Cart c)")
    //@Query("SELECT DISTINCT u FROM User u LEFT JOIN fetch Cart c on u.id = c.user.id order by u.id")
    //@Query("SELECT distinct u FROM User u LEFT JOIN FETCH u.cart c GROUP BY u.id")
    List<User> findAll();


    @Query("select u from User u where u.id =:id")
    User findUserById(@Param("id") int id);

    @Query("select u from User u where u.email =:email")
    User findByEmail(@Param("email") String email);

    @Query("select u from User u where u.name =:name")
    User findByUserName(@Param("name") String name);

    @Query("select u from User u where u.accountName =:accountName")
    User findByAccountName(@Param("accountName") String accountName);


    @Query("select u from User u")
    Page<User> findAll(Pageable pageable);

    @Query("select u from User u where u.userPermission.permissionId = 4")
    Page<User> findAllUser(Pageable pageable);

    @Query("select u from User u where u.userPermission.permissionId != 4")
    Page<User> findAllStaff(Pageable pageable);


    @Query("select a from User a where a.accountName = :accountName")
    User findAccountByName(@Param("accountName") String accountName);


    @Query("select a from User a  where a.accountName = :accountName and a.password=:password and a.userPermission.permissionId=4")
    User authenticate(@Param("accountName") String accountName, @Param("password") String password);

    @Query("select a from User a  where a.accountName = :accountName and a.password=:password and a.userPermission.permissionId=1")
    User authenticateAdmin(@Param("accountName") String accountName, @Param("password") String password);


    @Query("select a from User  a where a.passwordResetToken = :token")
    User findByPasswordResetToken(@Param("token") String token);

//    @Query("select a from User  a where a.user.email = :email")
//    User findByEmail(@Param("email") String email);

    @Query("select a from  User  a where a.id =:id")
    User findById(@Param("id") int id);

    @Query("select a from User a where a.status = :status")
    List<User> activeUsers(@Param("status") int status);

    @Query("select a from User a where a.userPermission.permissionId = :role")
    List<User> userUsers(@Param("role") int role);

    @Query("select a from User a where a.userPermission.permissionId = 4")
    List<User> findAppropriateUserForUser();

    @Query("select a from User a where a.userPermission.permissionId != 4")
    List<User> findEligibleUserForStaff();

}
