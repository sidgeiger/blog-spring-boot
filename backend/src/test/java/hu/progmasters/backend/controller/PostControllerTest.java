package hu.progmasters.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.dto.postdto.*;
import hu.progmasters.backend.repository.AppUserDetailsRepository;
import hu.progmasters.backend.repository.PostRepository;
import hu.progmasters.backend.service.AppUserService;
import hu.progmasters.backend.service.PostService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PostController postController;

    @Mock
    private AppUserDetailsRepository appUserDetailsRepository;

    @Mock
    private PostRepository postRepository;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private AppUserService appUserService;

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_create_post() throws Exception {

        AppUser mockAppUser = new AppUser();
        mockAppUser.setId(1L);
        mockAppUser.setUserName("mockUserName");

        PostCreateCommand command = new PostCreateCommand();
        command.setTitle("Subidubi");
        command.setText("Subi dubi");
        command.setTags("subi,dubi");

        when(appUserService.findById(1L)).thenReturn(mockAppUser);

        PostInfo mockPostInfo = new PostInfo();
        when(postService.createPost(any(PostCreateCommand.class))).thenReturn(mockPostInfo);

        mockMvc.perform(post("/api/posts/registerpost")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "Subidubi")
                        .param("text", "Subi dubi")
                        .param("accountId", "1")
                        .param("tags", "subi,dubi"))
                .andExpect(status().isCreated());

        verify(postService, times(1)).createPost(any(PostCreateCommand.class));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_delete_post() throws Exception {
        Long postId = 1L;
        mockMvc.perform(delete("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_update_post() throws Exception {

        AppUser mockAppUser = new AppUser();
        mockAppUser.setId(1L);
        mockAppUser.setUserName("subidubi");

        UpdatePostCommand updatePostCommand = new UpdatePostCommand();
        updatePostCommand.setText("Updated Text");

        PostInfo updatedPostInfo = new PostInfo();
        updatedPostInfo.setTitle("Subidubi");
        updatedPostInfo.setText("Subi dubi duuu");
        updatedPostInfo.setAccountUserName("subidubi");
        updatedPostInfo.setImageUrl("www.subidubidu.com/kep.jpg");
        updatedPostInfo.setNumOfLikes(0);
        updatedPostInfo.setTags(Arrays.asList("subi", "dubi"));

        when(postService.updatePost(anyLong(), any(UpdatePostCommand.class))).thenReturn(updatedPostInfo);

        mockMvc.perform(put("/api/posts/{postid}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Subidubi"))
                .andExpect(jsonPath("$.text").value("Subi dubi duuu"))
                .andExpect(jsonPath("$.accountUserName").value("subidubi"))
                .andExpect(jsonPath("$.imageUrl").value("www.subidubidu.com/kep.jpg"))
                .andExpect(jsonPath("$.numOfLikes").value(0))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags", hasSize(2))).andDo(print());

        verify(postService, times(1)).updatePost(eq(1L), any(UpdatePostCommand.class));
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_list_all() throws Exception {
        List<PostInfo> mockPostInfoList = Arrays.asList(
                new PostInfo("Subi", LocalDate.now(), "SubiDubi", "Subi", "www.subi.com/subi.jpg", 10, Arrays.asList("subi", "dubi")),
                new PostInfo("Dubi", LocalDate.now(), "DubiSubi", "Dubi", "www.subi.com/dubi.jpg", 5, Arrays.asList("subi", "dubi"))
        );

        when(postService.listAll()).thenReturn(mockPostInfoList);

        mockMvc.perform(get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Subi"))
                .andExpect(jsonPath("$[0].create").isNotEmpty())
                .andExpect(jsonPath("$[0].text").value("SubiDubi"))
                .andExpect(jsonPath("$[0].accountUserName").value("Subi"))
                .andExpect(jsonPath("$[0].imageUrl").value("www.subi.com/subi.jpg"))
                .andExpect(jsonPath("$[0].numOfLikes").value(10))
                .andExpect(jsonPath("$[0].tags", hasSize(2)))
                .andExpect(jsonPath("$[0].tags[0]").value("subi"))
                .andExpect(jsonPath("$[0].tags[1]").value("dubi"))
                .andExpect(jsonPath("$[1].title").value("Dubi"))
                .andExpect(jsonPath("$[1].create").isNotEmpty())
                .andExpect(jsonPath("$[1].text").value("DubiSubi"))
                .andExpect(jsonPath("$[1].accountUserName").value("Dubi"))
                .andExpect(jsonPath("$[1].imageUrl").value("www.subi.com/dubi.jpg"))
                .andExpect(jsonPath("$[1].numOfLikes").value(5))
                .andExpect(jsonPath("$[1].tags", hasSize(2)))
                .andExpect(jsonPath("$[1].tags[0]").value("subi"))
                .andExpect(jsonPath("$[1].tags[1]").value("dubi"));

        verify(postService, times(1)).listAll();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_get_post_by_id() throws Exception {
        Long postId = 1L;
        PostInfo mockPostInfo = new PostInfo("Subidubi", LocalDate.now(), "Subidubiduuu hu", "subidubi", "www.subi.com/subi.jpg", 5, Arrays.asList("subi", "dubi"));

        when(postService.getById(postId)).thenReturn(mockPostInfo);

        mockMvc.perform(get("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Subidubi"))
                .andExpect(jsonPath("$.create").isNotEmpty())
                .andExpect(jsonPath("$.text").value("Subidubiduuu hu"))
                .andExpect(jsonPath("$.accountUserName").value("subidubi"))
                .andExpect(jsonPath("$.imageUrl").value("www.subi.com/subi.jpg"))
                .andExpect(jsonPath("$.numOfLikes").value(5))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.tags[0]").value("subi"))
                .andExpect(jsonPath("$.tags[1]").value("dubi"));

        verify(postService, times(1)).getById(postId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_get_post_title_by_word() throws Exception {
        String word = "dudu";
        List<PostInfo> mockPostInfoList = Arrays.asList(
                new PostInfo("Subi dudu", LocalDate.now(), "SubiDubi", "Subi", "www.subi.com/subi.jpg", 10, Arrays.asList("subi", "dubi")),
                new PostInfo("Dubi dudu", LocalDate.now(), "DubiSubi", "Dubi", "www.subi.com/dubi.jpg", 5, Arrays.asList("subi", "dubi"))
        );

        when(postService.getTitleByWord(word)).thenReturn(mockPostInfoList);

        mockMvc.perform(get("/api/posts/title/{word}", word)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Subi dudu"))
                .andExpect(jsonPath("$[1].title").value("Dubi dudu"))
                .andExpect(jsonPath("$[0].create").isNotEmpty())
                .andExpect(jsonPath("$[1].create").isNotEmpty())
                .andExpect(jsonPath("$[0].text").value("SubiDubi"))
                .andExpect(jsonPath("$[1].text").value("DubiSubi"))
                .andExpect(jsonPath("$[0].accountUserName").value("Subi"))
                .andExpect(jsonPath("$[1].accountUserName").value("Dubi"))
                .andExpect(jsonPath("$[0].imageUrl").value("www.subi.com/subi.jpg"))
                .andExpect(jsonPath("$[1].imageUrl").value("www.subi.com/dubi.jpg"))
                .andExpect(jsonPath("$[0].numOfLikes").value(10))
                .andExpect(jsonPath("$[1].numOfLikes").value(5))
                .andExpect(jsonPath("$[0].tags", hasSize(2)))
                .andExpect(jsonPath("$[1].tags", hasSize(2)))
                .andExpect(jsonPath("$[0].tags[0]").value("subi"))
                .andExpect(jsonPath("$[0].tags[1]").value("dubi"))
                .andExpect(jsonPath("$[1].tags[0]").value("subi"))
                .andExpect(jsonPath("$[1].tags[1]").value("dubi"));

        verify(postService, times(1)).getTitleByWord(word);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_get_blog_text_By_word() throws Exception {
        String word = "dudu";
        List<PostInfo> mockPostInfoList = Arrays.asList(
                new PostInfo("Subi", LocalDate.now(), "SubiDubi dudu", "Subi", "www.subi.com/subi.jpg", 10, Arrays.asList("subi", "dubi")),
                new PostInfo("Dubi", LocalDate.now(), "DubiSubi dudu", "Dubi", "www.subi.com/dubi.jpg", 5, Arrays.asList("subi", "dubi"))
        );

        when(postService.getBlogTextByWord(word)).thenReturn(mockPostInfoList);

        mockMvc.perform(get("/api/posts/blogtext/{word}", word)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Subi"))
                .andExpect(jsonPath("$[1].title").value("Dubi"))
                .andExpect(jsonPath("$[0].create").isNotEmpty())
                .andExpect(jsonPath("$[1].create").isNotEmpty())
                .andExpect(jsonPath("$[0].text").value("SubiDubi dudu"))
                .andExpect(jsonPath("$[1].text").value("DubiSubi dudu"))
                .andExpect(jsonPath("$[0].accountUserName").value("Subi"))
                .andExpect(jsonPath("$[1].accountUserName").value("Dubi"))
                .andExpect(jsonPath("$[0].imageUrl").value("www.subi.com/subi.jpg"))
                .andExpect(jsonPath("$[1].imageUrl").value("www.subi.com/dubi.jpg"))
                .andExpect(jsonPath("$[0].numOfLikes").value(10))
                .andExpect(jsonPath("$[1].numOfLikes").value(5))
                .andExpect(jsonPath("$[0].tags", hasSize(2)))
                .andExpect(jsonPath("$[1].tags", hasSize(2)))
                .andExpect(jsonPath("$[0].tags[0]").value("subi"))
                .andExpect(jsonPath("$[0].tags[1]").value("dubi"))
                .andExpect(jsonPath("$[1].tags[0]").value("subi"))
                .andExpect(jsonPath("$[1].tags[1]").value("dubi"));

        verify(postService, times(1)).getBlogTextByWord(word);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_like_post() throws Exception {
        Long postId = 1L;
        PostInfoWithLikes mockPostInfoWithLikes = new PostInfoWithLikes("Subidubi", LocalDate.now(), "Subidubidu", 5);

        when(postService.likePost(postId)).thenReturn(mockPostInfoWithLikes);

        mockMvc.perform(post("/api/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Subidubi"))
                .andExpect(jsonPath("$.create").isNotEmpty())
                .andExpect(jsonPath("$.text").value("Subidubidu"))
                .andExpect(jsonPath("$.numberOfLikes").value(5));

        verify(postService, times(1)).likePost(postId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_Likes_on_post() throws Exception {
        Long postId = 1L;
        PostInfoLikes mockPostInfoLikes = new PostInfoLikes(Arrays.asList("Subi", "Dubi"), 3);

        when(postService.likesOnPost(postId)).thenReturn(mockPostInfoLikes);

        mockMvc.perform(get("/api/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.userNames", hasSize(2)))
                .andExpect(jsonPath("$.userNames[0]").value("Subi"))
                .andExpect(jsonPath("$.userNames[1]").value("Dubi"))
                .andExpect(jsonPath("$.likes").value(3));

        verify(postService, times(1)).likesOnPost(postId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_most_liked_post() throws Exception {
        PostInfo mockMostLikedPostInfo = new PostInfo("Most Liked Post", LocalDate.now(), "Subidubidu", "subidubi", "www.subidubi.com/subi.jpg", 10, Arrays.asList("subi", "dubi"));

        when(postService.mostLikedPost()).thenReturn(mockMostLikedPostInfo);

        mockMvc.perform(get("/api/posts/mostliked")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.title").value("Most Liked Post"))
                .andExpect(jsonPath("$.create").isNotEmpty())
                .andExpect(jsonPath("$.text").value("Subidubidu"))
                .andExpect(jsonPath("$.accountUserName").value("subidubi"))
                .andExpect(jsonPath("$.imageUrl").value("www.subidubi.com/subi.jpg"))
                .andExpect(jsonPath("$.numOfLikes").value(10))
                .andExpect(jsonPath("$.tags", hasSize(2)))
                .andExpect(jsonPath("$.tags[0]").value("subi"))
                .andExpect(jsonPath("$.tags[1]").value("dubi"));

        verify(postService, times(1)).mostLikedPost();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_remove_like() throws Exception {
        Long postId = 1L;

        mockMvc.perform(delete("/api/posts/{postId}/like", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postService, times(1)).removeLike(postId);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void test_search_by_tags() throws Exception {
        List<String> tags = Arrays.asList("subi", "dubi");

        PostInfo expectedPostInfo = new PostInfo();
        expectedPostInfo.setTitle("Subidubi");
        expectedPostInfo.setCreate(LocalDate.now());
        expectedPostInfo.setText("Subidubidu");
        expectedPostInfo.setAccountUserName("subidubi");
        expectedPostInfo.setImageUrl("www.subidubi.com/subi.jpg");
        expectedPostInfo.setNumOfLikes(42);
        expectedPostInfo.setTags(tags);

        when(postService.searchByTags(tags)).thenReturn(Collections.singletonList(expectedPostInfo));

        mockMvc.perform(get("/api/posts/blog/tags")
                        .param("words", "subi", "dubi"))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Subidubi"))
                .andExpect(jsonPath("$[0].create").isNotEmpty())
                .andExpect(jsonPath("$[0].text").value("Subidubidu"))
                .andExpect(jsonPath("$[0].accountUserName").value("subidubi"))
                .andExpect(jsonPath("$[0].imageUrl").value("www.subidubi.com/subi.jpg"))
                .andExpect(jsonPath("$[0].numOfLikes").value(42))
                .andExpect(jsonPath("$[0].tags", hasSize(2)))
                .andExpect(jsonPath("$[0].tags", containsInAnyOrder("subi", "dubi")));

        verify(postService, times(1)).searchByTags(tags);
    }
}