package com.epam.jmp.redislab.service;

import com.epam.jmp.redislab.api.RequestDescriptor;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitRule;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitTimeInterval;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.args.ExpiryOption;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class JedisRateLimitService implements RateLimitService {

    private final RateLimitRuleSearcher rateLimitRuleSearcher;
    private final JedisCluster jedisCluster;

    private static final String EMPTY = "";
    private static final long KEY_TTL = TimeUnit.HOURS.toSeconds(1);

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
        String redisKey = String.format("requestDescriptor:%s:%s:%s:%s_%s", accountId, clientIp, requestType, timeInterval.name(), timeInterval.getCurrentTime());

        final long requestNumber = jedisCluster.incr(redisKey);
        jedisCluster.expire(redisKey, KEY_TTL, ExpiryOption.NX);
        return requestNumber;
    }
}
