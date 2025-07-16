package org.ebndrnk.authorizationservice.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeviceIdExtractor {

    private static final String DEVICE_ID_HEADER = "User-Agent";

    private final HttpServletRequest request;

    public DeviceIdExtractor(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Extracts the device ID from the HTTP request header.
     * Uses the "User-Agent" header as the device identifier.
     *
     * @return deviceId string from header
     * @throws IllegalArgumentException if User-Agent header is missing or empty
     */
    public String extract() {
        String deviceId = request.getHeader(DEVICE_ID_HEADER);
        if (deviceId == null || deviceId.isEmpty()) {
            log.warn("User-Agent header is missing or empty");
            throw new IllegalArgumentException("Missing or empty User-Agent header");
        }
        log.debug("Extracted deviceId: {}", deviceId);
        return deviceId;
    }
}
