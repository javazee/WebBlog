package main.model.repository;

import main.model.Post;
import main.model.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TagToPostRepository extends JpaRepository<TagToPost, Integer> {

    @Query(value = "SELECT t.text, count(tp) FROM TagToPost tp join Tag t on tp.tag.id = t.id " +
            "join Post p on tp.post.id = p.id where t.text like %:query% and p.isActive = '1' " +
            "and p.moderationStatus = 'ACCEPTED' and p.publicationTime < CURRENT_TIMESTAMP() group by t.id ")
    List<Object[]> findTags(@Param("query") String query);

    @Query(value = "select t.text from Tag t join TagToPost tp on t.id = tp.tag.id " +
            "join Post p on tp.post.id = p.id where p.id = :id")
    List<String>  findTagsByPostId(@Param("id") int id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM TagToPost tp WHERE tp.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
