package com.ironyard.services;

import com.ironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by scofieldservices on 1/3/17.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findFirstByName(String name);
}
