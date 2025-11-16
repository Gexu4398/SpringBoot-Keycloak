package com.gregory.keycloak.bizservice.controller;

import com.gregory.keycloak.bizkeycloakmodel.service.KeycloakUserService;
import com.gregory.keycloak.bizkeycloakmodel.validator.NotSuperAdminUsername;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户重置")
@RestController
@RequestMapping("reset-password")
@Validated
public class ResetPasswordController {

  private final KeycloakUserService keycloakUserService;

  @Autowired
  public ResetPasswordController(KeycloakUserService keycloakUserService) {

    this.keycloakUserService = keycloakUserService;
  }

  @PostMapping("{username}")
  @Operation(summary = "用户重置")
  @PreAuthorize("isAnonymous()")
  public void reset(@NotSuperAdminUsername @PathVariable("username") String username) {

    keycloakUserService.resetUserCredentialByAnonymous(username);
  }
}
