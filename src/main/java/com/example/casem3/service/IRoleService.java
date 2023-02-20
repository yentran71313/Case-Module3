package com.example.casem3.service;



import com.example.casem3.model.Role;

import java.util.List;

public interface IRoleService {
    List<Role> getAllRole();

    Role getRoleById(int id);

}
