package com.github.vitormakino.demobatchprocessing.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.github.vitormakino.demobatchprocessing.entity.User;

public class UserProcessor implements ItemProcessor<User, User>{

  @Override
  public User process(User item)
    throws Exception {
    return item;
  }

}
