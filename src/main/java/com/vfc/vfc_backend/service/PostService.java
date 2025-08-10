package com.vfc.vfc_backend.service;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.repository.PostRepository;
import com.vfc.vfc_backend.repository.PostReplyRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostReplyRepository postReplyRepository;

    @Autowired
    private FriendService friendService;

    public PostService(FriendService friendService,PostRepository postRepository,PostReplyRepository postReplyRepository) {
        this.friendService = friendService;
        this.postRepository = postRepository;
        this.postReplyRepository = postReplyRepository;
    }

    @Transactional
    public Post savePost(Post postDTO) {
        // Create Meal entity
        Post post = new Post(
                postDTO.getUserId(),
                postDTO.getPostMessage()
        );
        post.setPostDate(LocalDateTime.now());

        return postRepository.save(post);
    }

    public PostReply saveReply(PostReply postReply, int postId) {
        Post post = getPostById(postId);
        if (post != null) {
            postReply.setPost(post);
            postReply.setReplyDate(LocalDateTime.now());
            return postReplyRepository.save(postReply);
        }
        return null;
    }

    /*
    public Post getPostById(int id) {
        return postRepository.findById(id).orElse(null);
    }*/

    public Post getPostById(int postId) {
        Post post = postRepository.findByIdWithReplies(postId);
        System.out.println("Fetching post with ID: " + postId + ", Result: " + (post != null ? "Post found: " + post.getPostMessage() : "null"));
        return post;
    }

    public List<Post> getPostsByUserId(int userId, int postPage, int pageSize) {

        Pageable pageable = PageRequest.of(postPage - 1, pageSize); // Spring uses 0-based indexing
        return postRepository.findByUserId(userId, pageable).getContent();
    }

    public List<Post> getPostsByUserId(int userId) {

        //Pageable pageable = PageRequest.of(mealPage - 1, pageSize); // Spring uses 0-based indexing
        return postRepository.findByUserId(userId);
    }



    public long countPostsByUserId(int userId) {
        return postRepository.countByUserId(userId);
    }

    public List<Post> getFriendsPosts(int userId, int page, int pageSize) {
        // Get friend IDs
        List<Integer> friendIds = friendService.getFriends(userId).stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return List.of(); // Return empty list if no friends
        }

        /// /////
        friendIds.add(3);
        /// ////
        // Create pageable for pagination
        Pageable pageable = PageRequest.of(page - 1, pageSize); // 0-based indexing

        // Fetch posts by friend IDs, sorted by postDate descending
        Page<Post> postPage =postRepository.findByUserIdInOrderByPostDateDesc(friendIds, pageable);
        return postPage.getContent();
    }

    public long countFriendsPosts(int userId) {
        List<Integer> friendIds = friendService.getFriends(userId).stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        /*
        if (friendIds.isEmpty()) {
            return 0;
        }

        return postRepository.countByUserIdIn(friendIds);*/
        return friendIds.isEmpty() ? 0 : postRepository.countByUserIdIn(friendIds);
    }



    public void deleteById(int theId) {
        postRepository.deleteById(theId);
    }

    public long getWeeklyPostCount(int userId) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Ensure start of week is Monday
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate weekStart = today.with(weekFields.dayOfWeek(), 1);
        LocalDateTime startDateTime = weekStart.atStartOfDay();
        LocalDateTime endDateTime = weekStart.plusDays(6).atTime(23, 59, 59);

        // Query posts in the date range
        List<Post> posts = postRepository.findByUserIdAndDateRange(userId, startDateTime, endDateTime);
        return posts.size();
    }
}
