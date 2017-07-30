#include "Utils.h"

namespace conversion {
  int strtoi(String s) {
    char arr[6];
    s.toCharArray(arr, sizeof(arr));
    return atoi(arr);
  }
}
