package com.example.hrstarter.controller;

import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.dto.UserDTO;
import com.example.hrstarter.entity.Role;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.service.RoleService;
import com.example.hrstarter.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> list() {
        List<UserDTO> userList = userService.findAll();
        // 轉換成 DTO 過濾 password

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> get(@PathVariable Long id) {

        return userService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

    }


    @AuditLog(action = "CREATE", entityType = "USER", idParam = "id")
    @PostMapping
    public void create(@RequestBody User user) {
        userService.insert(user);
    }

    @AuditLog(action = "UPDATE", entityType = "USER", idParam = "id")
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody User user) {
//        user.setId(id);
        userService.update(user);
    }

    @AuditLog(action = "DELETE", entityType = "USER", idParam = "id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<?> getRoles(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(roleService.getRolesByUserId(id));


    }
}
