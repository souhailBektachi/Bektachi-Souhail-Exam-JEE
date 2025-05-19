package com.souhailbektachi.backend.services;

import com.souhailbektachi.backend.dtos.UserDTO;
import com.souhailbektachi.backend.entities.User;

import java.util.List;

public interface UserService {
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    List<UserDTO> getAllUsers();
    UserDTO createUser(User user);
    UserDTO updateUser(Long id, User user);
    void deleteUser(Long id);
}
