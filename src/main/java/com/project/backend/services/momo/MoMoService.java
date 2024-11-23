package com.project.backend.services.momo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

@Service
public class MoMoService {

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.access_key}")
    private String accessKey;

    @Value("${momo.secret_key}")
    private String secretKey;

    @Value("${momo.partner_code}")
    private String partnerCode;

    @Value("${momo.redirect_url}")
    private String redirectUrl;

    @Value("${momo.ipn_url}")
    private String ipnUrl;

    @Value("${momo.partner_name}")
    private String partnerName;

    @Value("${momo.store_id}")
    private String storeId;

    @Value("${momo.lang}")
    private String lang;

    @Value("${momo.request_type}")
    private String requestType;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> createMoMoPayment(String orderId, String requestId, String amount, String orderInfo)
            throws Exception {
        String extraData = "";

        // Create raw signature
        String rawSignature = "accessKey=" + accessKey + "&amount=" + amount + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId + "&requestType=" + requestType;

        // Generate HMAC SHA256 signature
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] hash = hmacSHA256.doFinal(rawSignature.getBytes());
        StringBuilder signature = new StringBuilder();
        for (byte b : hash) {
            signature.append(String.format("%02x", b));
        }

        // Create JSON request body
        Map<String, Object> data = new HashMap<>();
        data.put("partnerCode", partnerCode);
        data.put("orderId", orderId);
        data.put("partnerName", partnerName);
        data.put("storeId", storeId);
        data.put("ipnUrl", ipnUrl);
        data.put("amount", amount);
        data.put("lang", lang);
        data.put("requestType", requestType);
        data.put("redirectUrl", redirectUrl);
        data.put("autoCapture", true);
        data.put("orderInfo", orderInfo);
        data.put("requestId", requestId);
        data.put("extraData", extraData);
        data.put("signature", signature.toString());
        data.put("orderGroupId", "");

        // Send POST request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(data), headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);

        // Return the response
        return response.getBody();
    }
}
