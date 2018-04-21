package org.arklang.lang;

import java.util.List;

public class NativeFunctions {
  public static void define(Environment env) {
    env.define("out", out);
    env.define("random", random);
    env.define("stime", stime);
    env.define("len", len);
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
      arguments.stream().map(Object::toString);
      for (Object obj : arguments) {
        if (obj != null) System.out.print(obj.toString() + " ");
      }
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

  private final static ArkCallable len = new ArkCallable() {
    @Override
    public int arity() {
      return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
      Object item = arguments.get(0);
      if (item instanceof ArkIndexable) {
        return ((ArkIndexable) item).length();
      }

      return null;
    }
  };
}
