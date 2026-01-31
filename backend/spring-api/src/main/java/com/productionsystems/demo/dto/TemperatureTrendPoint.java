package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TemperatureTrendPoint {
    String time;
    double temperature;
}
