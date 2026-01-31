package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class LatestDashboardResponse {
    long activeRuns;
    long totalDevicesToday;
    long passCount;
    long failCount;
    String systemHealth;
    List<TestRunResponse> recentRuns;
    List<TemperatureTrendPoint> temperatureTrend;
    List<HumidityTrendPoint> humidityTrend;
}
