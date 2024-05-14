package com.data.filtro.repository;

import com.data.filtro.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Integer> {
    UserPermission findByPermissionId(int id);
}
