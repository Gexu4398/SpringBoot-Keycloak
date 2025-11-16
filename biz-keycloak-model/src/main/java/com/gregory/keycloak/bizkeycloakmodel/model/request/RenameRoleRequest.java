package com.gregory.keycloak.bizkeycloakmodel.model.request;


import com.gregory.keycloak.bizkeycloakmodel.validator.NotSuperAdminRole;
import lombok.Data;

@Data
public class RenameRoleRequest {

  @NotSuperAdminRole
  private String newRoleName;
}
