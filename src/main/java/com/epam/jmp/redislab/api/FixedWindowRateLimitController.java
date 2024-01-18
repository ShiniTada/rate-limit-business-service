package com.epam.jmp.redislab.api;

import com.epam.jmp.redislab.api.validator.RequestValidator;
import com.epam.jmp.redislab.service.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ratelimit/fixedwindow")
public class FixedWindowRateLimitController {

    private final RateLimitService rateLimitService;
    private final RequestValidator requestValidator;

    public FixedWindowRateLimitController(RateLimitService rateLimitService, RequestValidator requestValidator) {
        this.rateLimitService = rateLimitService;
        this.requestValidator = requestValidator;
    }

    @PostMapping
    public ResponseEntity<?> shouldRateLimit(@RequestBody RateLimitRequest rateLimitRequest) {
        if (!requestValidator.isValid(rateLimitRequest)) {
            return ResponseEntity.badRequest().body("Invalid request. Each descriptor in request " +
                    "should has at least 1 of these 3 fields: accountId, clientIp, requestType");
        }
        if (rateLimitService.shouldLimit(rateLimitRequest.getDescriptors())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok().build();
    }

}
