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
        StatisticsResponse response = new StatisticsResponse();
        response.setPostsCount(postRepository.personalStatsByCountPosts(user));
        response.setLikesCount(postRepository.personalStatsByCountLikes(user));
        response.setDislikesCount(postRepository.personalStatsByCountDislikes(user));
        Integer countOfView = postRepository.personalStatsByViewCount(user);
        response.setViewsCount(countOfView == null ? 0 : countOfView);
        Date date = postRepository.firstPublicationDateOfUserPosts(user);
        if (date != null){
            response.setFirstPublication(date.getTime() / 1000);
        }
        return response;
    }

    public StatisticsResponse getGeneralStatistics(){
        StatisticsResponse response = new StatisticsResponse();
        response.setPostsCount(postRepository.generalStatsByCountPosts());
        response.setLikesCount(postRepository.generalStatsByCountLikes());
        response.setDislikesCount(postRepository.generalStatsByCountDislikes());
        Integer countOfView = postRepository.generalStatsByViewCount();
        response.setViewsCount(countOfView == null ? 0 : countOfView);
        Date date = postRepository.firstPublicationDateOfAllPosts();
        if (date != null){
            response.setFirstPublication(date.getTime() / 1000);
        }
        return response;
    }
}
