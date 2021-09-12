package main.model.repository;

import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    @Query(value = "select pc, u from PostComment pc join User u on pc.user.id = u.id where pc.post.id = :id")
    List<Object[]> findAllByPostId(@Param("id") int id);

}
