package org.arklang.lang;

public interface ArkIndexable {

  Object get(Token token, Object index);
  Object set(Token token, Object index, Object value);
  int length();
  default int indexToInteger(Token token, Object index) {
    if (index instanceof Integer) {
      return (int) index;
    }
    throw new RuntimeError(token, "Array index must be an integer.");
  }

}
