package org.arklang.lang;

import java.util.List;

public interface ArkCallable {
  int arity();
  default boolean variadic() {
    return false;
  }
  Object call(Interpreter interpreter, List<Object> arguments);
}
