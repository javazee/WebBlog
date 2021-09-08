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

    @Query(value = "Select p from Post p where p.moderationStatus = 'ACCEPTED' and p.isActive = '1'")
    List<Post> getPostsOrderByTime(Pageable page);

    @Query(value = "select * from posts order by (select count(*) from post_comments  where posts.id = post_comments.post_id) desc, posts.time desc",
            nativeQuery = true)
    List<Post> getPostsOrderByCountOfComment(Pageable page);

    @Query(value = "select * from posts order by (select count(*) from post_votes where posts.id = post_votes.post_id and post_votes.value = '1') desc, posts.time desc",
            nativeQuery = true)
    List<Post> getPostsOrderByCountOfLike(Pageable page);

}
