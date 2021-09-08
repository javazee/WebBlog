package main.model.repository;

import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    @Query(value = "Select count(c) from PostComment c where c.post.id = :postId")
    int countCommentForPost(@Param("postId") int postId);
}
