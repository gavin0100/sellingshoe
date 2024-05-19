package com.data.filtro.service;

import com.data.filtro.model.DTO.UserDTO;
import com.data.filtro.model.User;
import com.data.filtro.model.UserPermission;
import com.data.filtro.repository.UserPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserPermissionService {
    @Autowired
    UserPermissionRepository userPermissionRepository;


    public void update(UserPermission userPermission) {
        UserPermission newUserPermission = userPermissionRepository.findByPermissionId(userPermission.getPermissionId());
        newUserPermission.setCategoryManagement(userPermission.getCategoryManagement());
        newUserPermission.setOrderManagement(userPermission.getOrderManagement());
        newUserPermission.setProductManagement(userPermission.getProductManagement());
        newUserPermission.setUserManagement(userPermission.getUserManagement());
        newUserPermission.setStaffManagement(userPermission.getStaffManagement());
        newUserPermission.setMaterialManagement(userPermission.getMaterialManagement());
        newUserPermission.setPlaceOrderManagement(userPermission.getPlaceOrderManagement());
        userPermissionRepository.save(userPermission);
    }

    public List<UserPermission> getAll() {
        return userPermissionRepository.findAll();
    }
    public UserPermission getByUserPermissionId(int id) {
        return userPermissionRepository.findByPermissionId(id);
    }
}
