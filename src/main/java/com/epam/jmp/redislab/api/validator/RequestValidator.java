package com.epam.jmp.redislab.api.validator;

import com.epam.jmp.redislab.api.RateLimitRequest;
import com.epam.jmp.redislab.api.RequestDescriptor;

public class RequestValidator {

    /***
     * Verify whether every descriptor in request has at least 1 of these 3 fields:
     * accountId, clientIp, requestType
     * @param request - rate limit request
     * @return boolean whether the request is valid
     */
    public boolean isValid(RateLimitRequest request) {
        for (RequestDescriptor descriptor : request.getDescriptors()) {
            boolean noAccountId = !descriptor.getAccountId().isPresent() || descriptor.getAccountId().get().isEmpty();
            boolean noClientIp = !descriptor.getClientIp().isPresent() || descriptor.getClientIp().get().isEmpty();
            boolean noRequestType = !descriptor.getRequestType().isPresent() || descriptor.getRequestType().get().isEmpty();
            if (noAccountId && noClientIp && noRequestType) {
                return false;
            }
        }
        return true;
    }
}
