package com.epam.jmp.redislab.configuration.ratelimit;

public enum RateLimitTimeInterval {

    MINUTE(60),
    HOUR(3600);

    private final long expireTimeInSeconds;

    RateLimitTimeInterval(long expireTimeInSeconds) {
        this.expireTimeInSeconds = expireTimeInSeconds;
    }

    public long getExpireTimeInSeconds() {
        return expireTimeInSeconds;
    }
}