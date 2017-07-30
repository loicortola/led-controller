class Color;

#define STATE_CONFIGURED 1
#define STATE_WIFISTATUS 2
#define STATE_SWITCH 4

class DAO {
public:
  DAO();

  String getSSID();
  void storeSSID(String ssid);
  String getKey();
  void storeKey(String key);
  String getPassword();
  void storePassword(String password);

  int getMode();
  void storeMode(int mode);
  /**
   * State of the controller.
   * Bitwise operations
   * STATE_CONFIGURED
   * STATE_WIFISTATUS
   * STATE_SWITCH
   */
  bool getState(int bit);
  void storeState(int bit, bool set);
  Color* getColor();
  void storeColor(Color* color);
  int getLoopTime();
  void storeLoopTime(int loopTimeMs);
  void clear();
private:
  String readString(int offset);
  void storeString(int offset, String content, int length);
  int readByte(int position);
  void storeByte(int position, int value);
};
