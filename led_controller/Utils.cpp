#include "Utils.h"

namespace conversion {
  int strtoi(String s) {
    if (s == NULL || s.length() == 0) {
      return 0;
    }
    char arr[6];
    s.toCharArray(arr, sizeof(arr));
    return atoi(arr);
  }
}
