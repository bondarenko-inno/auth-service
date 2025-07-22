package org.ebndrnk.authorizationservice.unit;

import jakarta.servlet.http.HttpServletRequest;
import org.ebndrnk.authorizationservice.util.DeviceIdExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceIdExtractorTest {

    @Mock
    private HttpServletRequest request;

    @Test
    void extract_ShouldReturnDeviceId_WhenHeaderPresent() {
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        DeviceIdExtractor extractor = new DeviceIdExtractor(request);

        String deviceId = extractor.extract();

        assertEquals("Mozilla/5.0", deviceId);
    }

    @Test
    void extract_ShouldThrowException_WhenHeaderMissing() {
        when(request.getHeader("User-Agent")).thenReturn(null);

        DeviceIdExtractor extractor = new DeviceIdExtractor(request);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, extractor::extract);
        assertEquals("Missing or empty User-Agent header", ex.getMessage());
    }

    @Test
    void extract_ShouldThrowException_WhenHeaderEmpty() {
        when(request.getHeader("User-Agent")).thenReturn("");

        DeviceIdExtractor extractor = new DeviceIdExtractor(request);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, extractor::extract);
        assertEquals("Missing or empty User-Agent header", ex.getMessage());
    }
}
