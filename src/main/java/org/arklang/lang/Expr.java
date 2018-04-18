package org.arklang.lang;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitOperationExpr(Operation expr);
    R visitBinaryExpr(Binary expr);
    R visitUnaryExpr(Unary expr);
    R visitLiteralExpr(Literal expr);
    R visitVariableExpr(Variable expr);
  }
  static class Operation extends Expr {
    Operation(Token name, List<Expr> expressions) {
      this.name = name;
      this.expressions = expressions;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitOperationExpr(this);
    }

    final Token name;
    final List<Expr> expressions;
  }
  static class Binary extends Expr {
    Binary(Token operator, Expr left, Expr right) {
      this.operator = operator;
      this.left = left;
      this.right = right;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    final Token operator;
    final Expr left;
    final Expr right;
  }
  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    final Token operator;
    final Expr right;
  }
  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    final Object value;
  }
  static class Variable extends Expr {
    Variable(Token name) {
      this.name = name;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    final Token name;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
