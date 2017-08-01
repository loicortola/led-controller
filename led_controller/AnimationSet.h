class Animation;

class AnimationSet {
public:
  AnimationSet(Animation** set, int size);
  ~AnimationSet(void);
  int getSize() const;
  Animation** getItems();
private:
  Animation** items;
  int size;
};
