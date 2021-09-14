package main.model.repository;

import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {


    @Query("select count(p) from Post p where p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_DATE()")
    int countActiveAndAcceptedPosts();

    @Query("select count(p) from Post p where p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_DATE()" +
            "and (p.text LIKE %:query% OR p.title LIKE %:query%)")
    int countActiveAndAcceptedPostsWithQuery(@Param("query") String query);

    @Query("select count(p) from Post p where p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' and DATE(p.publicationTime) = :date")
    int countActiveAndAcceptedPostsWithDate(@Param("date") Date date);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_DATE() " +
            "GROUP BY p ")
    List<Post> getPostsOrderByTime(Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN PostComment pc ON pc.post.id = p.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_DATE() " +
            "GROUP BY p ORDER BY COUNT(pc) DESC")
    List<Post> getPostsOrderByCountOfComment(Pageable page);

    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN PostVote pv ON pv.post.id = p.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_DATE() " +
            "GROUP BY p ORDER BY COUNT(CASE pv.value WHEN 1 THEN 1 ELSE NULL END) DESC")
    List<Post> getPostsOrderByCountOfLike(Pageable page);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_DATE() " +
            "and (p.text LIKE %:query% OR p.title LIKE %:query%) GROUP BY p ")
    List<Post> searchPostsOrderByTimeDesc(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT YEAR(p.publicationTime) from Post p " +
            "WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate GROUP BY YEAR(p.publicationTime)")
    List<Integer> getYears(@Param("currentDate") Date date);

    @Query(value = "select date(p.publicationTime), count(p) from Post p where year(p.publicationTime) = :year and " +
            "p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < CURRENT_DATE() " +
            "group by date(p.publicationTime) order by date(p.publicationTime)")
    List<Object[]> getPostsCountByDate(@Param("year") int year);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime < CURRENT_DATE() " +
            "and DATE(p.publicationTime) = :date GROUP BY p ")
    List<Post> getPostsByDate(@Param("date") Date date, Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN TagToPost tp ON p.id = tp.post.id " +
            "LEFT JOIN Tag t on t.id = tp.tag.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_DATE() " +
            "and t.text like %:tag% GROUP BY p")
    List<Post> findPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime < CURRENT_DATE() " +
            "and p.id = :id")
    Post findPostById(@Param("id") int id);

}
