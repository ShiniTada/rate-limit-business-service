package com.epam.jmp.redislab.service;

import com.epam.jmp.redislab.api.RequestDescriptor;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitRule;

import org.springframework.stereotype.Service;


import java.util.Optional;
import java.util.Set;

@Service
public class RateLimitRuleSearcher {

    private final Set<RateLimitRule> rateLimitRules;
    public RateLimitRuleSearcher(Set<RateLimitRule> rateLimitRules) {
        this.rateLimitRules = rateLimitRules;
    }

    public Optional<RateLimitRule> searchByRequestDescriptor(RequestDescriptor descriptor) {
        return rateLimitRules.stream()
                .filter(rule -> isDescriptorApplicableForRule(descriptor, rule))
                .findFirst();
    }

    private boolean isDescriptorApplicableForRule(RequestDescriptor descriptor, RateLimitRule rule) {
        boolean matchedAccountId = isMatchedFields(descriptor.getAccountId(), rule.getAccountId());
        if (!matchedAccountId) {
            boolean fieldsArePresent = descriptor.getAccountId().isPresent() && rule.getAccountId().isPresent();
            if (fieldsArePresent && rule.getAccountId().get().isEmpty()) {
                matchedAccountId = rateLimitRules.stream()
                        .noneMatch(r -> descriptor.getAccountId().get().equals(r.getAccountId()));
            }
        }

        boolean matchedClientIp = isMatchedFields(descriptor.getClientIp(), rule.getClientIp());
        if (!matchedClientIp) {
            boolean fieldsArePresent = descriptor.getClientIp().isPresent() && rule.getClientIp().isPresent();
            if (fieldsArePresent && rule.getClientIp().get().isEmpty()) {
                matchedAccountId = rateLimitRules.stream()
                        .noneMatch(r -> descriptor.getClientIp().get().equals(r.getClientIp()));
            }
        }

        boolean matchedRequestType = isMatchedFields(descriptor.getRequestType(), rule.getRequestType());
        if (!matchedRequestType) {
            boolean fieldsArePresent = descriptor.getRequestType().isPresent() && rule.getRequestType().isPresent();
            if (fieldsArePresent && rule.getClientIp().get().isEmpty()) {
                matchedAccountId = rateLimitRules.stream()
                        .noneMatch(r -> descriptor.getRequestType().get().equals(r.getRequestType()));
            }
        }
        return matchedAccountId && matchedClientIp && matchedRequestType;
    }

    private boolean isMatchedFields(Optional<String> descriptorField, Optional<String> ruleField) {
        if (!descriptorField.isPresent() && !ruleField.isPresent()) {
            return true;
        }
        return descriptorField.isPresent() && ruleField.isPresent() && descriptorField.equals(ruleField);
    }
}
