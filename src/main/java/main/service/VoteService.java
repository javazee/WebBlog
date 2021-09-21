package main.service;

import main.api.response.VoteResponse;
import main.model.PostVote;
import main.model.User;
import main.model.repository.PostRepository;
import main.model.repository.PostVoteRepository;
import main.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class VoteService {

    private final UserRepository userRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostRepository postRepository;

    @Autowired
    public VoteService(UserRepository userRepository,
                       PostVoteRepository postVoteRepository,
                       PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postVoteRepository = postVoteRepository;
        this.postRepository = postRepository;
    }

    public VoteResponse like(int id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(username);
        Optional<PostVote> vote = postVoteRepository.findVote(id, user.get());
        if (vote.isPresent()){
            if (vote.get().getValue() == 1){
                postVoteRepository.delete(vote.get());
                return new VoteResponse(false);
            } else {
                vote.get().setValue((short) 1);
                vote.get().setVoteTime(new Date());
                postVoteRepository.save(vote.get());
                return new VoteResponse(true);
            }
        } else {
            PostVote like = new PostVote();
            like.setValue((short) 1);
            like.setPost(postRepository.findById(id).get());
            like.setUser(user.get());
            like.setVoteTime(new Date());
            postVoteRepository.save(like);
            return new VoteResponse(true);
        }
    }
    public VoteResponse dislike(int id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(username);
        Optional<PostVote> vote = postVoteRepository.findVote(id, user.get());
        if (vote.isPresent()){
            if (vote.get().getValue() == -1){
                postVoteRepository.delete(vote.get());
                return new VoteResponse(false);
            } else {
                vote.get().setValue((short) -1);
                vote.get().setVoteTime(new Date());
                postVoteRepository.save(vote.get());
                return new VoteResponse(true);
            }
        } else {
            PostVote dislike = new PostVote();
            dislike.setValue((short) - 1);
            dislike.setPost(postRepository.findById(id).get());
            dislike.setUser(user.get());
            dislike.setVoteTime(new Date());
            postVoteRepository.save(dislike);
            return new VoteResponse(true);
        }
    }
}
