#include <DHT.h>

#define DHTPIN 2        // Pin connected to DATA
#define DHTTYPE DHT11  // DHT 11

DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(115200);
  delay(1000);

  Serial.println("DHT11 TEST PROGRAM");
  Serial.println("Type\tStatus\tHumidity(%)\tTemperature(C)");

  dht.begin();
}

void loop() {
  float humidity = dht.readHumidity();
  float temperature = dht.readTemperature(); // Celsius

  Serial.print("DHT11\t");

  if (isnan(humidity) || isnan(temperature)) {
    Serial.print("ERROR\t");
    Serial.println("Failed to read from sensor");
  } else {
    Serial.print("OK\t");
    Serial.print("Humidity: ");
    Serial.print(humidity, 1);
    Serial.print("\t\t");
    Serial.print("Temperature: ");
    Serial.println(temperature, 1);
  }

  delay(1000);
}
