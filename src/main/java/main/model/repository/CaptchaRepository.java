package main.model.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaCode, Integer> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM CaptchaCode cc WHERE TIMESTAMP(cc.generationTime) < :time")
    void deleteOldCaptcha(@Param("time") Date time);

    @Query(value = "SELECT c.code FROM CaptchaCode c WHERE c.secretCode = :code")
    String getCodeBySecretCode(@Param("code") String code);
}
