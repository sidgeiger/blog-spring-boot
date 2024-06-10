package hu.progmasters.backend.service;


import hu.progmasters.backend.domain.AppUser;
import hu.progmasters.backend.domain.Category;
import hu.progmasters.backend.domain.Post;
import hu.progmasters.backend.domain.Tag;
import hu.progmasters.backend.dto.postdto.*;
import hu.progmasters.backend.exceptionhandling.AlreadyLiked;
import hu.progmasters.backend.exceptionhandling.PostNotExistsException;
import hu.progmasters.backend.exceptionhandling.PostNotFoundByIdException;
import hu.progmasters.backend.exceptionhandling.UnauthorizedException;
import hu.progmasters.backend.repository.CategoryRepository;
import hu.progmasters.backend.repository.PostRepository;
import hu.progmasters.backend.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static hu.progmasters.backend.service.AppUserService.ADMIN;

@Service
@Transactional
@Slf4j
public class PostService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    private final AppUserService accountService;

    private final CloudinaryImageService cloudinaryImageService;
    private final ModelMapper modelMapper;

    private final TagRepository tagRepository;


    @Autowired
    public PostService(CategoryRepository categoryRepository, PostRepository postRepository, AppUserService accountService, CloudinaryImageService cloudinaryImageService, ModelMapper modelMapper, TagRepository tagRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.accountService = accountService;
        this.cloudinaryImageService = cloudinaryImageService;
        this.modelMapper = modelMapper;
        this.tagRepository = tagRepository;
    }

    public PostInfo createPost(PostCreateCommand command) {
        Post savedPost = postRepository.save(modelMapper.map(command, Post.class));
        savedPost.setCategory(categoryRepository.getById(command.getCategory()));
        savedPost.setCreate(LocalDate.now());
        savedPost.setTags(addTags(command.getTags()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = accountService.findAccountByUserName(authentication.getName());
        savedPost.setAccount(appUser);

        cloudinaryImageService.imageControlPost(savedPost, command);

        List<String> tags = savedPost.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        PostInfo postInfo = modelMapper.map(savedPost, PostInfo.class);
        postInfo.setTags(tags);
        postInfo.setAccountUserName(appUser.getUserName());
        postInfo.setCategory(savedPost.getCategory().getCategoryName());

        return postInfo;
    }


    public PostInfo updatePost(Long id, UpdatePostCommand command) {
        Post post = this.findById(id);

        securityCheck(post, "You are not allowed to update this post");

        post.setText(command.getText());
        PostInfo postInfo = modelMapper.map(post, PostInfo.class);
        postInfo.setAccountUserName(post.getAccount().getUserName());
        List<String> tags = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());
        postInfo.setTags(tags);
        postInfo.setCategory(post.getCategory().getCategoryName());
        return postInfo;
    }


    public PostImageInfo updatePostWithImage(Long postid, UpdatePostImageCommand command) {
        Post post = this.findById(postid);

       securityCheck(post, "You are not allowed to update this post");

        MultipartFile newImageFile = command.getImage();

        if (newImageFile != null && !newImageFile.isEmpty()) {
            try {
                if (post.getImageUrl() != null) {
                    cloudinaryImageService.delete(post.getImageUrl());
                }
                Map imageData = cloudinaryImageService.upload(newImageFile);
                post.setImageUrl(imageData.get("url").toString());
                post.setImage(newImageFile.getBytes());
            } catch (IOException e) {
                log.error("Error with the picture upload.", e);
            }
        } else {
            log.warn("New image file is null or empty.");
        }

        PostImageInfo postImageInfo = modelMapper.map(post, PostImageInfo.class);
        postImageInfo.setAccountUserName(post.getAccount().getUserName());
        postImageInfo.setCategory(post.getCategory().getCategoryName());
        return postImageInfo;
    }

    public void deletePost(Long id) {
        Post post = this.findById(id);

        securityCheck(post, "You are not allowed to delete this post");

        postRepository.delete(post);
    }

    private static void securityCheck(Post post, String errorMessage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).contains(ADMIN)) {
            if (!post.getAccount().getUserName().equals(currentUsername)) {
                throw new UnauthorizedException(errorMessage);
            }
        }
    }

    Post findById(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isEmpty()) {
            throw new PostNotFoundByIdException(id);
        }
        return optionalPost.get();
    }

    public List<PostInfo> listAll() {
        List<PostInfo> postInfoList = postRepository.findAll().stream()
                .map(post -> {
                    String accountUserName = post.getAccount().getUserName();
                    PostInfo postInfo = modelMapper.map(post, PostInfo.class);
                    postInfo.setAccountUserName(accountUserName);
                    postInfo.setCategory(post.getCategory().getCategoryName());
                    return postInfo;
                })
                .collect(Collectors.toList());

        if (postInfoList.isEmpty()) {
            throw new PostNotExistsException();
        }
        return postInfoList;
    }

    public PostInfo getById(Long id) {
        Post post = this.findById(id);
        return modelMapper.map(post, PostInfo.class);
    }

    public List<PostInfo> getTitleByWord(String word) {
        List<Post> postList = postRepository.findAll().stream()
                .filter(post -> post.getTitle().toLowerCase().contains(word.toLowerCase()))
                .collect(Collectors.toList());

        if (postList.isEmpty()) {
            throw new PostNotExistsException();
        }

        return postList.stream().map(post -> {
                    String accountUserName = post.getAccount().getUserName();
                    PostInfo postInfo = modelMapper.map(post, PostInfo.class);
                    postInfo.setAccountUserName(accountUserName);
                    postInfo.setCategory(post.getCategory().getCategoryName());
                    return postInfo;
                })
                .collect(Collectors.toList());

    }

    public List<PostInfo> getBlogTextByWord(String word) {
        List<Post> postList = postRepository.findAll().stream()
                .filter(post -> post.getText().toLowerCase().contains(word.toLowerCase()))
                .collect(Collectors.toList());

        if (postList.isEmpty()) {
            throw new PostNotExistsException();
        }

        return postList.stream().map(post -> {
                    String accountUserName = post.getAccount().getUserName();
                    PostInfo postInfo = modelMapper.map(post, PostInfo.class);
                    postInfo.setAccountUserName(accountUserName);
                    postInfo.setCategory(post.getCategory().getCategoryName());
                    return postInfo;
                })
                .collect(Collectors.toList());
    }

    public List<PostInfo> getAuthorByName(String name) {

        List<Post> postList = postRepository.findAll().stream()
                .filter(post -> post.getAccount().getUserName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        if (postList.isEmpty()) {
            throw new PostNotExistsException();
        }

        return postList.stream().map(post -> {
                    String accountUserName = post.getAccount().getUserName();
                    PostInfo postInfo = modelMapper.map(post, PostInfo.class);
                    postInfo.setAccountUserName(accountUserName);
                    postInfo.setCategory(post.getCategory().getCategoryName());
                    return postInfo;
                })
                .collect(Collectors.toList());
    }

    public Set<PostInfo> getPostByNameBlogText(String word) {

        List<PostInfo> postInfoList1 = new ArrayList<>();
        List<PostInfo> postInfoList2 = new ArrayList<>();
        List<PostInfo> allPostList = new ArrayList<>();

        try {
            postInfoList1 = getTitleByWord(word);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

            if (postInfoList1 != null) {

                postInfoList1.forEach(postInfo -> {
                    modelMapper.map(postInfo, PostInfo.class);
                    allPostList.add(postInfo);
                });
            }

            try {
                postInfoList2 = getBlogTextByWord(word);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (postInfoList2 != null) {

                    postInfoList2.forEach(postInfo -> {
                        modelMapper.map(postInfo, PostInfo.class);
                        allPostList.add(postInfo);
                    });
                }
            }

            if (allPostList.isEmpty()) {
                throw new PostNotExistsException();
            }
            Set<PostInfo> postInfoSet = new LinkedHashSet<>(allPostList);
            return postInfoSet;
        }
    }

    public PostInfoWithLikes likePost(Long postId) {
        Post post = findById(postId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = accountService.findAccountByUserName(authentication.getName());

        if (post.getLikes().contains(appUser.getId())) {
            throw new AlreadyLiked("You already liked this post", post.getText());
        }
        post.getLikes().add(appUser.getId());

        PostInfoWithLikes postInfoWithLikes = modelMapper.map(post, PostInfoWithLikes.class);

        postInfoWithLikes.setNumberOfLikes(post.getLikes().size());
        postInfoWithLikes.setCategory(post.getCategory().getCategoryName());


        return postInfoWithLikes;
    }


    public PostInfoLikes likesOnPost(Long postId) {
        Post post = findById(postId);
        PostInfoLikes postInfoLikes = new PostInfoLikes();
        postInfoLikes.setLikes(post.getLikes().size());
        List<String> userNames = post.getLikes().stream().map(accountService::findById).map(AppUser::getUserName).collect(Collectors.toList());
        postInfoLikes.setUserNames(userNames);
        return postInfoLikes;
    }

    public void removeLike(Long postId) {
        Post post = findById(postId);

        if (post.getLikes().isEmpty()) {
            log.warn("Post has no likes");
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = accountService.findAccountByUserName(authentication.getName());
        if (!post.getLikes().contains(appUser.getId())) {
            log.warn("You are not allowed to delete this like");
            return;
        }

        post.getLikes().remove(appUser.getId());
    }


    private Set<Tag> addTags(String incomingTags) {
        List<String> tagList;
        Set<Tag> tags = new HashSet<>();
        if (!incomingTags.isEmpty()) {
            tagList = Arrays.stream(incomingTags.split(" ")).collect(Collectors.toList());
            if (tagList.size() > 5) {
                for (int i = tagList.size() - 1; i == 5; i--) {
                    tagList.remove(tagList.get(i));
                }
            }
            for (String tagName : tagList) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }

        }
        return tags;
    }


    public List<PostInfo> searchByTags(List<String> tagWords) {
        return tagWords.stream().flatMap(tagWord ->
                postRepository.findByTagWords(Collections.singletonList(tagWord)).stream().map(post -> {
                    String accountUserName = post.getAccount().getUserName();
                    PostInfo postInfo = modelMapper.map(post, PostInfo.class);
                    postInfo.setAccountUserName(accountUserName);
                    postInfo.setCategory(post.getCategory().getCategoryName());
                    List<String> tagList = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());
                    postInfo.setTags(tagList);
                    return postInfo;
                })
        ).collect(Collectors.toList());
    }

    public PostInfo mostLikedPost() {

        List<Post> postList = postRepository.findMostLikedPost();

        Post mostLikedPost = postList.isEmpty() ? new Post() : postList.get(0);


        PostInfo postInfo = modelMapper.map(mostLikedPost, PostInfo.class);
        if (!mostLikedPost.getTags().isEmpty()) {
            List<String> tags = mostLikedPost.getTags().stream().map(Tag::getName).collect(Collectors.toList());
            postInfo.setTags(tags);
        }
        postInfo.setNumOfLikes(mostLikedPost.getLikes().size());
        postInfo.setAccountUserName(mostLikedPost.getAccount().getUserName());
        postInfo.setCategory(mostLikedPost.getCategory().getCategoryName());

        return postInfo;
    }



}


