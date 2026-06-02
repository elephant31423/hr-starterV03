package com.example.hrstarter.controller;

import com.example.hrstarter.annotation.AuditLog;
import com.example.hrstarter.dto.UserDTO;
import com.example.hrstarter.dto.UserDetailDTO;
import com.example.hrstarter.entity.User;
import com.example.hrstarter.service.RoleService;
import com.example.hrstarter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
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
    public ResponseEntity<?> list() {
        List<UserDTO> userList = userService.findAll();
        // 轉換成 DTO 過濾 password
        log.info("查詢使用者資訊 {}", userList);

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {

        return userService.findUserDetailDTOById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());

    }


    @AuditLog(action = "CREATE", entityType = "USER", idParam = "id")
    @PostMapping
    public User create(@RequestBody User user) {
        userService.insert(user);
        return user;
    }

    @AuditLog(action = "UPDATE", entityType = "USER", idParam = "id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UserDetailDTO user) {

        try {
            log.info("修改使用者帳號 ID {} 資料: {}", id, user);
            user.setId(id);
            userService.updateBaseInfo(user);
            return ResponseEntity.ok(Map.of("success", true, "message", "修改成功"));
        } catch (Exception e) {
            log.error(" 修改使用者帳號失敗", e);
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }

    @AuditLog(action = "DELETE", entityType = "USER", idParam = "id")
    @DeleteMapping("/{id}")
    public Long delete(@PathVariable Long id) {
        userService.delete(id);
        return id;
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<?> getRoles(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(roleService.getRolesByUserId(id));
    }


}
