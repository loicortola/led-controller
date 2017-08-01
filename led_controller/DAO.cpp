#include <EEPROM.h>
#include <Arduino.h>
#include "DAO.h"
#include "Color.h"
#include "AnimationSet.h"
#include "Animation.h"
#define EEPROM_SSID 0 // Max Length 32 bytes
#define EEPROM_KEY 32 // Max Length 64 bytes
#define EEPROM_PASSWORD 96 // Max Length 32 bytes
#define EEPROM_MODE 128
#define EEPROM_STATE 129
#define EEPROM_R 130
#define EEPROM_G 131
#define EEPROM_B 132
#define EEPROM_LOOPTIME_100MS 133
#define EEPROM_ANIMATIONSET 134 // S | R | G | B | T | R | G | B | T ...

DAO::DAO() {
  EEPROM.begin(192);
}

void DAO::clear() {
  for (int i = 0 ; i < 192 ; i++) {
    EEPROM.write(i, 0);
  }
  EEPROM.commit();
}

String DAO::getSSID() {
  return readString(EEPROM_SSID);
}
void DAO::storeSSID(String ssid) {
  storeString(EEPROM_SSID, ssid, min(32,ssid.length()));
  EEPROM.commit();
}
String DAO::getKey() {
  return readString(EEPROM_KEY);
}
void DAO::storeKey(String key) {
  storeString(EEPROM_KEY, key, min(64,key.length()));
  EEPROM.commit();
}
String DAO::getPassword() {
  return readString(EEPROM_PASSWORD);
}
void DAO::storePassword(String password) {
  storeString(EEPROM_PASSWORD, password, min(32,password.length()));
  EEPROM.commit();
}
int DAO::getMode() {
  return readByte(EEPROM_MODE);
}
void DAO::storeMode(int mode) {
  storeByte(EEPROM_MODE, mode);
  EEPROM.commit();
}
bool DAO::getState(int bit) {
  return (readByte(EEPROM_STATE) & bit) == bit;
}

void DAO::storeState(int bit, bool shouldSet) {
  int currentState = readByte(EEPROM_STATE);
  if (shouldSet) {
    // Should set
    storeByte(EEPROM_STATE, currentState | bit);
  } else {
    // Should unset
    storeByte(EEPROM_STATE, currentState & (255 - bit));
  }
  EEPROM.commit();
}

Color* DAO::getColor() {
  return new Color(readByte(EEPROM_R), readByte(EEPROM_G), readByte(EEPROM_B));
}
void DAO::storeColor(Color* color) {
  storeByte(EEPROM_R, color->getR());
  storeByte(EEPROM_G, color->getG());
  storeByte(EEPROM_B, color->getB());
  EEPROM.commit();
}
int DAO::getLoopTime() {
  return readByte(EEPROM_LOOPTIME_100MS) * 100;
}
void DAO::storeLoopTime(int loopTimeMs) {
  storeByte(EEPROM_LOOPTIME_100MS, loopTimeMs / 100);
  EEPROM.commit();
}
AnimationSet* DAO::getAnimationSet() {
  int size = readByte(EEPROM_ANIMATIONSET);
  Animation* as[size];
  for(int i = 0; i < size; i++) {
    as[i] = new Animation(readByte(EEPROM_ANIMATIONSET + 1 + 4 * i),
                          readByte(EEPROM_ANIMATIONSET + 2 + 4 * i),
                          readByte(EEPROM_ANIMATIONSET + 3 + 4 * i),
                          readByte(EEPROM_ANIMATIONSET + 4 + 4 * i) * 100);
  }
  AnimationSet* result = new AnimationSet(as, size);
  for (int i = 0; i < size; i++) {
    delete as[i];
  }
  return result;
}
void DAO::storeAnimationSet(AnimationSet* as) {
  // Store size in first byte.
  storeByte(EEPROM_ANIMATIONSET, as->getSize());
  for (int i = 0; i < as->getSize(); i++) {
    storeByte(EEPROM_ANIMATIONSET + 1 + 4 * i, as->getItems()[i]->getR());
    storeByte(EEPROM_ANIMATIONSET + 2 + 4 * i, as->getItems()[i]->getG());
    storeByte(EEPROM_ANIMATIONSET + 3 + 4 * i, as->getItems()[i]->getB());
    storeByte(EEPROM_ANIMATIONSET + 4 + 4 * i, as->getItems()[i]->getLoopTime() / 100);
  }
  EEPROM.commit();
}

String DAO::readString(int offset) {
  String result;
  int i = 0;
  char c = char(EEPROM.read(offset));
  while(c != '\0') {
    result += c;
    i++;
    c = char(EEPROM.read(offset + i));
  }
  return result;
}
void DAO::storeString(int offset, String content, int length) {
  for (int i = 0; i < length; i++) {
    EEPROM.write(offset + i, content[i]);
  }
  EEPROM.write(offset + length, '\0');
}
int DAO::readByte(int position) {
  return EEPROM.read(position);
}
void DAO::storeByte(int position, int value) {
  EEPROM.write(position, value);
}
