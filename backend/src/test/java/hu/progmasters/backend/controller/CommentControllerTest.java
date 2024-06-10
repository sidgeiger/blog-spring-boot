package hu.progmasters.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.progmasters.backend.dto.commentdto.*;
import hu.progmasters.backend.repository.CommentRepository;
import hu.progmasters.backend.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CommentController commentController;

    @Mock
    private CommentRepository commentRepository;


    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_create_comment() throws Exception {
        CommentCreateCommand command = new CommentCreateCommand();
        command.setAccountId(1L);
        command.setPostId(1L);
        command.setCommentText("Subi az dubi");

        CommentInfo expectedCommentInfo = new CommentInfo();
        expectedCommentInfo.setUserName("subidubi");
        expectedCommentInfo.setCommentText("Subi az dubi");
        expectedCommentInfo.setCreationDate(LocalDateTime.now());
        expectedCommentInfo.setPostId(1L);
        expectedCommentInfo.setImageUrl("www.subidubi.com/subi.jpg");

        when(commentService.saveComment(command)).thenReturn(expectedCommentInfo);

        mockMvc.perform(post("/api/comments/writecomment")
                        .param("accountId", "1")
                        .param("postId", "1")
                        .param("commentText", "Subi az dubi"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("subidubi"))
                .andExpect(jsonPath("$.commentText").value("Subi az dubi"))
                .andExpect(jsonPath("$.creationDate").isNotEmpty())
                .andExpect(jsonPath("$.postId").value(1))
                .andExpect(jsonPath("$.imageUrl").value("www.subidubi.com/subi.jpg"));

        verify(commentService, times(1)).saveComment(command);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_delete_comment() throws Exception {
        Long commentId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/comments/deletecomment/{commentId}", commentId))
                .andExpect(status().isOk());

        verify(commentService, times(1)).deleteCommentById(commentId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_get_comments_by_accountName() throws Exception {
        String userName = "subidubi";
        List<CommentInfo> commentInfoList = Arrays.asList(
                new CommentInfo("subidubi", "Subi az Dubi", LocalDateTime.now(), 1L, null),
                new CommentInfo("subidubi", "Dubi az Subi", LocalDateTime.now(), 2L, null)
        );

        when(commentService.getCommentsByAccountName(userName)).thenReturn(commentInfoList);

        mockMvc.perform(get("/api/comments/account/{name}", userName))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].userName").value("subidubi"))
                .andExpect(jsonPath("$[0].commentText").value("Subi az Dubi"))
                .andExpect(jsonPath("$[1].userName").value("subidubi"))
                .andExpect(jsonPath("$[1].commentText").value("Dubi az Subi"));

        verify(commentService, times(1)).getCommentsByAccountName(userName);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_update_comment() throws Exception {

        Long commentId = 1L;
        UpdateCommentCommand updateCommentCommand = new UpdateCommentCommand();
        updateCommentCommand.setCommentText("Repa retek, gyertek");

        CommentInfo updatedCommentInfo = new CommentInfo("subidubi", "Repa retek, gyertek", LocalDateTime.now(), 1L, null);

        when(commentService.updateComment(commentId, updateCommentCommand)).thenReturn(updatedCommentInfo);

        mockMvc.perform(put("/api/comments/{commentid}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCommentCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("subidubi"))
                .andExpect(jsonPath("$.commentText").value("Repa retek, gyertek"));

        verify(commentService, times(1)).updateComment(commentId, updateCommentCommand);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_like_comment() throws Exception {
        Long commentId = 1L;
        CommentInfoWithLike expectedResult = new CommentInfoWithLike("Subidubidubiii", LocalDateTime.now(), 15);

        when(commentService.like(commentId)).thenReturn(expectedResult);

        mockMvc.perform(post("/api/comments/{commentId}/like", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentText").value("Subidubidubiii"))
                .andExpect(jsonPath("$.numberOfLikes").value(15));

        verify(commentService, times(1)).like(commentId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_likes_on_comment() throws Exception {
        Long commentId = 1L;

        CommentInfoLikes commentInfoLikes = new CommentInfoLikes(Arrays.asList("subidubi", "dubisubi"), 2);

        when(commentService.likesOnComment(commentId)).thenReturn(commentInfoLikes);

        mockMvc.perform(get("/api/comments/{commentId}/like", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNames", hasSize(2)))
                .andExpect(jsonPath("$.userNames[0]").value("subidubi"))
                .andExpect(jsonPath("$.userNames[1]").value("dubisubi"))
                .andExpect(jsonPath("$.likes").value(2));

        verify(commentService, times(1)).likesOnComment(commentId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_remove_like() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(delete("/api/comments/{commentId}/like", commentId))
                .andExpect(status().isOk());

        verify(commentService, times(1)).removeLike(commentId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_most_liked_comment() throws Exception {
        CommentInfoLikes commentInfoLikes = new CommentInfoLikes(Arrays.asList("subidubi", "dubisubi"), 10);
        when(commentService.mostLikedComment()).thenReturn(commentInfoLikes);

        mockMvc.perform(get("/api/comments/mostliked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userNames", hasSize(2)))
                .andExpect(jsonPath("$.userNames[0]").value("subidubi"))
                .andExpect(jsonPath("$.userNames[1]").value("dubisubi"))
                .andExpect(jsonPath("$.likes").value(10));

        verify(commentService, times(1)).mostLikedComment();
    }

}
