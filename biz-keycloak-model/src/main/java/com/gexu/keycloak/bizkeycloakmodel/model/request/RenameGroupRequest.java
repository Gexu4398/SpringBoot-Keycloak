package com.gexu.keycloak.bizkeycloakmodel.model.request;

import java.util.Calendar;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class RenameGroupRequest {

  private String newGroupName;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Calendar validityDate;
}
