package org.arklang.lang;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitExpressionStmt(Expression stmt);
    R visitIfStmt(If stmt);
    R visitWhileStmt(While stmt);
    R visitForInStmt(ForIn stmt);
    R visitPrintStmt(Print stmt);
    R visitSendStmt(Send stmt);
    R visitLetStmt(Let stmt);
    R visitBreakStmt(Break stmt);
  }
  static class Block extends Stmt {
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
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
  static class If extends Stmt {
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
  static class While extends Stmt {
    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }
  static class ForIn extends Stmt {
    ForIn(Token token, Token itemIterator, Token indexIterator, Expr enumerable, Stmt body) {
      this.token = token;
      this.itemIterator = itemIterator;
      this.indexIterator = indexIterator;
      this.enumerable = enumerable;
      this.body = body;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitForInStmt(this);
    }

    final Token token;
    final Token itemIterator;
    final Token indexIterator;
    final Expr enumerable;
    final Stmt body;
  }
  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }
  static class Send extends Stmt {
    Send(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSendStmt(this);
    }

    final Token keyword;
    final Expr value;
  }
  static class Let extends Stmt {
    Let(List<Token> names, List<Expr> initializers) {
      this.names = names;
      this.initializers = initializers;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLetStmt(this);
    }

    final List<Token> names;
    final List<Expr> initializers;
  }
  static class Break extends Stmt {
    Break(Token keyword) {
      this.keyword = keyword;
    }

    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBreakStmt(this);
    }

    final Token keyword;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
