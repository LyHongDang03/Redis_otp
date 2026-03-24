package com.example.Redis.service;

import com.example.Redis.config.CtvAuthConfig;
import com.example.Redis.entity.HrmDataEntity;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    private final CtvAuthConfig config;

    public String generateAssessToken(HrmDataEntity ctv){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        Date now = new Date();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(ctv.getCode())
                .issuer(config.getJwtIssuer())
                .audience(config.getJwtAudience())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(now)
                .expirationTime(new Date(
                        now.toInstant()
                                .plusSeconds(config.getRefreshExpireSeconds())
                                .toEpochMilli()
                ))
                .claim("typ", "refresh")
                .claim("role", "CTV")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public String generateRefreshToken(HrmDataEntity ctv){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        Date now = new Date();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(ctv.getCode())
                .issuer(config.getJwtIssuer())
                .audience(config.getJwtAudience())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(now)
                .expirationTime(new Date(
                        now.toInstant()
                                .plusSeconds(config.getAccessExpireSeconds())
                                .toEpochMilli()
                ))
                .claim("typ", "access")
                .claim("role", "CTV")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }


    public JWTClaimsSet verifyToken(String token, CtvAuthConfig config) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(config.getJwtSecret());
        if (!signedJWT.verify(verifier)) {
            throw new RuntimeException("Invalid signature");
        }
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        Date exp = claims.getExpirationTime();
        if (exp.before(new Date())) {
            throw new RuntimeException("Token expired");
        }
        if (!config.getJwtIssuer().equals(claims.getIssuer())) {
            throw new RuntimeException("Invalid issuer");
        }
        return claims;
    }
}