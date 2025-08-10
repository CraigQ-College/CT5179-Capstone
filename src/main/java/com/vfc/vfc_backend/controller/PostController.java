package com.vfc.vfc_backend.controller;

import com.vfc.vfc_backend.model.*;
import com.vfc.vfc_backend.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/add-post")
    public String showAddPostForm( HttpSession session,Model model) {
        User user = (User) session.getAttribute("user");


        Post post = new Post();
        post.setUserId(user.getUserId());
        model.addAttribute("post", post);
        model.addAttribute("user", user);

        return "add-post";
    }

    /*
    @GetMapping("/listPosts")
    public String listPosts(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        //int pageSize = 10;

        List<Post> posts = postService.getPostsByUserId(user.getUserId());
        //List<Post> posts = postService.getPostsByUserId(user.getUserId(),postPage,pageSize);

        //List<Workout> workouts = workoutService.getWorkoutsByUserId(userId, workoutPage, pageSize);
        //long totalWorkouts = workoutService.countWorkoutsByUserId(userId);
        //int totalWorkoutPages = (int) Math.ceil((double) totalWorkouts / pageSize);


        model.addAttribute("posts", posts);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("userName", user.getUserName());
        return "list-posts";
    }*/

    @GetMapping("/listPosts")
    public String showDashboard2ForUser(@RequestParam(defaultValue = "1") int postPage,HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        int userId = user.getUserId();
        int pageSize = 10;

        List<Post> posts = postService.getPostsByUserId(userId, postPage, pageSize);
        long totalPosts = postService.countPostsByUserId(userId);
        int totalPostPages = (int) Math.ceil((double) totalPosts / pageSize);

        String name = user.getUserName();
        // Add to model

        model.addAttribute("posts",posts);
        model.addAttribute("userId", userId);
        //model.addAttribute("userName", sessionUser.getUserName());

        model.addAttribute("currentPostPage", postPage);
        model.addAttribute("totalPostPages", totalPostPages);

        model.addAttribute("user", user);
        model.addAttribute("userName", name);

        return "list-posts";
    }

    @GetMapping("/listFeed")
    public String showFeed(@RequestParam(defaultValue = "1") int feedPostPage,HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        int userId = user.getUserId();
        int pageSize = 10;

        long totalFeedPosts = postService.countFriendsPosts(userId);
        int totalFeedPostPages = (int) Math.ceil((double) totalFeedPosts / pageSize);

        List<Post> posts = postService.getFriendsPosts(userId,feedPostPage,pageSize);
        //List<Post> posts = postService.getPostsByUserId(user.getUserId(),postPage,pageSize);

        //List<Workout> workouts = workoutService.getWorkoutsByUserId(userId, workoutPage, pageSize);
        //long totalWorkouts = workoutService.countWorkoutsByUserId(userId);
        //int totalWorkoutPages = (int) Math.ceil((double) totalWorkouts / pageSize);

        model.addAttribute("currentFeedPostPage",feedPostPage);
        model.addAttribute("totalFeedPostPages", totalFeedPostPages);

        model.addAttribute("feedPosts", posts);
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("userName", user.getUserName());
        return "feed";
    }


    @GetMapping("/show-post/{postId}")
    public String showPost(@PathVariable int postId, @RequestParam(defaultValue = "feed") String source, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Post post = postService.getPostById(postId);
        System.out.println("Post ID: " + postId + ", Post: " + post);
        if (post == null) {
            return "redirect:/posts/listFeed";
        }
        PostReply reply = new PostReply();
        reply.setUserId(user.getUserId());

        model.addAttribute("post", post);
        model.addAttribute("reply", reply);
        model.addAttribute("user", user);
        model.addAttribute("source", source);
        return "show-post";
    }

    @PostMapping("/save-post")
    public String savePost(@ModelAttribute("post") Post post) {
        post.setPostDate(LocalDateTime.now());
        Post savedPost = postService.savePost(post);
        return "redirect:/posts/show-post/" + savedPost.getPostId() + "?source=feed";
    }


    @PostMapping("/delete-post/{postId}")
    public String deletePost(@PathVariable("postId") int postId ){
        postService.deleteById(postId);
        return "redirect:/posts/listPosts" ;
    }

    @GetMapping("/reply/{postId}")
    public String showReplyForm(@PathVariable int postId, Model model) {
        model.addAttribute("reply", new PostReply());
        model.addAttribute("postId", postId);
        return "add-reply";
    }
    /*
    @PostMapping("/save-reply/{postId}")
    public String saveReply(@ModelAttribute("reply") PostReply reply, @PathVariable int postId) {
        postService.saveReply(reply, postId);
        return "redirect:/";
    }*/
    @PostMapping("/save-reply/{postId}")
    public String saveReply(@ModelAttribute("reply") PostReply reply, @PathVariable int postId, @RequestParam String source, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        reply.setReplyDate(LocalDateTime.now());
        reply.setUserId(user.getUserId());
        postService.saveReply(reply, postId);

        return "redirect:/posts/show-post/" + postId + "?source=" + source;
    }
}
