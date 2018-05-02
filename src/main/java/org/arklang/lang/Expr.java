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
    R visitArrayExpr(Array expr);
    R visitStrExpr(Str expr);
    R visitCharExpr(Char expr);
    R visitIndexGetExpr(IndexGet expr);
    R visitIndexSetExpr(IndexSet expr);
    R visitRangeExpr(Range expr);
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
  static class Array extends Expr {
    Array(Token bracket, List<Expr> items) {
      this.bracket = bracket;
      this.items = items;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitArrayExpr(this);
    }

    final Token bracket;
    final List<Expr> items;
  }
  static class Str extends Expr {
    Str(Token token, String str) {
      this.token = token;
      this.str = str;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitStrExpr(this);
    }

    final Token token;
    final String str;
  }
  static class Char extends Expr {
    Char(Token token, Character c) {
      this.token = token;
      this.c = c;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCharExpr(this);
    }

    final Token token;
    final Character c;
  }
  static class IndexGet extends Expr {
    IndexGet(Expr indexee, Token token, Expr index) {
      this.indexee = indexee;
      this.token = token;
      this.index = index;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIndexGetExpr(this);
    }

    final Expr indexee;
    final Token token;
    final Expr index;
  }
  static class IndexSet extends Expr {
    IndexSet(Expr indexee, Token token, Expr index, Expr value) {
      this.indexee = indexee;
      this.token = token;
      this.index = index;
      this.value = value;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIndexSetExpr(this);
    }

    final Expr indexee;
    final Token token;
    final Expr index;
    final Expr value;
  }
  static class Range extends Expr {
    Range(Expr lower, Expr upper, Token token, boolean closed) {
      this.lower = lower;
      this.upper = upper;
      this.token = token;
      this.closed = closed;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitRangeExpr(this);
    }

    final Expr lower;
    final Expr upper;
    final Token token;
    final boolean closed;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
