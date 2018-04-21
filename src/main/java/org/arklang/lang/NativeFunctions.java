package org.arklang.lang;

import java.util.List;

public class NativeFunctions {
  public static void define(Environment env) {
    env.define("out", out);
    env.define("random", random);
    env.define("stime", stime);
  }

  /**
   * Output something to stdout
   */
  private final static ArkCallable out = new ArkCallable() {
    @Override
    public int arity() {
      return 0;
    }

    @Override
    public boolean variadic() {
      return true;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
      for (Object o : arguments) System.out.print(o.toString());
      System.out.println();
      return null;
    }
  };

  /**
   * Generate a uniform random number.
   */
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

  /**
   * Retrieve the system time
   */
  private final static ArkCallable stime = new ArkCallable() {
    @Override
    public int arity() {
      return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
      return (double)System.currentTimeMillis() / 1000.0;
    }
  };
}
