from __future__ import annotations

import statistics


def test_stability(sensor_reader, recorder, config):
    with recorder.record_test("stability") as result:
        readings = [sensor_reader.read_sample() for _ in range(config.stability_samples)]
        result.readings.extend(readings)

        temps = [r.temperature_c for r in readings]
        hums = [r.humidity_pct for r in readings]

        temp_std = statistics.pstdev(temps)
        hum_std = statistics.pstdev(hums)

        assert temp_std <= config.max_temperature_stddev
        assert hum_std <= config.max_humidity_stddev
