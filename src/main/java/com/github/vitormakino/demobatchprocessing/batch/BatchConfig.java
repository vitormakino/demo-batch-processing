package com.github.vitormakino.demobatchprocessing.batch;


import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.github.vitormakino.demobatchprocessing.batch.listener.JobCompletionNotificationListener;
import com.github.vitormakino.demobatchprocessing.batch.processor.UserProcessor;
import com.github.vitormakino.demobatchprocessing.entity.User;


@Configuration
@EnableBatchProcessing
public class BatchConfig {

  private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);
  
  @Autowired
  private DataSource dataSource;

  @Autowired
  private Resource inputFile;
  
  @BeforeStep
  public void beforeStep(StepExecution stepExecution) {
      JobParameters parameters = stepExecution.getJobExecution()
        .getJobParameters();
      log.info("Before step params: {}", parameters);
  }
  
  @Bean 
  @StepScope
  public Resource inputFile(@Value("#{jobParameters[inputFilePath]}") String inputFilePath) {
      return new FileSystemResource(inputFilePath);
  }
  
  @Bean
  public FlatFileItemReader<User> reader() {
    return new FlatFileItemReaderBuilder<User>()
        .name("importUserReader")
        .resource(inputFile)
        .delimited()
        .names(new String[] {"id","name","email" })
        .targetType(User.class)
        .build();
  }

  @Bean
  public ItemProcessor<User, User> processor() {
    return new UserProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<User> writer() {
    return new JdbcBatchItemWriterBuilder<User>()
      .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
      .sql("INSERT INTO db_user (id, name, email) VALUES (:id, :name, :email)")
      .dataSource(dataSource)
      .build();
  }

  @Bean
  public Step step1(JobRepository jobRepository,
                    PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
            .<User, User>chunk(10, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
  }

  @Bean
  public Job importUserJob(JobCompletionNotificationListener listener, Step step1, JobRepository jobRepository) {
        return new JobBuilder("importUserJob", jobRepository)
            .listener(listener)
            .flow(step1)
            .end()
            .build();
    }
}
