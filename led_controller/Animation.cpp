#include <Arduino.h>
#include "Animation.h"
#include "Color.h"

Animation::~Animation(void) {
  delete this->currentColor;
  delete this->targetColor;
}

Animation::Animation(int r, int g, int b, int loopTime) {
  init(r, g, b, loopTime, NULL);
}

Animation::Animation(int r, int g, int b, int loopTime, Color* target) {
  init(r, g, b, loopTime, target);
}

void Animation::init(int r, int g, int b, int loopTime, Color* target) {
  this->r = min(255, max(0, r));
  this->g = min(255, max(0, g));
  this->b = min(255, max(0, b));
  // LoopTime is between 8 and 20s
  this->loopTime = min(20000, max(4000, loopTime));
  // Initial state is dark, matches state 0
  this->currentColor = new Color(0, 0, 0);
  this->currentState = -1;
  // Animation type
  if (target == NULL) {
    this->type = 1; // Simple color wheel animation
    this->targetColor = NULL;
  } else {
    this->type = 2; // Color to color (C2C) animation
    this->targetColor = target;
  }
}

int Animation::getR() const {
  return this->r;
}

int Animation::getG() const {
  return this->g;
}

int Animation::getB() const {
  return this->b;
}

int Animation::getLoopTime() const {
  return this->loopTime;
}


int Animation::getType() const {
  return this->type;
}

Animation* Animation::clone() {
  return new Animation(r, g, b, loopTime, targetColor->clone());
}

bool Animation::operator==(Color const& otherColor) const {
  return (this->r == otherColor.getR() && this->g == otherColor.getG() && this->b == otherColor.getB());
}

Color* Animation::getNextColor() {
  if (this->type == 1) {
    return getNextColorTypeWheel();
  } else {
    return getNextColorTypeC2C();
  }
}

Color* Animation::getTargetColor() {
  return targetColor;
}

Color* Animation::getNextColorTypeC2C() {
  int increment = 1;
  int r = 0;
  int g = 0;
  int b = 0;
  int c = 0;
  Color* oldColor = currentColor;
  // Next r
  c = this->currentColor->getR();
  if (c < this->targetColor->getR()) {
    r = c + increment;
  } else if (c > this->targetColor->getR()) {
    r = c - increment;
  }
  // Next g
  c = this->currentColor->getG();
  if (c < this->targetColor->getG()) {
    g = c + increment;
  } else if (c > this->targetColor->getG()) {
    g = c - increment;
  }
  // Next b
  c = this->currentColor->getB();
  if (c < this->targetColor->getB()) {
    b = c + increment;
  } else if (c > this->targetColor->getB()) {
    b = c - increment;
  }
  this->currentColor = new Color(r, g, b);
  delete oldColor;
  return this->currentColor;
}

bool Animation::isFinished() {
  return this->getType() == 2 && (*(this->currentColor) == *(this->targetColor));
}
void Animation::reset() {
  if (this->type != 2) {
    return;
  }
  if (this->currentColor) {
    delete this->currentColor;
  }
  this->currentColor = new Color(r, g, b);
}
void Animation::printTo(Print& dest) {
  dest.print("Animation:{r:");
  dest.print(this->r);
  dest.print(",g:");
  dest.print(this->g);
  dest.print(",b:");
  dest.print(this->b);
  dest.print(",loopTime:");
  dest.print(this->loopTime);
  if (this->currentColor != NULL) {
    dest.print(",currentColor:");
    this->currentColor->printTo(dest);
  }
  if (this->targetColor != NULL) {
    dest.print(",targetColor:");
    this->targetColor->printTo(dest);
  }
  dest.print("}");
}

Color* Animation::getNextColorTypeWheel() {
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
