package com.gexu.keycloak.bizkeycloakmodel.model.request;

import com.gexu.keycloak.bizkeycloakmodel.validator.NotSuperAdminRole;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewRoleRequest {

  @NotSuperAdminRole
  private String name;

  private List<String> scopes;
}
