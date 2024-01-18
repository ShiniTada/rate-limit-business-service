package com.epam.jmp.redislab.api.validator;

import com.epam.jmp.redislab.api.RateLimitRequest;
import com.epam.jmp.redislab.api.RequestDescriptor;
import org.junit.platform.commons.util.StringUtils;

public class RequestValidator {

    /***
     * Verify whether every descriptor in request has at least 1 of these 3 fields:
     * accountId, clientIp, requestType
     * @param request - rate limit request
     * @return boolean whether the request is valid
     */
    public boolean isValid(RateLimitRequest request) {
        for (RequestDescriptor descriptor : request.getDescriptors()) {
            boolean noAccountId = StringUtils.isBlank(descriptor.getAccountId().get());
            boolean noClientIp = StringUtils.isBlank(descriptor.getClientIp().get());
            boolean noRequestType = StringUtils.isBlank(descriptor.getRequestType().get());
            if (noAccountId && noClientIp && noRequestType) {
                return false;
            }
        }
        return true;
    }
}
