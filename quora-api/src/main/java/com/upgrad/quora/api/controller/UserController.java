package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignOutBusinessService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    /**
     * This class handles pertaining to UserEnity (user table in database).
     * This allows new users to sign-up, existing users to sign-in and signed-in users to
     * sign-out.
     *
     * This class uses the AuthenticationService, SignupBusinessService and signOutBusinessService
     * classes which is autowired and thus instantiated by Spring Framework for us.
     */

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    SignupBusinessService signupBusinessService;

    @Autowired
    SignOutBusinessService signOutBusinessService;

    /**
     * @param signupUserRequest
     * @return
     * @throws SignUpRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST,
            path = "/user/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        //Accepts JSON Sign up request from user containing new user info

        //If the username is already taken throw SignUpRestrictedException
        if (signupBusinessService.getUserByUserName(signupUserRequest.getUserName()) != null)
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        //If the user email is already used to register throw SignUpRestrictedException
        else if (signupBusinessService.getUserByEmail(signupUserRequest.getEmailAddress()) != null)
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        //Else define and instantiate a new userEntity object from the supplied request
        else {
            final UserEntity userEntity = new UserEntity();
            userEntity.setUuid(UUID.randomUUID().toString());
            userEntity.setFirstname(signupUserRequest.getFirstName());
            userEntity.setLastname(signupUserRequest.getLastName());
            userEntity.setUsername(signupUserRequest.getUserName());
            userEntity.setEmail(signupUserRequest.getEmailAddress());
            userEntity.setPassword(signupUserRequest.getPassword());
            userEntity.setCountry(signupUserRequest.getCountry());
            userEntity.setAboutme(signupUserRequest.getAboutMe());
            userEntity.setDob(signupUserRequest.getDob());
            userEntity.setContactnumber(signupUserRequest.getContactNumber());
            userEntity.setRole("nonadmin");
            final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
            SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
            return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
        }
    }

    /**
     * @param authorization
     * @return
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method = RequestMethod.POST,
            path = "/user/signin",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        //Code for basic signup auth
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        //Generate User Auth Token using authenticationService
        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthToken.getUser();

        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(user.getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();

        //Return access token with the respective header name
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }

    /**
     * @param authorization
     * @return
     * @throws SignOutRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST,
            path = "/user/signout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        //User signOutBusinessService class' signout method to drive business logic of signout
        UserAuthTokenEntity authToken = signOutBusinessService.signout(authorization);
        //If user auth token does not exist in database ie. User is not signed in
        //Throw SignOutRestrictedException
        if (authToken == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        //Else sign out successfully
        UserEntity signedUser = authToken.getUser();
        SignoutResponse signoutResponse = new SignoutResponse();
        signoutResponse.id(signedUser.getUuid()).message("SIGNED OUT SUCCESSFULLY");

        //Appropriate response model (generated by swagger 2.0) is instantiated/configured
        // and returned as JSON.
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}