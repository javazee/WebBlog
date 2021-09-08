package main.model.repository;

import main.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

    @Query(value = "Select count(v) from PostVote v where v.post.id = :postId and v.value = 1")
    int countLikeForPost(@Param("postId") int postId);

    @Query(value = "Select count(v) from PostVote v where v.post.id = :postId and v.value = -1")
    int countDislikeForPost(@Param("postId") int postId);
}
