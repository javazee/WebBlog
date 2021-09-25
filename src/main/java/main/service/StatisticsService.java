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

    public StatisticsResponse getPersonalStatistics(User user){
        int countPosts = postRepository.personalStatsByCountPosts(user);
        int countLikes = postRepository.personalStatsByCountLikes(user);
        int countDislikes = postRepository.personalStatsByCountDislikes(user);
        int countOfView = postRepository.personalStatsByViewCount(user);
        Date date = postRepository.firstPublicationDateOfUserPosts(user);
        return new StatisticsResponse(
                countPosts,
                countLikes,
                countDislikes,
                countOfView,
                date.getTime() / 1000);
    }

    public StatisticsResponse getGeneralStatistics(){
        int countPosts = postRepository.generalStatsByCountPosts();
        int countLikes = postRepository.generalStatsByCountLikes();
        int countDislikes = postRepository.generalStatsByCountDislikes();
        int countOfView = postRepository.generalStatsByViewCount();
        Date date = postRepository.firstPublicationDateOfAllPosts();
        return new StatisticsResponse(
                countPosts,
                countLikes,
                countDislikes,
                countOfView,
                date.getTime() / 1000);
    }
}
