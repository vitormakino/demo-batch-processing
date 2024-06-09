package com.github.vitor.makino.demobatchprocessing;


import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.vitormakino.demobatchprocessing.AppConfiguration;
import com.github.vitormakino.demobatchprocessing.DemoBatchProcessingApplication;


@SpringBootTest(classes = DemoBatchProcessingApplication.class)
@Testcontainers
@Import(TestConfig.class)
public class BatchProcessingIntegrationTest {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private AppConfiguration appConfiguration;
  
  private static final String EMAIL = "teste@teste.com";
  
  @BeforeEach
  public void setUp()
    throws IOException, InterruptedException {
    // Copia um arquivo de teste para o diretório de entrada
    File testFile = new File(appConfiguration.getDirectory().getInput() + "/testfile.txt");
    if (!testFile.exists()) {
      testFile.createNewFile();
    }
    
    try (FileWriter writer = new FileWriter(testFile)) {
      writer.write("1,teste," + EMAIL );
    }
  }

  @Test
  public void testBatchProcessing()
    throws Exception {
    // Aguarda o processamento do arquivo
    Thread.sleep(60000);

    // Verifica se o arquivo zipado está na pasta processados
    File processedFile = new File(appConfiguration.getDirectory().getSuccess() + "/testfile.txt.zip");
    assertTrue(processedFile.exists());

    // Verifica se os dados foram inseridos no banco de dados
    try (Connection connection = dataSource.getConnection()) {
      Statement statement = connection.createStatement();
      ResultSet resultSet =
        statement.executeQuery("SELECT * FROM db_user WHERE email='"+ EMAIL + "'");
      assertTrue(resultSet.next());
      assertEquals(EMAIL, resultSet.getString("email"));
    }
  }

}
