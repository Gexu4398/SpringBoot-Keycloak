package com.gregory.keycloak.bizkeycloakmodel.model.request;

import lombok.Data;

@Data
public class AcceptUserRequest {

  private String roleName;
  private String memo;
}
