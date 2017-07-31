class Color;

class Animation {
public:
  Animation(int r, int g, int b, int loopTime);
  Animation(int r, int g, int b, int loopTime, Color* target);
  int getR() const;
  int getG() const;
  int getB() const;
  int getLoopTime() const;
  int getType() const;
  Color* getNextColor();
  bool operator==(Color const& c) const;
private:
  int r;
  int g;
  int b;
  int loopTime;
  int currentState;
  int type;
  void init(int r, int g, int b, int loopTime, Color* target);
  Color* currentColor;
  Color* targetColor;
  Color* getNextColorTypeWheel();
  Color* getNextColorTypeC2C();
};
