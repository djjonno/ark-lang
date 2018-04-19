package org.arklang.lang;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitOperationExpr(Operation expr);
    R visitBinaryExpr(Binary expr);
    R visitUnaryExpr(Unary expr);
    R visitLiteralExpr(Literal expr);
    R visitVariableExpr(Variable expr);
    R visitTernaryExpr(Ternary expr);
    R visitLambdaExpr(Lambda expr);
  }
  static class Assign extends Expr {
    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    final Token name;
    final Expr value;
  }
  static class Operation extends Expr {
    Operation(Token token, Expr target, List<Expr> arguments) {
      this.token = token;
      this.target = target;
      this.arguments = arguments;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitOperationExpr(this);
    }

    final Token token;
    final Expr target;
    final List<Expr> arguments;
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
  static class Ternary extends Expr {
    Ternary(Expr condition, Expr expr1, Expr expr2) {
      this.condition = condition;
      this.expr1 = expr1;
      this.expr2 = expr2;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitTernaryExpr(this);
    }

    final Expr condition;
    final Expr expr1;
    final Expr expr2;
  }
  static class Lambda extends Expr {
    Lambda(Token name, List<Token> parameters, List<Stmt> body) {
      this.name = name;
      this.parameters = parameters;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLambdaExpr(this);
    }

    final Token name;
    final List<Token> parameters;
    final List<Stmt> body;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
