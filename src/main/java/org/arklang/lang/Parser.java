package org.arklang.lang;

import com.sun.org.apache.bcel.internal.generic.RETURN;

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
    if (match(LET)) return letDeclaration();
    if (match(FOR)) return forDeclaration();

    return statement();
  }

  private Stmt statement() {
    if (match(IF)) return ifStatement();
    if (match(WHILE)) return whileStatement();
    if (match(PRINT)) return printStatement();
    if (match(SEND)) return new Stmt.Send(previous(), expression());
    if (match(BREAK)) return new Stmt.Break(previous());
    if (match(LBRACE)) return new Stmt.Block(block());

    return expressionStmt();
  }

  private Stmt.Expression expressionStmt() {
    return new Stmt.Expression(expression());
  }

  private Expr expression() {
    return assignment();
  }

  private Expr assignment() {
    Expr expr = ternary();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = expression();
      if (expr instanceof Expr.Variable) {
        return new Expr.Assign(((Expr.Variable) expr).name, value);
      } else if (expr instanceof Expr.IndexGet) {
        Expr.IndexGet get = (Expr.IndexGet) expr;
        return new Expr.IndexSet(get.indexee, get.token, get.index, value);
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
    if (match(LPAREN)) {
      Expr expr = expression();
      consume(RPAREN, "Expect ')' after grouping.");
      return expr;
    }

    // Binary operations are only valid within group expressions
    Token prev = previous();
    if (prev != null && prev.type == LPAREN) {
      return binary();
    } else {
      return unary();
    }
  }

  private Expr binary() {

    if (match(OR, AND, BANG_EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL,
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

    return operation();
  }

  private Expr operation() {
    if (match(LAMBDA)) {
      Token token = previous();
      Expr.Lambda expr = lambda();
      /*
      If Lambda has no name and arguments, it should also be parsed
      as an operation because there would be no opportunity in future to execute.
       */
      if ((expr.name == null && expr.parameters.size() == 0) || !check(RPAREN)) {
        // this is an operation. Parse arguments.
        return new Expr.Operation(expr.name != null ? expr.name : token, expr, arguments());
      } else {
        return expr;
      }
    }

    Token prev = previous();
    Expr expr = primary();
    if (prev != null && expr instanceof Expr.Variable && prev.type == LPAREN) {
      return new Expr.Operation(((Expr.Variable) expr).name, expr, arguments());
    } else if (match(LBRACKET)) {
      expr = new Expr.IndexGet(expr, previous(), expression());
      consume(RBRACKET, "Expect ']' after indexing operation.");
    }
    return expr;
  }

  private List<Expr> arguments() {
    List<Expr> arguments = new ArrayList<>();
    while (!check(RPAREN)) {
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

  private Expr.Lambda lambda() {
    Token name = null;
    if (match(IDENTIFIER)) {
      name = previous();
    }

    consume(COLON, "Expect ':' after lambda declaration.");

    List<Token> parameters = new ArrayList<>();
    if (check(IDENTIFIER)) {
      do {
        parameters.add(consume(IDENTIFIER, "Expect parameter name."));
      } while (!check(RIGHT_ARROW));
    }

    consume(RIGHT_ARROW, "Expect '->' after lambda params.");

    if (check(LBRACE)) {
      match(LBRACE);
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

    if (match(INT)) {
      // Check for range operator
      Expr value = new Expr.Literal(previous().literal);
      if (match(DOT_DOT, DOT_DOT_DOT)) {
        boolean closed = previous().type == DOT_DOT_DOT;
        return new Expr.Range(value, expression(), previous(),
            closed);
      } else {
        return value;
      }
    }

    if (match(DOUBLE, CHAR)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(STRING)) {
      return string();
    }

    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    if (match(LBRACKET)) {
      return array();
    }

    throw error(peek(), "Expect expression.");
  }

  private Expr array() {
    Token bracket = previous();

    List<Expr> items = new ArrayList<>();
    while (!match(RBRACKET)) {
      do {
        items.add(expression());
      } while (match(COMMA));
    }

    return new Expr.Array(bracket, items);
  }

  private Expr string() {
    /* Inspect string for interpolation operations */
    String literal = (String) previous().literal;

    // TODO: Implement string interpolation

    return new Expr.Literal(literal);
  }

  private Stmt letDeclaration() {
    List<Token> names = new ArrayList<>();
    List<Expr> initializers = new ArrayList<>();

    do {
      Token name = consume(IDENTIFIER, "Expect variable name.");
      Expr initializer = null;
      if (match(EQUAL)) {
        initializer = expression();
      }
      names.add(name);
      initializers.add(initializer);
    } while (match(COMMA));

    return new Stmt.Let(names, initializers);
  }

  private Stmt forDeclaration() {
    Token token = previous();

    consume(IDENTIFIER, "Expect iterator after 'for' declaration.");
    Token itemIterator = previous(),
        indexIterator = null;

    if (check(COMMA)) {
      match(COMMA);
      consume(IDENTIFIER, "Expect index iterator in 'for' declaration.");
      indexIterator = previous();
    }

    consume(IN, "Expect 'in' after index declaration.");

    Expr enumerator = expression();
    Stmt body = statement();
    Stmt.ForIn forIn = new Stmt.ForIn(token, itemIterator, indexIterator, enumerator, body);
    return new Stmt.Block(new ArrayList<Stmt>() {{ add(forIn); }});
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

    while (!check(RBRACE)) {
      statements.add(declaration());
    }

    consume(RBRACE, "Expect '}' after block.");
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
    if (current == 0) return null;
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
