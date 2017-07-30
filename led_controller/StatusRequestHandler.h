#include <ESP8266Webserver.h>

class LedController;
class Color;
class Animation;

class StatusRequestHandler {
public:
  StatusRequestHandler(ESP8266WebServer& refServer, LedController& lc) {
    this->server = &refServer;
    this->lc = &lc;
  }
  void getHandler() {
    int mode = lc->getMode();
    String result = "{\"mode\":" + String(mode) + ",";
    result += "\"switchedOn\":";
    result += lc->isSwitchedOn() ? "true," : "false,";
    switch (mode) {
      case 0: {
        result += "blinking:true";
        break;
      }
      case 1: {
        Color* c = lc->getColor();
        // Mode color
        result += "\"color\":{\"r\":" + String(c->getR()) + ",\"g\":" + String(c->getG()) + ",\"b\":" + String(c->getB()) + "}";
        break;
      }
      case 2: {
        Animation* a = lc->getAnimation();
        // Mode color
        result += "\"animation\":{\"r\":" + String(a->getR()) + ",\"g\":" + String(a->getG()) + ",\"b\":" + String(a->getB()) + ",\"loopTime\":" + a->getLoopTime() + "}";
        break;
      }
    }
    result += "}";
    server->send(200, "application/json charset=UTF-8;", result);
  }
private:
  ESP8266WebServer* server;
  LedController* lc;
};
