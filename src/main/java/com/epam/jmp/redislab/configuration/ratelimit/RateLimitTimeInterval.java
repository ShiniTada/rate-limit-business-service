package com.epam.jmp.redislab.configuration.ratelimit;

import java.time.LocalTime;
import java.util.function.Supplier;

public enum RateLimitTimeInterval {
    MINUTE(() -> LocalTime.now().getMinute()),
    HOUR(() -> LocalTime.now().getHour());

    private final Supplier<Integer> currentTimeProvider;

    RateLimitTimeInterval(Supplier<Integer> timeUnitProvider) {
        this.currentTimeProvider = timeUnitProvider;
    }

    public Integer getCurrentTime() {
        return currentTimeProvider.get();
    }

}
