// ✅ TwoFAService.java
package com.cabinet360.auth.service;

import com.cabinet360.auth.config.TwoFAConfig;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TwoFAService {

    private final GoogleAuthenticator gAuth;
    private final TwoFAConfig config;

    public TwoFAService(GoogleAuthenticator gAuth, TwoFAConfig config) {
        this.gAuth = gAuth;
        this.config = config;
    }

    // 🔐 Generate a new TOTP secret
    public String generateSecretKey() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    // ✅ Verify a submitted 2FA code using stored secret
    public boolean verifyCode(String secret, int code) {
        return gAuth.authorize(secret, code);
    }

    // 📌 Build raw otpauth:// URI
    public String buildQrUrl(String email, String secret) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=%d&period=%d",
                config.getIssuer(), email, secret,
                config.getIssuer(), config.getDigits(), config.getPeriod()
        );
    }

    // 🖼️ Return public link to Google Charts QR Code
    public String getQrImageUrl(String email, String secret) {
        String otpAuthUrl = buildQrUrl(email, secret);
        return "https://api.qrserver.com/v1/create-qr-code/?data=" +
                URLEncoder.encode(otpAuthUrl, StandardCharsets.UTF_8) +
                "&size=200x200";
    }
}
