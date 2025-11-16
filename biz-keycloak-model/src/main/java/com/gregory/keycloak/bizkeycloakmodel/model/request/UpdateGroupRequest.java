package com.gregory.keycloak.bizkeycloakmodel.model.request;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateGroupRequest {

  private String ID;

  private String name;

  private List<String> roles;

}
