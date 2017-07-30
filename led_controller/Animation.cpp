#include <Arduino.h>
#include "Animation.h"
#include "Color.h"

Animation::Animation(int r, int g, int b, int loopTime) {
  this->r = min(255, max(0, r));
  this->g = min(255, max(0, g));
  this->b = min(255, max(0, b));
  // LoopTime is between 8 and 20s
  this->loopTime = min(20000, max(4000, loopTime));
  // Initial state is dark, matches state 0
  this->currentColor = new Color(0, 0, 0);
  this->currentState = -1;
}

int Animation::getR() {
  return this->r;
}

int Animation::getG() {
  return this->g;
}

int Animation::getB() {
  return this->b;
}

int Animation::getLoopTime() {
  return this->loopTime;
}

Color* Animation::getNextColor() {
  int c = 0;
  int increment = 2;
  Color* oldColor = currentColor;
  switch (this->currentState) {
    case -1:
      // Step -1: From (0,0,0) to (0,0,b)
      c = this->currentColor->getB();
      if (c < this->b) {
        this->currentColor = currentColor->withB(c + increment);
        break;
      }
      this->currentState = 0;
    case 0:
      // Step 0: From (0,0,b) to (r,0,b)
      c = this->currentColor->getR();
      if (c < this->r) {
        this->currentColor = currentColor->withR(c + increment);
        break;
      }
      this->currentState = 1;
    case 1:
      // Step 1: From (r,0,b) to (r,0,0)
      c = this->currentColor->getB();
      if (c > 0) {
        this->currentColor = currentColor->withB(c - increment);
        break;
      }
      this->currentState = 2;
    case 2:
      // Step 2: From (r,0,0) to (r,g,0)
      c = this->currentColor->getG();
      if (c < this->getG()) {
        this->currentColor = currentColor->withG(c + increment);
        break;
      }
      this->currentState = 3;
    case 3:
      // Step 3: From (r,g,0) to (0,g,0)
      c = this->currentColor->getR();
      if (c > 0) {
        this->currentColor = currentColor->withR(c - increment);
        break;
      }
      this->currentState = 4;
    case 4:
      // Step 4: From (0,g,0) to (0,g,b)
      c = this->currentColor->getB();
      if (c < this->b) {
        this->currentColor = currentColor->withB(c + increment);
        break;
      }
      this->currentState = 5;
    case 5:
      // Step 5: From (0,g,b) to (0,0,b)
      c = this->currentColor->getG();
      if (c > 0) {
        this->currentColor = currentColor->withG(c - increment);
        break;
      }
      this->currentState = 0;
  }
  if (oldColor != currentColor) {
    delete oldColor;
  }
  return this->currentColor;
}

