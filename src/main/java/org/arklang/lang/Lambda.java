package org.arklang.lang;

import java.util.List;

public class Lambda implements ArkCallable {

  private final Expr.Lambda declaration;
  private final Environment closure;

  public Lambda(Expr.Lambda declaration, Environment closure) {
    this.declaration = declaration;
    this.closure = closure;
  }

  @Override
  public int arity() {
    return declaration.parameters.size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment env = new Environment(closure);
    if (declaration.parameters != null) {
      for (int i = 0; i < declaration.parameters.size(); ++i) {
        env.define(declaration.parameters.get(i).lexeme, arguments.get(i));
      }
    }

    try {
      interpreter.executeBlock(declaration.body.statements, env);
    } catch (SendJump send) {
      return send.value;
    }

    return null;
  }
}
