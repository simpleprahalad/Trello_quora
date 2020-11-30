package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
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
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity getUserAuthToken(final String authorization) throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthToken =userDao.getUserAuthToken(authorization);
        if(userAuthToken==null)
            throw new SignOutRestrictedException("SGR-001","User is not Signed in.");
        else
            return userAuthToken;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAuthToken(final UserAuthTokenEntity userAuthToken){

        UserEntity signedUser= userAuthToken.getUser();
        userAuthToken.setLogoutAt(ZonedDateTime.now());
        userAuthToken.setExpiresAt(ZonedDateTime.now());
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(signedUser.getPassword());
        userAuthToken.setAccessToken(jwtTokenProvider.generateToken(signedUser.getUuid(), ZonedDateTime.now(),ZonedDateTime.now()));
        userDao.updateAuthToken(userAuthToken);

    }
}
