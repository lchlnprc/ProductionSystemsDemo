package com.productionsystems.demo.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HumidityTrendPoint {
    String time;
    double humidity;
}
