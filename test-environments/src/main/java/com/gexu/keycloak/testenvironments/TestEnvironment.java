package com.gexu.keycloak.testenvironments;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import java.util.Locale;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

/**
 * API 测试基类。 该类对于 Keycloak 和 Minio 系统的相关 Bean 进行了 Mock 处理。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.profiles.active=test",
    "app.show-sql=true",
    "logging.level.liquibase=debug",
})
@Slf4j
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestInstance(Lifecycle.PER_CLASS)
// 此处要设置 mergeMode，否则回替换调原来的 listeners，会导致部分测试失败
// 参考文献 https://www.baeldung.com/spring-testexecutionlistener
@Rollback(false)
abstract class TestEnvironment {

  protected Faker faker = new Faker(new Locale("zh-CN"));

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  @Qualifier("keycloakEntityManager")
  protected EntityManager keycloakEntityManager;

  @Autowired
  @Qualifier("bizDataSource")
  private DataSource bizDataSource;

  @AfterEach
  @SneakyThrows
  void afterEach() {

  }
}
