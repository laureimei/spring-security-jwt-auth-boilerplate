package com.lauratesan.jwt.integration;

import com.lauratesan.jwt.repository.auth.RoleRepository;
import com.lauratesan.jwt.repository.auth.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class SignupIT extends BaseIntegrationTest {

    private static final String EMAIL = "maytest@test.com";
    private static final String EMAIL2 = "nonexistentuser@gmail.com";
    private static final String CORRECT_PASSWORD = "maycraftscodes";
    private static final String INCORRECT_CONFIRM_PASSWORD = "incorrectpassword1";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MockMvc mockMvc;

    @Override
    protected void afterEach() {
    }

    @Override
    protected void beforeEach() {

    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void should_return_error_when_signup_failed_given_email_already_exist() throws Exception {
        String signupRequestAsJson = "{\n" +
                "    \"email\": \"" + EMAIL + "\",\n" +
                "    \"password\": \"" + CORRECT_PASSWORD + "\",\n" +
                "    \"confirmPassword\": \"" + CORRECT_PASSWORD + "\",\n" +
                "    \"firstName\" : \"May\",\n" +
                "    \"lastName\" : \"Sanejo\"\n" +
                "}";

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupRequestAsJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exist! Please use another email."))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void should_return_success_when_signup_given_new_valid_user() throws Exception {
        String signupRequestAsJson = "{\n" +
                "    \"email\": \"" + EMAIL2 + "\",\n" +
                "    \"confirmEmail\": \"" + EMAIL2 + "\",\n" +
                "    \"password\": \"" + CORRECT_PASSWORD + "\",\n" +
                "    \"confirmPassword\": \"" + CORRECT_PASSWORD + "\",\n" +
                "    \"firstName\" : \"May\",\n" +
                "    \"lastName\" : \"Sanejo\"\n" +
                "}";

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupRequestAsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    void should_return_error_when_signup_failed_given_incorrect_confirm_password() throws Exception {
        String signupRequestAsJson = "{\n" +
                "    \"email\": \"" + EMAIL2 + "\",\n" +
                "    \"password\": \"" + CORRECT_PASSWORD + "\",\n" +
                "    \"confirmPassword\": \"" + INCORRECT_CONFIRM_PASSWORD + "\",\n" +
                "    \"firstName\" : \"May\",\n" +
                "    \"lastName\" : \"Sanejo\"\n" +
                "}";

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupRequestAsJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please retype your password!"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

}
