package federicolepore.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class MailgunService {

    private static final Logger log = LoggerFactory.getLogger(MailgunService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${mailgun.domainName}")
    private String domainName;
    @Value("${mailgun.apiKey}")
    private String apiKey;
    @Value("${mailgun.from}")
    private String from;

    public void sendEmail(String to, String subject, String text, String html) {
        String domain = domainName == null ? "" : domainName.trim();
        String key = apiKey == null ? "" : apiKey.trim();
        String sender = from == null ? "" : from.trim();

        String encodedDomain = UriUtils.encodePathSegment(domain, StandardCharsets.UTF_8);
        String url = "https://api.mailgun.net/v3/" + encodedDomain + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String auth = "api:" + key;
        String basicAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + basicAuth);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("from", "SkillSwap <" + sender + ">");
        body.add("to", to);
        body.add("subject", subject);
        body.add("text", text);
        body.add("html", html);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            log.info("Mailgun response status={}, body={}", response.getStatusCode(), response.getBody());
        } catch (HttpStatusCodeException ex) {
            log.error("Mailgun error status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        }
    }

    // vecchia a 3 parametri
    public void sendEmail(String to, String subject, String text) {
        sendEmail(to, subject, text, "<p>" + text + "</p>");
    }
}