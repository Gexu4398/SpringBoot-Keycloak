package com.gexu.keycloak.testenvironments;


import java.util.Optional;
import java.util.Set;
import com.gexu.keycloak.bizkeycloakmodel.model.KeycloakRole;
import com.gexu.keycloak.bizkeycloakmodel.model.UserEntity;
import com.gexu.keycloak.bizkeycloakmodel.repository.UserEntityRepository;
import com.gexu.keycloak.bizkeycloakmodel.service.KeycloakService;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

public class UnitTestEnvironment extends TestEnvironment {

  @MockBean
  private UserEntityRepository userEntityRepository;

  @MockBean
  private KeycloakService keycloakService;

  @BeforeAll
  void beforeAll() {

    // BizLogger 会记录用户信息，因此要 mock 一个固定的返回值，否则不会生成 BizLog 记录，
    // 若后续测试有具体的值需求或者判定逻辑，那么需要针对每个测试的具体逻辑，mock 具体的值来进行测试，
    // 届时，可以考虑将 mock 返回值的代码写到对应的 method 中，而不是 beforeAll。
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
