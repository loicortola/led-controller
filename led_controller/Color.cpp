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

Color* Color::clone() {
  return new Color(r, g, b);
}

bool Color::operator==(Color const& otherColor) const {
  return (this->r == otherColor.getR() && this->g == otherColor.getG() && this->b == otherColor.getB());
}

void Color::printTo(Print& dest) {
  dest.print("Color:{r:");
  dest.print(this->r);
  dest.print(",g:");
  dest.print(this->g);
  dest.print(",b:");
  dest.print(this->b);
  dest.print("}");
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
