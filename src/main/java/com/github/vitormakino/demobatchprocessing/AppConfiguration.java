package com.github.vitormakino.demobatchprocessing;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfiguration {

  private Directory directory;

  public Directory getDirectory() {
    return directory;
  }

  public void setDirectory(Directory directory) {
    this.directory = directory;
  }

  public static class Directory {
    private String input;
    private String success;
    private String errors;

    public String getInput() {
      return input;
    }

    public void setInput(String input) {
      this.input = input;
    }

    public String getSuccess() {
      return success;
    }

    public void setSuccess(String success) {
      this.success = success;
    }

    public String getErrors() {
      return errors;
    }

    public void setErrors(String errors) {
      this.errors = errors;
    }

  }
}
