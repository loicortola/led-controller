#include <ESP8266WebServer.h>

class WifiStations;
class LedController;

class WifiRequestHandler {
public:
  WifiRequestHandler(ESP8266WebServer& refServer, LedController& lc, WifiController& wc) {
    this->server = &refServer;
    this->lc = &lc;
    this->wc = &wc;
  }
  void getHandler() {
    WifiStations *networks = wc->scanNetworks();
    String result = "<select name=\"ssid\" id=\"ssid\">";
    for(int i = 0; i < networks->getCount(); i++) {
      result += "<option value=\"";
      result += networks->getValue(i);
      result += "\"> ";
      result += networks->getValue(i);
      result += "</option>";
    }
    delete networks;
    result += "</select>";
    server->send(200, "text/html charset=UTF-8;", result);
  }
  void postHandler() {
    String ssid = server->arg("ssid");
    String key = server->arg("key");
    String password = server->arg("password");
    if (password == NULL || password.length() == 0) {
      server->send(400, "text/html charset=UTF-8;", "Password required.");
      return;
    }
    wc->configure(ssid, key, password);
    server->send(200, "text/html charset=UTF-8;", "");
  }
private:
  ESP8266WebServer* server;
  LedController* lc;
  WifiController* wc;
};
