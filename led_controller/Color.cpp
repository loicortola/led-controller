#include <Arduino.h>
#include "Color.h"

Color::Color(int r, int g, int b) {
  this->r = min(255, max(0, r));
  this->g = min(255, max(0, g));
  this->b = min(255, max(0, b));
}

int Color::getR() const {
  return this->r;
}

int Color::getG() const {
  return this->g;
}

int Color::getB() const {
  return this->b;
}

Color* Color::withR(int r) {
  return new Color(min(255,max(0,r)), this->g, this->b);
}

Color* Color::withG(int g) {
  return new Color(this->r, min(255,max(0,g)), this->b);
}

Color* Color::withB(int b) {
  return new Color(this->r, this->g, min(255,max(0,b)));
}
