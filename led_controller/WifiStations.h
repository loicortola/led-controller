#include <Arduino.h>

class WifiStations {
public:
  WifiStations(String* values, int count) {
    this->values = values;
    this->count = count;
  }
  int getCount() {
    return this->count;
  }
  String getValue(int i) {
    return this->values[i];
  }
private:
  String* values;
  int count;
};
