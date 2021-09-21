package main.model.repository;

import main.model.PostVote;
import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {

    @Query("SELECT v FROM PostVote v WHERE v.post.id = :post_id AND v.user = :user")
    Optional<PostVote> findVote(@Param("post_id") int postId, @Param("user") User user);
}
