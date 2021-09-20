package main.model.repository;

import main.model.Post;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {


    @Query("select count(p) from Post p where p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_TIMESTAMP()")
    int countActiveAndAcceptedPosts();

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_TIMESTAMP() " +
            "GROUP BY p ")
    Page<Post> getPostsOrderByTime(Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN PostComment pc ON pc.post.id = p.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_TIMESTAMP() " +
            "GROUP BY p ORDER BY COUNT(pc) DESC")
    Page<Post> getPostsOrderByCountOfComment(Pageable page);

    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN PostVote pv ON pv.post.id = p.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_TIMESTAMP() " +
            "GROUP BY p ORDER BY COUNT(CASE pv.value WHEN 1 THEN 1 ELSE NULL END) DESC")
    Page<Post> getPostsOrderByCountOfLike(Pageable page);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_TIMESTAMP() " +
            "and (p.text LIKE %:query% OR p.title LIKE %:query%) GROUP BY p ")
    Page<Post> searchPostsOrderByTimeDesc(@Param("query") String query, Pageable pageable);

    @Query(value = "SELECT YEAR(p.publicationTime) from Post p " +
            "WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate GROUP BY YEAR(p.publicationTime)")
    List<Integer> getYears(@Param("currentDate") Date date);

    @Query(value = "select date(p.publicationTime), count(p) from Post p where year(p.publicationTime) = :year and " +
            "p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < CURRENT_TIMESTAMP() " +
            "group by date(p.publicationTime) order by date(p.publicationTime)")
    List<Object[]> getPostsCountByDate(@Param("year") int year);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime < CURRENT_TIMESTAMP() " +
            "and DATE(p.publicationTime) = :date GROUP BY p ")
    Page<Post> getPostsByDate(@Param("date") Date date, Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "LEFT JOIN TagToPost tp ON p.id = tp.post.id " +
            "LEFT JOIN Tag t on t.id = tp.tag.id " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime <= CURRENT_TIMESTAMP() " +
            "and t.text like %:tag% GROUP BY p")
    Page<Post> findPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.publicationTime < CURRENT_TIMESTAMP() " +
            "and p.id = :id")
    Post getPostById(@Param("id") int id);

    @Query(value = "SELECT COUNT(p) FROM Post p WHERE p.moderationStatus = 'NEW' and p.isActive = 1")
    int countPostsForModeration();

    @Query(value = "SELECT p FROM Post p " +
            "WHERE p.isActive = :isActive AND p.moderationStatus = :status AND p.user.email = :email")
    Page<Post> getMyPosts(Pageable page,
                          @Param("isActive") boolean isActive,
                          @Param("status") ModerationStatus status,
                          @Param("email") String email);

    @Query(value = "SELECT p FROM Post p JOIN p.moderators ms ON ms.email = :email " +
            "WHERE p.isActive = 1 AND p.moderationStatus = :status")
    Page<Post> findMyModeratedPosts(Pageable pageable, @Param("status") ModerationStatus status, @Param("email") String email);

    @Query(value = "SELECT p FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'NEW'")
    Page<Post> findNewPostsForModeration(Pageable pageable);
}
