package com.gexu.keycloak.bizkeycloakmodel.model.request;

import com.gexu.keycloak.bizkeycloakmodel.validator.NotSuperAdminRoleId;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {

  private String name;

  private String groupId;

  private String phoneNumber;

  @NotSuperAdminRoleId
  @NotEmpty(message = "角色集合不能为空")
  private Set<String> roleId;

  private String picture;
}
