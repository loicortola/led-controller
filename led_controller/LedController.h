extern "C" {
#include "user_interface.h"
}
class Color;
class Animation;
class AnimationSet;
class DAO;

#ifndef LED_R_PIN
#define LED_R_PIN 5
#endif

#ifndef LED_G_PIN
#define LED_G_PIN 4
#endif

#ifndef LED_B_PIN
#define LED_B_PIN 14
#endif

#define MAX_ANIMATIONS_IN_SET 6

/**
 * The Led Controller controls all the leds of the Beacon
 */
class LedController {
public:
  LedController(DAO* dao);
  void begin();
  void update();
  void stop();
  void loadDefaults();
  void toggle();
  void switchOn();
  void switchOff();
  bool isSwitchedOn();
  int getMode();
  void setMode(int mode);
  Color* getColor();
  void changeColor(Color* c);
  void setColor(Color* c);
  Animation* getAnimation();
  AnimationSet* getAnimationSet();
  void blink(Color* color);
  void animate(Animation* a);
  void animateSet(AnimationSet* as);
  void onAnimationTick();
  void stopAnimation();
private:
  DAO* dao;
  Animation* currentAnimation;
  AnimationSet* currentAnimationSet;
  int currentAnimationStep;
  Color* currentBlink;
  os_timer_t timer;
  bool blinking;
  void startBlinking(Color* c);
  void startAnimation(Animation* a);
  void startAnimationSet(AnimationSet* as);
  void clearAnimations();
};
