package com.ironyard.services;

import com.ironyard.entities.Photo;
import com.ironyard.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by scofieldservices on 1/3/17.
 */
public interface PhotoRepository extends CrudRepository<Photo, Integer> {
    List<Photo> findByRecipient(User receiver);
    List<Photo> findByRecipientAndIsPublic(User receiver, Boolean isPublic);
}
