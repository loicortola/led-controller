#include <ESP8266Webserver.h>
#include "Utils.h"

class LedController;

using namespace conversion;

class ColorRequestHandler {
public:
  ColorRequestHandler(ESP8266WebServer& refServer, LedController& lc) {
    this->server = &refServer;
    this->lc = &lc;
  }
  void postHandler() {
    int red = 0;
    int green = 0;
    int blue = 0;
    bool isSwiping = false;
    for(int i = 0; i < server->args();i++) {
      String argName = server->argName(i);
      if (argName == "red") {
        red = strtoi(server->arg(i));
      } else if (argName == "green") {
          green = strtoi(server->arg(i));
      } else if (argName == "blue") {
          blue = strtoi(server->arg(i));
      } else if (argName == "swiping") {
          isSwiping = true;
      }
    }
    if (red + green + blue > 0) {
      if (isSwiping) {
        lc->changeColor(new Color(red, green, blue));
      } else {
        lc->setColor(new Color(red, green, blue));
      }
      server->send(200, "application/json charset=UTF-8;", "{\"message\": \"Success\"}");
    } else {
      server->send(400, "application/json charset=UTF-8;", "{\"message\": \"Invalid Parameters. Please supply the following query parameters: red, green, and blue between 0 and 255.\"}");
    }
  }
private:
  ESP8266WebServer* server;
  LedController* lc;
};
