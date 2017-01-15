package com.ironyard.controllers;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.ironyard.entities.Photo;
import com.ironyard.entities.User;
import com.ironyard.services.PhotoRepository;
import com.ironyard.services.UserRepository;
import com.ironyard.utilities.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

/**
 * Created by scofieldservices on 1/3/17.
 */
@RestController
public class IronGramController {
    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    Server dbui = null;

    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(username);

        if (user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        }

        else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("username", username);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return users.findFirstByName(username);
    }

    @RequestMapping("/upload")
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver, MultipartFile photo, Boolean isPublic, Long lifeTime) throws Exception {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User senderUser = users.findFirstByName(username);
        User receiveUser = users.findFirstByName(receiver);

        if(receiveUser == null) {
            throw new Exception("Receiver does not exist");
        }

        if(!photo.getContentType().startsWith("image")){
            throw new Exception("Please upload image files only");
        }
        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo p = new Photo();
        p.setSender(senderUser);
        p.setRecipient(receiveUser);
        p.setFilename(photoFile.getName());
        p.setPublic(isPublic);
        p.setLifeTime(lifeTime);
        System.out.println(p.getSender());
        System.out.println(p.getRecipient());
        System.out.println(p.getFilename());
        System.out.println(p.getPublic());
        System.out.println(p.getLifeTime());
        photos.save(p);

        response.sendRedirect("/");

        return p;
    }

        @RequestMapping(path = "/photos", method = RequestMethod.GET)
        public List<Photo> showPhotos(HttpSession session) throws Exception {
            String username = (String) session.getAttribute("username");

            if (username == null) {
                throw new Exception("not logged in");
            }
            User user = users.findFirstByName(username);
            List<Photo> allPhotos = (List<Photo>) photos.findAll();
            for (Photo photo: allPhotos) {
                if (photo.getLifeTime() == null) {
                    photo.setLifeTime((long) 10);
                }
            }

            for (Photo photo: allPhotos) {
                if (photo.getPostedTime() == null) {
                    photo.setPostedTime(LocalDateTime.now());
                    photos.save(photo);
                }

                if (LocalDateTime.now().isAfter(photo.getPostedTime().plusSeconds(photo.getLifeTime()))) {
                    File deletePhoto = new File("public/"+photo.getFilename());
                    deletePhoto.delete();
                    photos.delete(photo);
                }

            }

            return photos.findByRecipient(user);
        }

    @RequestMapping(path = "/public-photos", method = RequestMethod.GET)
    public Iterable<Photo> jsonStream(HttpSession session){
        String userName = (String) session.getAttribute("username");
        System.out.println(userName);
        User sender = users.findFirstByName(userName);
        System.out.println(sender);
        return photos.findBySenderAndIsPublic(sender, true);
    }


}