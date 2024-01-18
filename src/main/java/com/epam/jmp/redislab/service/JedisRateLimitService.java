package com.epam.jmp.redislab.service;

import com.epam.jmp.redislab.api.RequestDescriptor;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitRule;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitTimeInterval;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.args.ExpiryOption;

import java.util.Optional;
import java.util.Set;

@Component
public class JedisRateLimitService implements RateLimitService {

    private final RateLimitRuleSearcher rateLimitRuleSearcher;
    private final JedisCluster jedisCluster;

    private static final String EMPTY = "";

    public JedisRateLimitService(RateLimitRuleSearcher rateLimitRuleSearcher, JedisCluster jedisCluster) {
        this.rateLimitRuleSearcher = rateLimitRuleSearcher;
        this.jedisCluster = jedisCluster;
    }

    @Override
    public boolean shouldLimit(Set<RequestDescriptor> descriptors) {
        return descriptors.stream()
                .anyMatch(this::shouldLimitDescriptor);
    }

    private boolean shouldLimitDescriptor(RequestDescriptor descriptor) {
        Optional<RateLimitRule> rule = rateLimitRuleSearcher.searchByRequestDescriptor(descriptor);
        if (rule.isPresent()) {
            long currentRequestNumber = incrementAndGetRequestNumber(descriptor, rule.get().getTimeInterval());
            return rule.get().isForbidden(currentRequestNumber);
        }
        return true;
    }

    private long incrementAndGetRequestNumber(RequestDescriptor requestDescriptor, RateLimitTimeInterval timeInterval) {
        String accountId = requestDescriptor.getAccountId().orElse(EMPTY);
        String clientIp = requestDescriptor.getClientIp().orElse(EMPTY);
        String requestType = requestDescriptor.getRequestType().orElse(EMPTY);
        String redisKey = String.format("requestDescriptor:%s:%s:%s", accountId, clientIp, requestType);

        final long requestNumber = jedisCluster.incr(redisKey);
        jedisCluster.expire(redisKey, timeInterval.getExpireTimeInSeconds(), ExpiryOption.NX);
        return requestNumber;
    }
}
