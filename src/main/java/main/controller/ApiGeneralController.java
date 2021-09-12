package main.controller;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.tagsResponse.TagsResponse;
import main.service.SettingsService;
import main.service.TagsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagsService tagsService;

    public ApiGeneralController(InitResponse initResponse, SettingsService service, TagsService tagsService) {
        this.initResponse = initResponse;
        this.settingsService = service;
        this.tagsService = tagsService;
    }

    @GetMapping("/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    private SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    private TagsResponse tags(@RequestParam(required = false) String query){
        return tagsService.getTags(query);
    }
}
