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
        if (signupBusinessService.getUserByUserName(signupUserRequest.getUserName()) != null)
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        else if (signupBusinessService.getUserByEmail(signupUserRequest.getEmailAddress()) != null)
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
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
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthToken.getUser();

        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(user.getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();

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
        UserAuthTokenEntity authToken = signOutBusinessService.signout(authorization);
        if (authToken == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        UserEntity signedUser = authToken.getUser();
        SignoutResponse signoutResponse = new SignoutResponse();
        signoutResponse.id(signedUser.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}