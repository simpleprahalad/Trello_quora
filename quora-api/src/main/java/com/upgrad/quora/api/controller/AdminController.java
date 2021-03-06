package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    /**
     * This controller handles the endpoints which allow ADMIN users to delete particular user
     * regisrted in the application by passing thier UUID.
     *
     * @param userUuid
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE,
            path = "/admin/user/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String userUuid,
                                                         @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {
        //adminBusinessService will take the parameters of user
        // 1. UUID of user which is to be deleted (User UUID is input as path variable)
        // 2. User auth token of admin user to verify if user is signed in or not.
        final UserEntity userEntity = adminBusinessService.deleteUser(userUuid, authorization);

        //Appropriate response model (generated by swagger 2.0) is configured and returned as JSON.
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(userEntity.getUuid()).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<>(userDeleteResponse, HttpStatus.OK);
    }
}