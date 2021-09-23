package main.service;

import main.api.response.StatisticsResponse;
import main.model.User;
import main.model.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StatisticsService {

    private final PostRepository postRepository;

    @Autowired
    public StatisticsService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public StatisticsResponse getStatistics(User user){
        int countPosts = postRepository.statsByCountPosts(user);
        System.out.println(countPosts);
        int countLikes = postRepository.statsByCountLikes(user);
        int countDislikes = postRepository.statsByCountDislikes(user);
        int countOfView = postRepository.statsByViewCount(user);
        Date date = postRepository.firstPublicationDate(user);
        return new StatisticsResponse(
                countPosts,
                countLikes,
                countDislikes,
                countOfView,
                date.getTime() / 1000);
    }
}
