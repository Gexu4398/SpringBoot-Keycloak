package com.gexu.keycloak.bizkeycloakmodel.model.request;


import com.gexu.keycloak.bizkeycloakmodel.validator.NotSuperAdminRole;
import lombok.Data;

@Data
public class RenameRoleRequest {

  @NotSuperAdminRole
  private String newRoleName;
}
