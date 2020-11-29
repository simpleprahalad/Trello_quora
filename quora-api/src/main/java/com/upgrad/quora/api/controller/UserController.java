package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
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

@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method= RequestMethod.POST,path="/user/signin",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        //System.out.println(Arrays.toString(Base64.getDecoder().decode(authorization.split("Basic ")[0])));
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);

        String decodedText=new String(decode);
        String[] decodedArray=decodedText.split(":");

        UserAuthTokenEntity userAuthToken=authenticationService.authenticate(decodedArray[0],decodedArray[1]);
        UserEntity user=userAuthToken.getUser();

        SigninResponse signinResponse=new SigninResponse();
        signinResponse.setId(user.getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers=new HttpHeaders();

        headers.add("access-token",userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse,headers, HttpStatus.OK);

    }
}
