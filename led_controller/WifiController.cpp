#include <WiFiClient.h>
#include <ESP8266WiFi.h>
#include "WifiController.h"
#include "LedController.h"
#include "Color.h"
#include "DAO.h"

void stoc(String s, char* c) {
  for(int i = 0; i < s.length(); i++) {
    c[i] = char(s[i]);
  }
  c[s.length()] = '\0';
}

WifiController::WifiController(DAO* dao, LedController* lc) {
  this->dao = dao;
  this->lc = lc;
}

void WifiController::begin() {
  String name = "Led-" + String(ESP.getChipId());
  char nameChars[name.length() + 1];
  stoc(name, nameChars);
  WiFi.hostname(name);
  if (!isConfigured()) {
    Serial.println("Not configured. Will setup Access-Point");
    WiFi.disconnect(true);
    WiFi.softAPConfig(IPAddress(10, 0, 0, 1),IPAddress(10, 0, 0, 1), IPAddress(255, 255, 255, 0));
    WiFi.softAP(nameChars);
    WiFi.mode(WIFI_AP);
  } else {
    Serial.println("Configured. Will connect station");
    WiFi.mode(WIFI_STA);
    WiFi.softAPdisconnect(true);
    String ssid = dao->getSSID();
    char ssidChar[ssid.length() + 1];
    stoc(ssid, ssidChar);
    String key = dao->getKey();
    char keyChar[key.length() + 1];
    stoc(key, keyChar);
    WiFi.begin(ssidChar, keyChar);
  }
}

WifiStations* WifiController::scanNetworks() {
  Serial.println("Scanning available networks...");
  int numSsid = WiFi.scanNetworks();
  if (numSsid == -1) {
    Serial.println("Couldn't get a wifi connection");
    while (true);
  }
  String* result = new String[numSsid];
  for (int thisNet = 0; thisNet < numSsid; thisNet++) {
    result[thisNet] = WiFi.SSID(thisNet);
  }
  return new WifiStations(result, numSsid);
}
void WifiController::stop() {

}

bool WifiController::isConfigured() {
  return dao->getState(STATE_CONFIGURED);
}

void WifiController::configure(String ssid, String key, String password) {
  dao->storeSSID(ssid);
  dao->storeKey(key);
  dao->storePassword(password);
  dao->storeState(STATE_CONFIGURED, true);
  dao->storeState(STATE_WIFISTATUS, false);
  // Not mode 0 anymore
  dao->storeMode(1);
}

void WifiController::connect() {
  int c = 0;
  bool wasConnectedBefore = dao->getState(STATE_WIFISTATUS);
  if (!wasConnectedBefore) {
    // We didn't have a good wifi status before. Blink orange while connecting
    lc->blink(new Color(255, 255, 0));
  }
  while (WiFi.status() != WL_CONNECTED && c < 10) {
    c++;
    delay(1000);
    Serial.print(".");
  }
  if (c == 10) {
    Serial.println("Could not connect to network. Will reset configuration.");
    dao->storeState(STATE_CONFIGURED, false);
    if (!dao->getState(STATE_WIFISTATUS)) {
      // We didn't have a good wifi status before. Blink red.
      lc->blink(new Color(255, 0, 0));
    }
    return;
  }
  lc->stopAnimation();
  if (!wasConnectedBefore) {
    // We didn't have a good wifi status before. First connection sets the light blue
    lc->setColor(new Color(0, 0, 255));
    dao->storeState(STATE_WIFISTATUS, true);
  }
  Serial.println("");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

}
