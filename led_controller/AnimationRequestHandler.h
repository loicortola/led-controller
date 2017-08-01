#include <ESP8266WebServer.h>
#include "AnimationSet.h"

class LedController;


class AnimationRequestHandler {
public:
  AnimationRequestHandler(ESP8266WebServer& refServer, LedController& lc) {
    this->server = &refServer;
    this->lc = &lc;
  }
  void postHandler() {
    int type = 1;
    if (server->hasArg("type")) {
      type = server->arg("type").toInt();
    }
    if (type == 1) {
      handleWheelAnimation();
    } else if (type == 2) {
      handleC2CAnimation();
    }
  }
private:
  ESP8266WebServer* server;
  LedController* lc;
  void handleWheelAnimation() {
    int red = server->arg("red").toInt();
    int green = server->arg("green").toInt();
    int blue = server->arg("blue").toInt();
    int loopTime = server->arg("looptime").toInt();
    if (red + green + blue > 0) {
      lc->animate(new Animation(red, green, blue, loopTime));
      server->send(200, "application/json charset=UTF-8;", "{\"message\": \"Success\"}");
    } else {
      server->send(400, "application/json charset=UTF-8;", "{\"message\": \"Invalid Parameters. Please supply the following query parameters: red, green, and blue between 0 and 255, looptime between 4000 and 20000.\"}");
    }
  }
  void handleC2CAnimation() {
    Animation* animationSet[MAX_ANIMATIONS_IN_SET];
    int c = 0;
    for(int i = 0; i < server->args();i++) {
      String argName = server->argName(i);
      if (argName == "step") {
        if (c < MAX_ANIMATIONS_IN_SET) {
          Animation* a = parseStep(server->arg(i));
          if (a != NULL) {
            animationSet[c++] = a;
          }
        }
      }
    }
    if (c < 2) {
      server->send(400, "application/json charset=UTF-8;", "{\"message\": \"Invalid Parameters. Please supply a list of at least 2 'step' arguments. Example: yourUrl?step=255,0,0,4000&step=0,255,0,4000 . Values are R,G,B,durationInMs.\"}");
    }
    lc->animateSet(new AnimationSet(animationSet, c));
    for (int i = 0; i < c; i++) {
      if (animationSet[i] != NULL) {
        delete animationSet[i];
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
