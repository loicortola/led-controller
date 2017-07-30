#include <Arduino.h>
#include "WifiStations.h"

class DAO;
class LedController;

/**
 * The Led Controller controls all the leds of the Beacon
 */
class WifiController {
public:
  WifiController(DAO* dao, LedController* lc);
  void begin();
  WifiStations* scanNetworks();
  void stop();
  bool isConfigured();
  void configure(String ssid, String key, String password);
  void connect();
private:
  DAO* dao;
  LedController* lc;
};
