package main.model.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.response.postsResponse.AuthorOfPost;
import main.api.response.postsResponse.CommentResponse;
import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "select p, " +
            "(select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate), " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate ")
    List<Object[]> getPostsOrderByTime(@Param("currentDate") Date date,
                                       Pageable pageable);

    @Query(value = "select p, " +
            "(select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate), " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id) as cc, " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate " +
            "order by cc desc, p.publicationTime desc")
    List<Object[]> getPostsOrderByCountOfComment(@Param("currentDate") Date date,
                                                 Pageable page);

    @Query(value = "select p, " +
            "(select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate), " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1') as lk, " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') as dk " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate " +
            "order by lk desc, dk, p.publicationTime desc")
    List<Object[]> getPostsOrderByCountOfLike(@Param("currentDate") Date date,
                                              Pageable page);

    @Query(value = "select p, " +
            "(select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate " +
            "and (p.text like %:query% or p.title like %:query%)), " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate " +
            "and (p.text like %:query% or p.title like %:query%) order by p.publicationTime desc")
    List<Object[]> searchPostsOrderByTimeDesc(@Param("query") String query,
                                              @Param("currentDate") Date date,
                                              Pageable pageable);

    @Query(value = "select year(p.publicationTime) from Post p " +
            "where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate group by year(p.publicationTime)")
    List<Integer> getYears(@Param("currentDate") Date date);

    @Query(value = "select date(p.publicationTime), count(p) from Post p where year(p.publicationTime) = :year and " +
            "p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate " +
            "group by date(p.publicationTime) order by date(p.publicationTime)")
    List<Object[]> getPostsCountByDate(@Param("year") int year, @Param("currentDate") Date currentDate);

    @Query(value = "select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate ")
    int countActiveAndAcceptedPost(@Param("currentDate") Date date);

    @Query(value = "select p, " +
            "(select count(p) from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' " +
            "and p.publicationTime < :currentDate and date(p.publicationTime) = :date), " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate " +
            "and date(p.publicationTime) = :date order by p.publicationTime desc")
    List<Object[]> getPostsByDate(@Param("date") Date date, @Param("currentDate") Date currentDate, Pageable pageable);

    @Query(value = "select p, " +
            "(select count(p) from Post p join TagToPost tp on p.id = tp.post.id join Tag t on tp.tag.id = t.id " +
            "where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate " +
            "and t.text like %:tag%), " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') " +
            "from Post p join TagToPost tp on p.id = tp.post.id " +
            "join Tag t on tp.tag.id = t.id " +
            "where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate " +
            "and t.text like %:tag% order by p.publicationTime desc")
    List<Object[]> findPostsByTag(@Param("tag") String tag, @Param("currentDate") Date currentDate, Pageable pageable);

    @Query(value = "select p, u.id, u.name, " +
            "(select count(pv) from PostVote pv where pv.post.id = :id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where pv.post.id = :id and pv.value = '-1') " +
            "from Post p join User u on p.user.id = u.id " +
            "where p.moderationStatus = 'ACCEPTED' and p.publicationTime < :currentDate and p.id = :id")
    List<Object[]> findPostById(@Param("id") int id,
                                @Param("currentDate") Date date);

}
