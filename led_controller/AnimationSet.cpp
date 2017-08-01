#include "Animation.h"
#include "AnimationSet.h"
#include "Color.h"
#include <Arduino.h>

AnimationSet::AnimationSet(Animation** set, int size) {
  this->items = new Animation*[size];
  this->size = size;
  Animation* a1;
  Animation* a2;
  for (int i = 0; i < size; i++) {
    if (i == 0) {
      a1 = set[size - 1];
    } else {
      a1 = set[i - 1];
    }
    Animation* a2 = set[i];
    this->items[i] = new Animation(a1->getR(), a1->getG(), a1->getB(), a2->getLoopTime(), new Color(a2->getR(), a2->getG(), a2->getB()));
  }
}

AnimationSet::~AnimationSet(void) {
  for (int i = 0; i < this->size; i++) {
    delete this->items[i];
  }
  delete[] this->items;
}

int AnimationSet::getSize() const {
  return size;
}
Animation** AnimationSet::getItems() {
  return items;
}
