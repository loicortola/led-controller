#include <Arduino.h>
#include "LedController.h"
#include "Animation.h"
#include "Color.h"
#include "DAO.h"
extern "C" {
#include "user_interface.h"
}

#define BLINK_THRESHOLD 500

/**
 * The Led Controller performs all led operations
 */
LedController::LedController(DAO* dao) {
  this->dao = dao;
}

void onTick(void* instance) {
  ((LedController *) instance)->onAnimationTick();
}

void LedController::begin() {
  // Initialize timer
  os_timer_setfn(&(this->timer), onTick , this);

  // Initialize color
  this->changeColor(new Color(0, 0, 0));
}

void LedController::update() {
}

void LedController::stop() {
  this->blinking = false;
  this->changeColor(new Color(0,0,0));
  os_timer_disarm(&(this->timer));
}

void LedController::loadDefaults() {
    if (!this->isSwitchedOn()) {
      this->changeColor(new Color(0,0,0));
      return;
    }
    switchOn();
}

void LedController::toggle() {
  if (isSwitchedOn()) {
    switchOff();
  } else {
    switchOn();
  }
}

void LedController::switchOn() {
  dao->storeState(STATE_SWITCH, true);
  int mode = this->getMode();
  if (mode == 0) {
    if (currentBlink != NULL) {
      // Blink mode
      this->changeColor(this->currentBlink);
    }
  } else if (mode == 1) {
    // Plain Color mode
    this->changeColor(this->getColor());
  } else if (mode == 2) {
    // Animate mode
    this->startAnimation(this->getAnimation());
  }
}

void LedController::switchOff() {
  if (this->getMode() != 0) {
    // SwitchOff is used internally with blink mode
    this->stopAnimation();
  }
  this->changeColor(new Color(0,0,0));
  dao->storeState(STATE_SWITCH, false);
}

bool LedController::isSwitchedOn() {
  return dao->getState(STATE_SWITCH);
}

void LedController::blink(Color* c) {
  // Set blinking mode
  setMode(0);
  blinking = true;
  currentBlink = c;
  changeColor(c);
  startBlinking(c);
}

int LedController::getMode() {
  return dao->getMode();
}

void LedController::setMode(int mode) {
  dao->storeMode(mode);
}

Color* LedController::getColor() {
  return dao->getColor();
}

void LedController::setColor(Color* c) {
  this->stopAnimation();
  dao->storeColor(c);
  this->setMode(1);
  this->switchOn();
}

void LedController::changeColor(Color* c) {
  analogWrite(LED_R_PIN, min(1023, 4 * c->getR()));
  analogWrite(LED_G_PIN, min(1023,  4 * c->getG()));
  analogWrite(LED_B_PIN, min(1023,  4 * c->getB()));
}

void LedController::stopAnimation() {
  os_timer_disarm(&(this->timer));
}

void LedController::startAnimation(Animation* a) {
  // Max Ops = 255*6 = 1.5k. But increment of 2, so half.
  // Min looptime = 4s
  int ops = a->getR() + a->getG() + a->getB();
  int triggerMs = a->getLoopTime() / ops;
  os_timer_arm(&(this->timer), triggerMs, true);
}

void LedController::startBlinking(Color* c) {
  // Blink every second
  os_timer_arm(&(this->timer), 1000, true);
}

Animation* LedController::getAnimation() {
  if (this->currentAnimation == NULL) {
    Color* c = dao->getColor();
    int loopTime = dao->getLoopTime();
    this->currentAnimation = new Animation(c->getR(), c->getG(), c->getB(), loopTime);
  }
  return currentAnimation;
}

void LedController::onAnimationTick() {
  if (blinking) {
    this->toggle();
  } else {
    this->changeColor(this->currentAnimation->getNextColor());
  }
}

void LedController::animate(Animation* a) {
  this->stopAnimation();
  if (this->currentAnimation != NULL) {
    delete this->currentAnimation;
  }
  this->currentAnimation = a;
  dao->storeColor(new Color(a->getR(), a->getG(), a->getB()));
  dao->storeLoopTime(a->getLoopTime());
  this->setMode(2);
  this->switchOn();
  this->startAnimation(a);
}
