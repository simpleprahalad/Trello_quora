package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SignOutBusinessService {

    @Autowired
    UserDao userDao;

    /**
     * @param authorization
     * @return
     * @throws SignOutRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signout(final String authorization) throws SignOutRestrictedException {
        //Authenticate user auth token passed as parameter
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        //Throw SignOutRestrictedException if user is not signed in
        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User has not signed in.");
        }

        //Set logout time for the respective user auth token
        userAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
        return userAuthTokenEntity;
    }
}