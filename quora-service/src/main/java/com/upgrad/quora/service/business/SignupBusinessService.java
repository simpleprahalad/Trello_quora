package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private UserDao userDao;

    /**
     * @param userEntity
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) {

        //Sign up the user by encrypting and setting password with salt
        String password = userEntity.getPassword();
        String[] encryptedText = cryptographyProvider.encrypt(password);
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        //call to persist userEntity in user table in database and return that user entity
        return userDao.createUser(userEntity);
    }

    /**
     * @param userName
     * @return
     */
    public UserEntity getUserByUserName(final String userName) {
        //Get user by username from the userDAO
        return userDao.getUserByUserName(userName);
    }

    /**
     * @param email
     * @return
     */
    public UserEntity getUserByEmail(final String email) {
        //Get user by username from the userDAO
        return userDao.getUserByEmail(email);
    }
}
