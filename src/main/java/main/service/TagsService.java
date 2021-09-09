package main.service;

import main.api.response.tagsResponse.TagResponse;
import main.api.response.tagsResponse.TagsResponse;
import main.model.TagToPost;
import main.model.repository.PostRepository;
import main.model.repository.TagRepository;
import main.model.repository.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.Collections.*;

@Service
public class TagsService {

    private final TagRepository tagRepository;

    private final TagToPostRepository tagToPostRepository;

    private final PostRepository postRepository;

    @Autowired
    public TagsService(TagRepository tagRepository,
                       TagToPostRepository tagToPostRepository,
                       PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.tagToPostRepository = tagToPostRepository;
        this.postRepository = postRepository;
    }


    public TagsResponse getTags(){
        List<TagToPost> tagToPosts = tagToPostRepository.findAll();
        HashMap<Integer, Integer> tagsCount = new HashMap<>();
        for (TagToPost tagTopost: tagToPosts){
            if (tagsCount.containsKey(tagTopost.getTag().getId())){
                tagsCount.replace(tagTopost.getTag().getId(), tagsCount.get(tagTopost.getTag().getId()) + 1);
            } else tagsCount.put(tagTopost.getTag().getId(), 1);
        }

        //количество всех постов
        long count = postRepository.count();

        //ненормализованный вес самого популярного тега
        float weight = max(tagsCount.values()).floatValue() / count;

        //коэффициент для нормализации
        float k = 1 / weight;

        //создание объекта response и заполнение его данными
        TagsResponse tagsResponse = new TagsResponse();
        for (Map.Entry<Integer, Integer> entry: tagsCount.entrySet()){
            if (tagRepository.findById(entry.getKey()).isPresent()) {
                TagResponse tagResponse = new TagResponse();
                tagResponse.setName(tagRepository.findById(entry.getKey()).get().getText());
                tagResponse.setWeight(entry.getValue() * k / count);
                tagsResponse.getTags().add(tagResponse);
            }
        }

        return tagsResponse;
    }
}
