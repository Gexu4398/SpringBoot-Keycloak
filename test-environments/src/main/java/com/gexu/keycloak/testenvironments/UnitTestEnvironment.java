package com.gexu.keycloak.testenvironments;

import com.gexu.keycloak.bizkeycloakmodel.model.KeycloakRole;
import com.gexu.keycloak.bizkeycloakmodel.model.UserEntity;
import com.gexu.keycloak.bizkeycloakmodel.repository.UserEntityRepository;
import com.gexu.keycloak.bizkeycloakmodel.service.KeycloakService;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class UnitTestEnvironment extends TestEnvironment {

  @MockitoBean
  private UserEntityRepository userEntityRepository;

  @MockitoBean
  private KeycloakService keycloakService;

  @BeforeAll
  void beforeAll() {

    final var userEntity = new UserEntity();
    userEntity.setId("admin");
    userEntity.setUsername("admin");
    userEntity.setFirstName("admin");
    userEntity.setRoles(Set.of(KeycloakRole.builder().name("超级管理员").clientRole(true).build()));
    Mockito.when(keycloakService.getRealm()).thenReturn("console-app");
    Mockito.when(userEntityRepository.findByUsernameAndRealmId("admin", keycloakService.getRealm()))
        .thenReturn(Optional.of(userEntity));
  }
}
