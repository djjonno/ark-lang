package org.arklang.lang;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitExpressionStmt(Expression stmt);
  }
  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
