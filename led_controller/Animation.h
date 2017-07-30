class Color;

class Animation {
public:
  Animation(int r, int g, int b, int loopTime);
  int getR();
  int getG();
  int getB();
  int getLoopTime();
  Color* getNextColor();
private:
  int r;
  int g;
  int b;
  int loopTime;
  int currentState;
  Color* currentColor;
};
