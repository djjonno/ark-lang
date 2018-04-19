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
    return new Stmt.Expression(assignment());
  }

  private Expr assignment() {
    Expr expr = ternary();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();
      if (expr instanceof Expr.Variable) {
        return new Expr.Assign(((Expr.Variable) expr).name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expr ternary() {
    Expr expr = expression();

    if (match(QUESTION_MARK)) {
      Expr expr1 = expression();
      consume(COLON, "Expect ':' for ternary expression.");
      Expr expr2 = expression();
      return new Expr.Ternary(expr, expr1, expr2);
    }

    return expr;
  }

  private Expr expression() {
    while (match(LEFT_PAREN)) {
      Expr expr = binary();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return expr;
    }
    return unary();
  }

  private Expr binary() {
    while (match(OR, AND, BANG_EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL,
        LESS, LESS_EQUAL, MINUS, PLUS, SLASH, STAR, STAR_STAR, PERCENT,
        AMPERSAND, CARET, LEFT_SHIFT, RIGHT_SHIFT, U_RIGHT_SHIFT, PIPE)) {
      Token operator = previous();
      Expr left = assignment();
      Expr right = assignment();
      return new Expr.Binary(operator, left, right);
    }
    return operation();
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
    if (match(LEFT_PAREN) && match(LAMBDA)) {
      Token token = previous();
      Expr expr = lambda();
      consume(RIGHT_PAREN, "Expect ')' after lambda declaration.");
      return new Expr.Operation(token, expr, arguments());
    }

    if (match(IDENTIFIER)) {
      Token token = previous();
      return new Expr.Operation(token, new Expr.Variable(token), arguments());
    }

    return primary();
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
    return assignment();
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
      // Arrow Lambdas have only a single expression which is the sent value.
      // Package the expression in a block with a send stmt.
      List<Stmt> block = Arrays.asList(new Stmt.Send(name, assignment()));
      return new Expr.Lambda(null, parameters, block);
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

    throw error(peek(), "Expect expression.");
  }

  private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = assignment();
    }

    return new Stmt.Let(name, initializer);
  }

  /*
  Statement Functions
   */
  private Stmt ifStatement() {
    Expr condition = assignment();
    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }
    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private Stmt whileStatement() {
    Expr condition = assignment();
    Stmt block = statement();
    return new Stmt.While(condition, block);
  }

  private Stmt printStatement() {
    Expr expr = assignment();
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
