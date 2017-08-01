#include <ESP8266Webserver.h>

class LedController;

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
        red = server->arg(i).toInt();
      } else if (argName == "green") {
          green = server->arg(i).toInt();
      } else if (argName == "blue") {
          blue = server->arg(i).toInt();
      } else if (argName == "swiping") {
          isSwiping = true;
      }
    }
    if (red + green + blue > 0) {
      Color* c = new Color(red, green, blue);
      if (isSwiping) {
        lc->changeColor(c);
      } else {
        lc->setColor(c);
      }
      delete c;
      server->send(200, "application/json charset=UTF-8;", "{\"message\": \"Success\"}");
    } else {
      server->send(400, "application/json charset=UTF-8;", "{\"message\": \"Invalid Parameters. Please supply the following query parameters: red, green, and blue between 0 and 255.\"}");
    }
  }
private:
  ESP8266WebServer* server;
  LedController* lc;
};
