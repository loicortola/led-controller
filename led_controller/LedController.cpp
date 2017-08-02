#include <Arduino.h>
#include "LedController.h"
#include "Animation.h"
#include "AnimationSet.h"
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
  this->currentAnimationStep = -1;
}

void onTick(void* instance) {
  ((LedController *) instance)->onAnimationTick();
}

void LedController::begin() {
  // Initialize timer
  os_timer_setfn(&(this->timer), onTick , this);

  // Initialize color
  Color* black = new Color(0, 0, 0);
  this->changeColor(black);
  delete black;
}

void LedController::update() {
}

void LedController::stop() {
  this->blinking = false;
  switchOff();
  os_timer_disarm(&(this->timer));
}

void LedController::loadDefaults() {
    if (!this->isSwitchedOn()) {
      switchOff();
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
    Color *c = this->getColor();
    this->changeColor(c);
    delete c;
  } else if (mode == 2) {
    // Animate mode
    this->startAnimation(this->getAnimation());
  } else if (mode == 3) {
    // AnimateSet mode
    this->startAnimationSet(this->getAnimationSet());
  }
}

void LedController::switchOff() {
  if (this->getMode() != 0) {
    // SwitchOff is used internally with blink mode
    stopAnimation();
    clearAnimations();
  }
  Color* black = new Color(0,0,0);
  this->changeColor(black);
  delete black;
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

void LedController::startAnimationSet(AnimationSet* as) {
  if (this->currentAnimationStep == as->getSize()) {
    this->currentAnimationStep = 0;
  }
  if (currentAnimationStep == -1) {
    Animation* firstAnim = as->getItems()[0];
    currentAnimation = new Animation(0,0,0, firstAnim->getLoopTime(), new Color(firstAnim->getR(), firstAnim->getG(), firstAnim->getB()));
  } else {
    currentAnimation = as->getItems()[currentAnimationStep]->clone();
    currentAnimation->reset();
  }
  currentAnimation->printTo(Serial);
  int ops = max(1, max(max(abs(currentAnimation->getR() - currentAnimation->getTargetColor()->getR()),
                    abs(currentAnimation->getG() - currentAnimation->getTargetColor()->getG())),
                    abs(currentAnimation->getB() - currentAnimation->getTargetColor()->getB())));
  int triggerMs = currentAnimation->getLoopTime() / ops;
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
    delete c;
  }
  return currentAnimation;
}

AnimationSet* LedController::getAnimationSet() {
  if (this->currentAnimationSet == NULL) {
    AnimationSet* as = dao->getAnimationSet();
    this->currentAnimationSet = as;
  }
  return currentAnimationSet;
}

void LedController::onAnimationTick() {
  if (blinking) {
    this->toggle();
  } else {
    if (this->currentAnimation->isFinished()) {
      this->stopAnimation();
      delete this->currentAnimation;
      this->currentAnimation = NULL;
      this->currentAnimationStep++;
      this->startAnimationSet(this->currentAnimationSet);
    } else {
      Color* c = this->currentAnimation->getNextColor();
      this->changeColor(c);
    }
  }
}

void LedController::clearAnimations() {
  delete this->currentAnimationSet;
  delete this->currentAnimation;
  this->currentAnimationStep = -1;
  this->currentAnimation = NULL;
  this->currentAnimationSet = NULL;
}

void LedController::animate(Animation* a) {
  this->stopAnimation();
  this->clearAnimations();
  this->currentAnimation = a;
  Color* initialColor = new Color(a->getR(), a->getG(), a->getB());
  dao->storeColor(initialColor);
  delete initialColor;
  dao->storeLoopTime(a->getLoopTime());
  this->setMode(2);
  this->switchOn();
}


void LedController::animateSet(AnimationSet* as) {
  this->stopAnimation();
  this->clearAnimations();
  this->currentAnimationSet = as;
  dao->storeAnimationSet(as);
  this->setMode(3);
  yield();
  this->switchOn();
}
