
class Color;
class Print;
class Animation {
public:
  ~Animation(void);
  Animation(int r, int g, int b, int loopTime);
  Animation(int r, int g, int b, int loopTime, Color* target);
  int getR() const;
  int getG() const;
  int getB() const;
  int getLoopTime() const;
  int getType() const;
  Color* getNextColor();
  Color* getTargetColor();
  bool operator==(Color const& c) const;
  void printTo(Print& dest);
  /**
   * AnimationSet only
   */
  bool isFinished();
  void reset();
  Animation* clone();
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
