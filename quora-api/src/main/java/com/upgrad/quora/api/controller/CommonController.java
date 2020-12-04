package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserProfileBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class CommonController {

  @Autowired
  private UserProfileBusinessService userProfileBusinessService;

  @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userUuid,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException, UserNotFoundException {

    final UserEntity userEntity = userProfileBusinessService.getUserProfile(userUuid, authorization);

    UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
        .firstName(userEntity.getFirstname()).lastName(userEntity.getLastname())
        .userName(userEntity.getUsername()).emailAddress(userEntity.getEmail())
        .country(userEntity.getCountry()).aboutMe(userEntity.getAboutme())
        .dob(userEntity.getDob()).contactNumber(userEntity.getContactnumber());

    return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
  }
}
