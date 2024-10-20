package com.gexu.keycloak.bizkeycloakmodel.model.request;

import com.gexu.keycloak.bizkeycloakmodel.validator.NotSuperAdminRole;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewRoleRequest {

  @NotSuperAdminRole
  private String name;

  private List<String> scopes;
}
