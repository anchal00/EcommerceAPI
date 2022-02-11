package com.example.demo.controller;

import com.example.demo.ECommerceApplication;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.Constants;
import com.example.demo.utils.GlobalConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.h2.command.ddl.CreateUser;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ECommerceApplication.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class UserControllerTests {

    private final Logger log = LoggerFactory.getLogger(UserControllerTests.class);

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CartRepository cartRepository;

    @Test
    public void testCorrectRequestBodyCreatesNewUserSuccessfully() throws Exception {

        when(userRepository.save(any(User.class))).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(null);
        URI uri = UriComponentsBuilder
                .fromPath(GlobalConstants.ECOMMERCE_API_ENDPOINT + GlobalConstants.USER_RESOURCE_PATH + "/create")
                .build().toUri();

        CreateUserRequest dummyRequestBody = createDummyUserRequest(true);
        assertNotNull(dummyRequestBody);
        String dummyContent = new ObjectMapper().writeValueAsString(dummyRequestBody);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON).content(dummyContent).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertNotNull(response);
        String responseString = response.getContentAsString();

        User user = new ObjectMapper().readValue(responseString, User.class);

        assertNotNull(user);
        assertEquals(200, response.getStatus());

    }

    @Test
    public void testIncompleteRequestBodyDoesntCreateUser() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(null);
        URI uri = UriComponentsBuilder
                .fromPath(GlobalConstants.ECOMMERCE_API_ENDPOINT + GlobalConstants.USER_RESOURCE_PATH + "/create")
                .build().toUri();

        CreateUserRequest dummyRequestBody = createDummyUserRequest(false);
        assertNotNull(dummyRequestBody);
        String dummyContent = new ObjectMapper().writeValueAsString(dummyRequestBody);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON).content(dummyContent).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertNotNull(response);
        assertNotEquals(200, response.getStatus());
    }

    @Test
    public void testMismatchedPasswordsDoesntCreateUser() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(null);
        URI uri = UriComponentsBuilder
                .fromPath(GlobalConstants.ECOMMERCE_API_ENDPOINT + GlobalConstants.USER_RESOURCE_PATH + "/create")
                .build().toUri();

        CreateUserRequest dummyRequestBody = createDummyUserRequest(false);
        assertNotNull(dummyRequestBody);

        /**
         * modifying the password a bit to make a 'password' and 'confirmPassword fields
         * differ
         */
        dummyRequestBody.setPassword("dummyPasswordChanged");

        String dummyContent = new ObjectMapper().writeValueAsString(dummyRequestBody);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON).content(dummyContent).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertNotNull(response);
        assertNotEquals(200, response.getStatus());
    }

    @Test
    public void testGetUserById() throws Exception {

        User userStub = new User();
        userStub.setId(1);
        userStub.setUsername("dummy");
        userStub.setPassword("dummypass");
        userStub.setCart(new Cart());

        when(userRepository.findById(1L)).thenReturn(Optional.of(userStub));

        URI uri = UriComponentsBuilder
                .fromPath(GlobalConstants.ECOMMERCE_API_ENDPOINT + GlobalConstants.USER_RESOURCE_PATH + "/1")
                .build().toUri();

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertNotNull(response);

        User retrievedUser = new ObjectMapper().readValue(response.getContentAsString(), User.class);
        assertNotNull(retrievedUser);
        assertEquals(200, response.getStatus());
        assertEquals(userStub.getUsername(), retrievedUser.getUsername());

    }

    private CreateUserRequest createDummyUserRequest(boolean valid) {

        CreateUserRequest createdUserRequest = new CreateUserRequest();
        createdUserRequest.setUsername("dummy");
        createdUserRequest.setPassword("dummyPassword");
        if (!valid) {
            /**
             * if valid = false , create invalid request by leaving 'confirmPassword' field
             * as null
             * 
             */

            return createdUserRequest;
        }
        createdUserRequest.setConfirmPassword("dummyPassword");
        return createdUserRequest;

    }
}
