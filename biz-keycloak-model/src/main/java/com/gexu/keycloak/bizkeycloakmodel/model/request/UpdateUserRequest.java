package com.gexu.keycloak.bizkeycloakmodel.model.request;

import com.gexu.keycloak.bizkeycloakmodel.validator.NotSuperAdminRoleId;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

  private String name;

  private String groupId;

  private String phoneNumber;

  @NotSuperAdminRoleId
  private Set<String> roleId;

  private String picture;
}
