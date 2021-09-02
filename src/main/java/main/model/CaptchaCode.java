package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table (name = "captcha_codes")
public class CaptchaCode {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date commentTime;

    @Column(columnDefinition = "TINYTEXT NOT NULL")
    private String code;

    @Column(name = "secret_code", columnDefinition = "TINYTEXT NOT NULL")
    private String secretCode;



}
