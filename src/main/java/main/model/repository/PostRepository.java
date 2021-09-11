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
}
