#include <ESP8266Webserver.h>

class LedController;

class PowerRequestHandler {
public:
  PowerRequestHandler(ESP8266WebServer& refServer, LedController& lc) {
    this->server = &refServer;
    this->lc = &lc;
  }
  void postHandler() {
    int power = -1;
    for(int i = 0; i < server->args();i++) {
      String argName = server->argName(i);
      if (argName == "power") {
        power = server->arg(i).toInt();
      }
    }
    if (power == 1) {
      lc->switchOn();
    } else if (power == 0) {
      lc->switchOff();
    } else {
      lc->toggle();
    }
    server->send(200, "application/json charset=UTF-8;", "{\"message\": \"Success\"}");
  }
private:
  ESP8266WebServer* server;
  LedController* lc;
};
