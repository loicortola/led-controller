class Print;

class Color {
public:
  Color(int r, int g, int b);
  int getR() const;
  int getG() const;
  int getB() const;
  Color* withR(int r);
  Color* withG(int g);
  Color* withB(int b);
  Color* clone();
  void printTo(Print& dest);
  bool operator ==(Color const& c) const;
private:
  int r;
  int g;
  int b;

};
