package com.github.vitormakino.demobatchprocessing.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.vitormakino.demobatchprocessing.AppConfiguration;

@Component
public class ImportUserRoute extends RouteBuilder {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job importUserJob;

  @Autowired
  private AppConfiguration appConfiguration;
  
  @Override
  public void configure() throws Exception {
      from("file://"+ appConfiguration.getDirectory().getInput() + "?noop=true")
          .routeId("inputFileRoute")
          .log("New file detected: ${header.CamelFileName}")
          .process(exchange -> {
              // Launch the Spring Batch job
              JobParameters jobParameters = new JobParametersBuilder()
                  .addString("inputFilePath", exchange.getIn().getHeader("CamelFileAbsolutePath", String.class))
                  .toJobParameters();
              JobExecution execution = jobLauncher.run(importUserJob, jobParameters);
              exchange.getIn().setHeader("batch.status" , execution.getExitStatus().getExitCode());
          })
          .choice()
              .when(header("batch.status").isEqualTo("COMPLETED"))
                  .log("Processing successful for file: ${header.CamelFileName}")
                  .to("direct:processSuccess")
              .otherwise()
                  .log("Processing failed for file: ${header.CamelFileName}")
                  .to("direct:processFailure");

      // Route to handle successful processing
      from("direct:processSuccess")
          .to("file://"+ appConfiguration.getDirectory().getSuccess() + "?fileName=${header.CamelFileName}.zip&noop=true&fileExist=Override")
          .log("File moved to processados and zipped: ${header.CamelFileName}");

      // Route to handle failed processing
      from("direct:processFailure")
          .to("file://"+ appConfiguration.getDirectory().getErrors() + "?fileName=${header.CamelFileName}&noop=true&fileExist=Override")
          .log("File moved to erros: ${header.CamelFileName}");
  }

}
