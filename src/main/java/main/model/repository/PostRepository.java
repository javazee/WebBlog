package main.model.repository;

import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "Select count(p) from Post p where p.moderationStatus = 'ACCEPTED' and p.isActive = '1'")
    long countWithAcceptedStatusAndActiveState();

    @Query(value = "select id, time, title, text, view_count, " +
            "(select count(*) from post_comments  where posts.id = post_comments.post_id) as comment_count, " +
            "(select count(*) from post_votes where posts.id = post_votes.post_id and value = '1') as like_count, " +
            "(select count(*) from post_votes where posts.id = post_votes.post_id and value = '-1') as dislike_count, " +
            "(select id from users where posts.user_id = users.id) as user_id, " +
            "(select name from users where posts.user_id = users.id) as user_name " +
            "from posts where is_active = 1 and moderation_status = 'ACCEPTED'",
            nativeQuery = true)
    List<Object[]> getPostsOrderByTime(Pageable page);

    @Query(value = "select id, time, title, text, view_count, " +
            "(select count(*) from post_comments  where posts.id = post_comments.post_id) as comment_count, " +
            "(select count(*) from post_votes where posts.id = post_votes.post_id and value = '1') as like_count, " +
            "(select count(*) from post_votes where posts.id = post_votes.post_id and value = '-1') as dislike_count, " +
            "(select id from users where posts.user_id = users.id) as user_id, " +
            "(select name from users where posts.user_id = users.id) as user_name " +
            "from posts where is_active = 1 and moderation_status = 'ACCEPTED' order by comment_count desc, time desc",
            nativeQuery = true)
    List<Object[]> getPostsOrderByCountOfComment(Pageable page);

    @Query(value = "select id, time, title, text, view_count, " +
            "(select count(*) from post_comments  where posts.id = post_comments.post_id) as comment_count, " +
            "(select count(*) from post_votes where posts.id = post_votes.post_id and value = '1') as like_count, " +
            "(select count(*) from post_votes where posts.id = post_votes.post_id and value = '-1') as dislike_count, " +
            "(select id from users where posts.user_id = users.id) as user_id, " +
            "(select name from users where posts.user_id = users.id) as user_name " +
            "from posts where is_active = 1 and moderation_status = 'ACCEPTED' order by like_count desc, time desc",
            nativeQuery = true)
    List<Object[]> getPostsOrderByCountOfLike(Pageable page);
}
