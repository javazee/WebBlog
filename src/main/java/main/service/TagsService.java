package main.service;

import main.api.response.tagsResponse.TagResponse;
import main.api.response.tagsResponse.TagsResponse;
import main.model.repository.PostRepository;
import main.model.repository.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TagsService {

    private final TagToPostRepository tagToPostRepository;

    private final PostRepository postRepository;

    @Autowired
    public TagsService(TagToPostRepository tagToPostRepository,
                       PostRepository postRepository) {
        this.tagToPostRepository = tagToPostRepository;
        this.postRepository = postRepository;
    }


    public TagsResponse getTags(String query){
        List<Object[]> tagToPosts = tagToPostRepository.findTags((query == null)? "": query, new Date());
        //количество всех постов
        long count = postRepository.countActiveAndAcceptedPost(new Date());

        //ненормализованный вес самого популярного тега
        float weight = 1;
        Optional<Object[]> mostOftenTag = tagToPosts.stream().max(Comparator.comparing(a -> Integer.parseInt(a[1].toString())));
        if (mostOftenTag.isPresent()){
            weight = Float.parseFloat(mostOftenTag.get()[1].toString()) / count;
        }

        //коэффициент для нормализации
        float k = 1 / weight;

        //создание объекта response и заполнение его данными
        TagsResponse tagsResponse = new TagsResponse();
        for (Object[] tag: tagToPosts){
                TagResponse tagResponse = new TagResponse();
                tagResponse.setName(tag[0].toString());
                tagResponse.setWeight(Integer.parseInt(tag[1].toString()) * k / count);
                tagsResponse.getTags().add(tagResponse);
        }
        return tagsResponse;
    }
}
