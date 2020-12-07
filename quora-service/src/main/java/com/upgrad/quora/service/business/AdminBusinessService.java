package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * @param userUuid
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(final String userUuid, final String authorizationToken)
            throws AuthorizationFailedException, UserNotFoundException {

        //Authenticate the user auth token passed as parameter using authenticationService
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateToken(authorizationToken,
                "ATHR-002", "User is signed out.");

        //If user is non admin - throw AuthorizationFailedException
        if (userAuthTokenEntity.getUser().getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        //Else delete user using userDAO
        UserEntity userEntity = userDao.deleteUser(userUuid);
        //If user does not exist in database throw UserNotFoundException
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        } else {
            return userEntity;
        }
    }
}