package com.bharat.bank.role.services;

import com.bharat.bank.response.Response;
import com.bharat.bank.role.entity.Role;

import java.util.List;

public interface RoleService {

    Response<Role> createRole(Role roleRequest);
    Response<Role> updateRole(Role roleRequest);
    Response<List<Role>> getAllRoles();
    Response<?> deleteRole(Long id);
}
