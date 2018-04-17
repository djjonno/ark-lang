package org.arklang.lang;

import java.util.ArrayList;
import java.util.List;

import static org.arklang.lang.TokenType.*;

public class Parser {

  private static class ParseError extends RuntimeException {}
  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Stmt.Expression> parse() {
    List<Stmt.Expression> expressions = new ArrayList<>();

    while (!isAtEnd()) {
      expressions.add(new Stmt.Expression(expression()));
    }

    return expressions;
  }

  private Expr expression() {
    while (match(LEFT_PAREN)) {
      Expr expr = or();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return expr;
    }
    return unary(); // unary
  }

  private Expr or() {
    /*
    "or" | "and" | "!=" | "==" | ">" | ">=" | "<" | "<=" | "-" | "+" | "/" | "*" | "**" | "%"
     */
    while (match(OR, AND, BANG_EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL,
        LESS, LESS_EQUAL, MINUS, PLUS, SLASH, STAR, STAR_STAR, PERCENT)) {
      Token operator = previous();
      Expr left = expression();
      Expr right = expression();
      return new Expr.Binary(operator, left, right);
    }
    return unary();
  }

  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

    return primary();
  }

  private Expr primary() {
    if (match(FALSE)) return new Expr.Literal(false);
    if (match(TRUE)) return new Expr.Literal(true);
    if (match(NIL)) return new Expr.Literal(null);

    if (match(INT, DOUBLE, STRING, CHAR)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    throw error(peek(), "Expect expression.");

  }

  /*
  Helper methods for Parser.
   */

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private boolean check(TokenType tokenType) {
    if (isAtEnd()) return false;
    return peek().type == tokenType;
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }

  private ParseError error(Token token, String message) {
    Ark.error(token, message);
    return new ParseError();
  }

  // TODO: Add panic mode - synchronise
}
