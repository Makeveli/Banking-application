package com.bharat.bank.role.services;

import com.bharat.bank.exceptions.BadRequestException;
import com.bharat.bank.exceptions.NotFoundException;
import com.bharat.bank.response.Response;
import com.bharat.bank.role.entity.Role;
import com.bharat.bank.role.repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImplementation implements RoleService {
    private RoleRepository roleRepository;

    @Override
    public Response<Role> createRole(Role roleRequest) {
        if(roleRepository.findByName(roleRequest.getName()).isPresent()){
            throw new BadRequestException("Role already exists");
        }
        Role savedRole = roleRepository.save(roleRequest);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role saved successfully")
                .data(savedRole)
                .build();
    }

    @Override
    public Response<Role> updateRole(Role roleRequest) {
        Role role = roleRepository.findById(roleRequest.getId())
                .orElseThrow(()->new NotFoundException("Role with this id does not exist"));
        role.builder()
                .name(roleRequest.getName())
                .build();
        Role updatedRole = roleRepository.save(role);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
    }

    @Override
    public Response<List<Role>> getAllRoles() {
        List<Role> roleList = roleRepository.findAll();
        return Response.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles fetched successfully")
                .data(roleList)
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id) {
        if(!roleRepository.existsById(id)){
            throw new NotFoundException("Role not found");
        }
        roleRepository.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build();
    }
}
