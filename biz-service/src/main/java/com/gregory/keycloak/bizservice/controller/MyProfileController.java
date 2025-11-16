package com.gregory.keycloak.bizservice.controller;

import com.gregory.keycloak.bizkeycloakmodel.helper.JwtHelper;
import com.gregory.keycloak.bizkeycloakmodel.model.User;
import com.gregory.keycloak.bizkeycloakmodel.model.request.ResetPasswordRequest;
import com.gregory.keycloak.bizkeycloakmodel.service.KeycloakUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "个人信息管理")
@RestController
@RequestMapping("profile")
// 此处用 MyProfileController 命名是因为 Spring Data Rest 中有一个同名的 Controller，为了避免冲突导致启动失败
public class MyProfileController {

  private final KeycloakUserService keycloakUserService;

  @Autowired
  public MyProfileController(KeycloakUserService keycloakUserService) {

    this.keycloakUserService = keycloakUserService;
  }

  @PutMapping
  @Operation(summary = "修改用户信息")
  @PreAuthorize("isAuthenticated()")
  public User updateUser(@RequestBody User user) {

    return keycloakUserService.updateProfile(user);
  }

  @PostMapping("reset-password")
  @Operation(summary = "修改密码")
  @PreAuthorize("isAuthenticated()")
  @SneakyThrows
  public void resetPassword(@RequestBody ResetPasswordRequest request) {

    final var username = JwtHelper.getUsername();
    keycloakUserService.resetUserCredential(username, request.getOriginalPassword(),
        request.getPassword());
  }
}
