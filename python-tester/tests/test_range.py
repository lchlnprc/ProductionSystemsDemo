from __future__ import annotations


def test_range_validation(sensor_reader, recorder, config):
    with recorder.record_test("range_validation") as result:
        reading = sensor_reader.read_sample()
        result.readings.append(reading)
        assert config.min_temperature_c <= reading.temperature_c <= config.max_temperature_c
        assert config.min_humidity_pct <= reading.humidity_pct <= config.max_humidity_pct
