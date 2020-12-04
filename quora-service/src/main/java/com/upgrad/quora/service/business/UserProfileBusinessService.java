package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileBusinessService {

  @Autowired
  private UserDao userDao;

  public UserEntity getUserProfile(final String userUuid, final String authorizationToken)
      throws AuthorizationFailedException,
      UserNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
    UserEntity userEntity = userDao.getUserByUuid(userUuid);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
    } else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt()
        .isBefore(ZonedDateTime.now())) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to get user details.");
    } else if (userEntity == null) {
      throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
    } else {
      return userEntity;
    }
  }
}
