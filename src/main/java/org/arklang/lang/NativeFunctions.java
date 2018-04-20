package org.arklang.lang;

import java.util.List;

public class NativeFunctions {
  public static void define(Environment env) {
    env.define("random", random);
  }

  private final static ArkCallable random = new ArkCallable() {
    @Override
    public int arity() {
      return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
      return Math.random();
    }
  };
}
