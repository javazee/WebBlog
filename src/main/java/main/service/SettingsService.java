package main.service;

import main.api.request.GlobalSettingsRequest;
import main.api.response.SettingsResponse;
import main.model.GlobalSetting;
import main.model.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingsService {

    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public SettingsService(GlobalSettingsRepository globalSettingsRepository) {
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public SettingsResponse getGlobalSettings(){
        List<GlobalSetting> globalSettings = globalSettingsRepository.findAll();
        SettingsResponse settingsResponse = new SettingsResponse();
        for (GlobalSetting globalSetting: globalSettings) {
            if (globalSetting.getCode().equals("MULTIUSER_MODE"))
                settingsResponse.setMultiuserMode(globalSetting.getValue().equals("YES"));
            if (globalSetting.getCode().equals("POST_PREMODERATION"))
                settingsResponse.setPostPremoderation(globalSetting.getValue().equals("YES"));
            if (globalSetting.getCode().equals("STATISTICS_IS_PUBLIC"))
                settingsResponse.setStatisticsIsPublic(globalSetting.getValue().equals("YES"));
        }
        return settingsResponse;
    }

    public void setGlobalSettings(GlobalSettingsRequest request){
        GlobalSetting multiuserMode = globalSettingsRepository.findByCode("MULTIUSER_MODE");
        multiuserMode.setValue(request.isMultiUsed() ? "YES" : "NO");
        globalSettingsRepository.save(multiuserMode);
        GlobalSetting postPremoderation = globalSettingsRepository.findByCode("POST_PREMODERATION");
        postPremoderation.setValue(request.isPreModerated() ? "YES" : "NO");
        globalSettingsRepository.save(postPremoderation);
        GlobalSetting statisticsIsPublic = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");
        statisticsIsPublic.setValue(request.isPublic() ? "YES" : "NO");
        globalSettingsRepository.save(statisticsIsPublic);
    }
}
