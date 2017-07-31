#include <ESP8266WebServer.h>
#include "Utils.h"

class LedController;

using namespace conversion;

class AnimationRequestHandler {
public:
  AnimationRequestHandler(ESP8266WebServer& refServer, LedController& lc) {
    this->server = &refServer;
    this->lc = &lc;
  }
  void postHandler() {
    if (!server->hasArg("type")) {
      server->send(400, "application/json charset=UTF-8;", "{\"message\": \"Invalid Parameters. Please supply an animation 'type' parameter.\"}");
      return;
    }
    int type = strtoi(server->arg("type"));
    if (type == 1) {
      handleWheelAnimation();
    } else {
      handleC2CAnimation();
    }

  }
private:
  ESP8266WebServer* server;
  LedController* lc;
  void handleWheelAnimation() {
    int red = strtoi(server->arg("red"));
    int green = strtoi(server->arg("green"));
    int blue = strtoi(server->arg("blue"));
    int loopTime = strtoi(server->arg("looptime"));
    if (red + green + blue > 0) {
      lc->animate(new Animation(red, green, blue, loopTime));
      server->send(200, "application/json charset=UTF-8;", "{\"message\": \"Success\"}");
    } else {
      server->send(400, "application/json charset=UTF-8;", "{\"message\": \"Invalid Parameters. Please supply the following query parameters: red, green, and blue between 0 and 255, looptime between 4000 and 20000.\"}");
    }
  }
  void handleC2CAnimation() {
    Animation* animationSet[6];
    int c = 0;
    for(int i = 0; i < server->args();i++) {
      String argName = server->argName(i);
      if (argName == "step") {
        if (c < 6) {
          animationSet[c++] = parseStep(server->arg(i));
        }
      }
    }
    server->send(200, "application/json charset=UTF-8;", "{\"message\": \"Success\"}");
  }
  Animation* parseStep(String step) {
    if (step == NULL || step.length() == 0) {
      return NULL;
    }
    int lastTokenIndex = 0;
    int tokenCount = 0;
    int tokens[4];
    for(int i = 0; i < step.length(); i++) {
      char c = step.charAt(i);
      if (c == ',') {
        if (tokenCount == 4) {
          continue;
        }
        String t = step.substring(lastTokenIndex, i);
        t.trim();
        tokens[tokenCount] = t.toInt();
        tokenCount++;
        lastTokenIndex = i + 1;
      }
    }
    // Last token
    if (lastTokenIndex < step.length()) {
      String t = step.substring(lastTokenIndex);
      t.trim();
      tokens[tokenCount] = t.toInt();
      tokenCount++;
    }
    if (tokenCount != 4) {
      return NULL;
    }
    return new Animation(tokens[0],tokens[1],tokens[2],tokens[3]);
  }
};
