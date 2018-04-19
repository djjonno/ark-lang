package org.arklang.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.arklang.lang.TokenType.*;

public class Parser {

  private static class ParseError extends RuntimeException {}
  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Stmt> parse() {
    List<Stmt> expressions = new ArrayList<>();

    try {
      while (!isAtEnd()) {
        expressions.add(declaration());
      }
    } catch (ParseError e) {
      return null;
    }

    return expressions;
  }

  private Stmt declaration() {
    if (match(LET)) return varDeclaration();

    return statement();
  }

  private Stmt statement() {
    if (match(IF)) return ifStatement();
    if (match(WHILE)) return whileStatement();
    if (match(PRINT)) return printStatement();
    if (match(BREAK)) return new Stmt.Break(previous());
    if (match(LEFT_BRACE)) return new Stmt.Block(block());

    return expressionStmt();
  }

  private Stmt.Expression expressionStmt() {
    return new Stmt.Expression(expression());
  }

  private Expr expression() {
    // Look-ahead to lambda
    if (check(LEFT_PAREN) && checkNext(LAMBDA)) {
      return lambdaExpr();
    }

    return assignment();
  }

  private Expr lambdaExpr() {
    if (match(LEFT_PAREN) && match(LAMBDA)) {
      Expr expr = lambda();
      consume(RIGHT_PAREN, "Expect ')' after lambda expression.");
      return expr;
    }

    return primary();
  }

  private Expr assignment() {
    Expr expr = ternary();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = expression();
      if (expr instanceof Expr.Variable) {
        return new Expr.Assign(((Expr.Variable) expr).name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expr ternary() {
    Expr expr = grouping();

    if (match(QUESTION_MARK)) {
      Expr expr1 = expression();
      consume(COLON, "Expect ':' for ternary expression.");
      Expr expr2 = expression();
      return new Expr.Ternary(expr, expr1, expr2);
    }

    return expr;
  }

  private Expr grouping() {
    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after grouping.");
      return expr;
    }
    return binary();
  }

  private Expr binary() {
    while (match(OR, AND, BANG_EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL,
        LESS, LESS_EQUAL, MINUS, PLUS, SLASH, STAR, STAR_STAR, PERCENT,
        AMPERSAND, CARET, LEFT_SHIFT, RIGHT_SHIFT, U_RIGHT_SHIFT, PIPE)) {
      Token operator = previous();
      Expr left = expression();
      Expr right = expression();
      return new Expr.Binary(operator, left, right);
    }
    return unary();
  }

  private Expr unary() {
    if (match(BANG, MINUS, TILDE)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

    return primary();
  }

  private Expr operation() {
  return null;
  }

  private List<Expr> arguments() {
    List<Expr> arguments = new ArrayList<>();
    while (!check(RIGHT_PAREN)) {
      arguments.add(argument());
    }
    return arguments;
  }

  private Expr argument() {
    if (match(LAMBDA)) {
       return lambda();
    }
    return expression();
  }

  private Expr lambda() {
    Token name = null;
    if (match(IDENTIFIER)) {
      name = previous();
    }

    consume(PIPE, "Expect '|' after lambda declaration.");

    List<Token> parameters = new ArrayList<>();
    if (!check(RIGHT_ARROW)) {
      do {
        parameters.add(consume(IDENTIFIER, "Expect parameter name."));
      } while (match(COMMA));
    }

    consume(RIGHT_ARROW, "Expect '->' after lambda params.");

    if (check(LEFT_BRACE)) {
      return new Expr.Lambda(name, parameters, block());
    } else {
      // Arrow Lambdas have only a single grouping which is the sent value.
      // Package the grouping in a block with a send stmt.
      List<Stmt> block = Arrays.asList(new Stmt.Send(name, expression()));
      return new Expr.Lambda(name, parameters, block);
    }

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

    throw error(peek(), "Expect grouping.");
  }

  private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    return new Stmt.Let(name, initializer);
  }

  /*
  Statement Functions
   */
  private Stmt ifStatement() {
    Expr condition = expression();
    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }
    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private Stmt whileStatement() {
    Expr condition = expression();
    Stmt block = statement();
    return new Stmt.While(condition, block);
  }

  private Stmt printStatement() {
    Expr expr = expression();
    return new Stmt.Print(expr);
  }

  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(RIGHT_BRACE)) {
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expect '}' after block.");
    return statements;
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

  private Token peekNext() {
    return tokens.get(current + 1);
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

  private boolean checkNext(TokenType tokenType) {
    if (isAtEnd()) return false;
    return peekNext().type == tokenType;
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
}
