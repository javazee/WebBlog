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

    @Query(value = "select p, " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' order by p.publicationTime desc")
    List<Object[]> getPostsOrderByTimeDesc(Pageable pageable);

    @Query(value = "select p, " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' order by p.publicationTime")
    List<Object[]> getPostsOrderByTime(Pageable pageable);

    @Query(value = "select p, " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id) as cc, " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1'), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' order by cc desc, p.publicationTime desc")
    List<Object[]> getPostsOrderByCountOfComment(Pageable page);

    @Query(value = "select p, " +
            "(select count(pc) from PostComment pc where p.id = pc.post.id), " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '1') as lk, " +
            "(select count(pv) from PostVote pv where p.id = pv.post.id and pv.value = '-1') as dk " +
            "from Post p where p.isActive = 1 and p.moderationStatus = 'ACCEPTED' order by lk desc, dk, p.publicationTime desc")
    List<Object[]> getPostsOrderByCountOfLike(Pageable page);
}
