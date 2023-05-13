package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @MockBean
    private UserMapper userMapper;

    private User user;

    @BeforeEach
    public void set() {
        user = User.builder()
                .name("john")
                .email("john.doe@mail.com").build();
    }

    @Test
    void saveShouldReturnOkWhenValidRequest() throws Exception {
        User newUser = User.builder()
                .id(1L)
                .name(user.getName())
                .email(user.getEmail())
                .build();

        when(userService.add(any()))
                .thenReturn(newUser);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void saveShouldReturnBadRequestWhenUserIsNull() throws Exception {
        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(null))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Malformed JSON Request")));
    }

    @Test
    void editShouldReturnOkWhenValidRequest() throws Exception {
        // arrange
        long userId = 1L;

        User updatedUser = User.builder().build();
        updatedUser.setId(userId);
        updatedUser.setName("John Doe");
        updatedUser.setEmail("johndoe@example.com");


        when(userService.edit(anyLong(), any())).thenReturn(updatedUser);

        // act
        mockMvc.perform(patch("/users/{0}", userId)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void editShouldReturnBadRequestWhenUserIsNull() throws Exception {
        mockMvc.perform(patch("/users/{0}", 1L)
                        .content(mapper.writeValueAsString(null))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Malformed JSON Request")));
    }

    @Test
    void editShouldReturnBadRequestWhenUserIdWrong() throws Exception {
        mockMvc.perform(patch("/users/{0}", "1L")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Failed to convert value of type")));
    }

    @Test
    void editShouldReturnBadRequestWhenUserIdZeroOrNegative() throws Exception {
        mockMvc.perform(patch("/users/{0}", 0L)
                        .content(mapper.writeValueAsString(null))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Malformed JSON Request")));
    }

    @Test
    void getAllShouldReturnOk() throws Exception {
        user.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(User.builder()
                .id(2L).email("Sheldon.Cooper@big-bang.com")
                .name("Sheldon Lee Cooper")
                .build());

        when(userService.getAll())
                .thenReturn(users);

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdShouldReturnOkWhenValidRequest() throws Exception {
        long userId = 1L;
        user.setId(userId);
        when(userService.getById(anyLong())).thenReturn(user);

        mockMvc.perform(get("/users/{0}", userId)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getByIdShouldReturnBadRequestWhenUserIdWrong() throws Exception {
        mockMvc.perform(get("/users/{0}", "1L")
                        .content(mapper.writeValueAsString(null))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Failed to convert value of type")));
    }

    @Test
    void deleteShouldReturnNoContentWhenValidRequest() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/users/{0}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnBadRequestWhenUserIdIsWrong() throws Exception {
        mockMvc.perform(delete("/users/{0}", "0L")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}