package main.model.repository;

import main.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<GlobalSetting, Integer> {

    GlobalSetting findByCode(@Param("code") String code);
}
