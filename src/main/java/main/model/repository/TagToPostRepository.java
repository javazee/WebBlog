package main.model.repository;

import main.model.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagToPostRepository extends JpaRepository<TagToPost, Integer> {
}
