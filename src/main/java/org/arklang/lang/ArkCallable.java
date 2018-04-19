package org.arklang.lang;

import java.util.List;

public interface ArkCallable {
  int arity();
  Object call(Interpreter interpreter, List<Object> arguments);
}
