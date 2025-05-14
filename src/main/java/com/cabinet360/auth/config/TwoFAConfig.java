package com.cabinet360.auth.config;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwoFAConfig {

    @Value("${twofa.digits:6}")
    private int digits;

    @Value("${twofa.period:30}")
    private int period;

    @Value("${twofa.window:1}")
    private int window;

    @Value("${twofa.issuer:Cabinet360}")
    private String issuer;

    public String getIssuer() {
        return issuer;
    }

    public int getDigits() {
        return digits;
    }

    public int getPeriod() {
        return period;
    }

    @Bean
    public GoogleAuthenticator googleAuthenticator() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setCodeDigits(digits)
                .setTimeStepSizeInMillis(period * 1000L)
                .setWindowSize(window)
                .build();

        return new GoogleAuthenticator(config);
    }
}
