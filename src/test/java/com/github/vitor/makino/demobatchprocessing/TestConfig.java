package com.github.vitor.makino.demobatchprocessing;


import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;


@TestConfiguration(proxyBeanMethods = false)
class TestConfig {

  @Bean
  @ServiceConnection
  @RestartScope
  public OracleContainer oracleFreeContainer() {
    OracleContainer oracleContainer =
      new OracleContainer(DockerImageName.parse("gvenzl/oracle-free:latest"));
    return oracleContainer.withInitScript("./schema-oracle.sql");
  }
}
