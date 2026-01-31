from __future__ import annotations


def test_sensor_connectivity(sensor_reader, recorder):
    with recorder.record_test("connectivity") as result:
        reading = sensor_reader.read_sample()
        result.readings.append(reading)
        assert reading.temperature_c is not None
        assert reading.humidity_pct is not None
