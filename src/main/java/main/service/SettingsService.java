package main.service;

import main.api.response.SettingsResponse;
import main.model.GlobalSetting;
import main.model.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    @Autowired
    GlobalSettingsRepository globalSettingsRepository;

    GlobalSetting multiuserMode;
    GlobalSetting postPremoderation;
    GlobalSetting statisticsIsPublic;

    SettingsResponse settingsResponse;

    public SettingsResponse getGlobalSettings(){
        multiuserMode  = globalSettingsRepository.findByCode("MULTIUSER_MODE");
        postPremoderation = globalSettingsRepository.findByCode("POST_PREMODERATION");
        statisticsIsPublic = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");
        settingsResponse = new SettingsResponse();
        if (multiuserMode != null) settingsResponse.setMultiuserMode(multiuserMode.getValue().equals("YES"));
        if (postPremoderation != null) settingsResponse.setPostPremoderation(postPremoderation.getValue().equals("YES"));
        if (statisticsIsPublic != null) settingsResponse.setStatisticsIsPublic(statisticsIsPublic.getValue().equals("YES"));
        return settingsResponse;
    }

    public void setMultiuserMode (boolean mode){
        multiuserMode  = globalSettingsRepository.findByCode("MULTIUSER_MODE");
        multiuserMode.setValue(mode ? "YES" : "NO");
        globalSettingsRepository.save(multiuserMode);
    }

    public void setPostPremoderation (boolean status){
        postPremoderation = globalSettingsRepository.findByCode("POST_PREMODERATION");
        postPremoderation.setValue(status ? "YES" : "NO");
        globalSettingsRepository.save(postPremoderation);
    }

    public void setStatisticsIsPublic (boolean isPublic){
        statisticsIsPublic = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");
        statisticsIsPublic.setValue(isPublic ? "YES" : "NO");
        globalSettingsRepository.save(statisticsIsPublic);
    }
}
