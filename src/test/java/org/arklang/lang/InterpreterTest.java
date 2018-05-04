package org.arklang.lang;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.arklang.lang.TokenType.*;

public class InterpreterTest {

  Interpreter interpreter = new Interpreter();

  @org.junit.Before
  public void setUp() throws Exception {
  }

  @org.junit.After
  public void tearDown() throws Exception {
  }

  @org.junit.Test
  public void visitOperationExpr() {

  }

  private Expr constructExpr(Object obj) {
    if (obj instanceof String) {
      return new Expr.Str(null, (String)obj);
    } else if (obj instanceof Character) {
      return new Expr.Char(null, (Character)obj);
    } else {
      return new Expr.Literal(obj);
    }
  }

  @org.junit.Test
  public void visitAdditionBinaryExpr() {
    Token token = new Token(PLUS, "+", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Number[][] vals = new Number[][] {
        {1, 2, 3},
        {1.0, 2, 3.0},
        {1.5, 2, 3.5},
        {2, 1, 3},
        {1, -2, -1},
        {-1, 2, 1},
        {-1, -2, -3},
        {-2, -1, -3},
    };

    for (Number[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitSubtractionBinaryExpr() {
    Token token = new Token(MINUS, "-", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Number[][] vals = new Number[][] {
        {1, 2, -1},
        {1, 2.0, -1.0},
        {2, 0.5, 1.5},
        {2, 1, 1},
        {1, -2, 3},
        {-1, 2, -3},
        {-1, -2, 1},
        {-2, -1, -1},
        {-2, -1.5, -0.5},
    };

    for (Number[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitMultiplicationBinaryExpr() {
    Token token = new Token(STAR, "*", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Number[][] vals = new Number[][] {
        {2, 3, 6},
        {1, -2, -2},
        {-1, 2, -2},
        {-3, -2, 6},
        {-2, -1, 2},
        {-2.5, -1.5, 3.75},
    };

    for (Number[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitDivisionBinaryExpr() {
    Token token = new Token(SLASH, "/", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Number[][] vals = new Number[][] {
        {2, 3, 0},
        {1, -2, 0},
        {-1, 2, 0},
        {-3, -2, 1},
        {-2, -1, 2},

        {-2.5, -1.5, 5/3.0},
        {6, 2.0, 3.0},
        {6, -2.0, -3.0},
    };

    for (Number[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitExponentBinaryExpr() {
    Token token = new Token(STAR_STAR, "**", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Number[][] vals = new Number[][] {
        {2, 3, 8},
        {3, 5, 243},
        {2, 0, 1},
        {2, 0.0, 1.0},
        {2, -1, 0},
    };

    for (Number[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitModuloBinaryExpr() {
    Token token = new Token(PERCENT, "%", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Number[][] vals = new Number[][] {
        {3, 2, 1},
        {3, 5, 3},
        {-10, 4, -2},
        {-10.0, 4, -2.0},
    };

    for (Number[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitGreaterBinaryExpr() {
    Token token = new Token(GREATER, ">", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Object[][] vals = new Object[][] {
        {3, 2, true},
        {3.0, 2, true},
        {3.0, 2.0, true},
        {3, 2.0, true},
        {3, 5, false},
        {3.0, 5, false},
        {-10, 4, false},
        {-10.0, 4, false},
    };

    for (Object[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitGreaterEqualBinaryExpr() {
    Token token = new Token(GREATER_EQUAL, ">=", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Object[][] vals = new Object[][] {
        {3, 2, true},
        {3.0, 2, true},
        {3.0, 2.0, true},
        {3, 2.0, true},
        {4, 4, true},
        {4.0, 4, true},
        {4.5, 4, true},
        {4.5, 4.0, true},
        {3, 5, false},
        {3.0, 5, false},
        {3.5, 5, false},
        {3.5, 5.5, false},
        {-10, 4, false},
        {-10.0, 4, false},
    };

    for (Object[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitLessBinaryExpr() {
    Token token = new Token(LESS, "<", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Object[][] vals = new Object[][] {
        {3, 2, false},
        {3.0, 2, false},
        {3.0, 2.0, false},
        {3, 2.0, false},
        {3, 5, true},
        {3.0, 5, true},
        {-10, 4, true},
        {-10.0, 4, true},
    };

    for (Object[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitLessEqualBinaryExpr() {
    Token token = new Token(LESS_EQUAL, "<=", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Object[][] vals = new Object[][] {
        {3, 2, false},
        {3.0, 2, false},
        {3.0, 2.0, false},
        {3, 2.0, false},
        {4.5, 4, false},
        {4.5, 4.0, false},

        {4, 4, true},
        {4.0, 4, true},
        {3, 5, true},
        {3.0, 5, true},
        {3.5, 5, true},
        {3.5, 5.5, true},
        {-10, 4, true},
        {-10.0, 4, true},
    };

    for (Object[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitEqualEqualBinaryExpr() {
    Token token = new Token(EQUAL_EQUAL, "==", null, 1);

    /*
     [ op1, op2, expected ]
     */
    final Object[][] vals = new Object[][] {
        {3.0, 2, false},
        {3.0, 2.0, false},
        {3, 2.0, false},
        {4, 4.0, true},
        {4.0, 4, true},
        {4, 4, true},

        {"hello", "hello", true},
        {"hello", "world", false},
        {'h', 'h', true},
        {'h', 'g', false},
//        {'h', 'h' - 0, true}, // comparison of char with ints
    };

    for (Object[] set : vals) {
      Expr op1 = constructExpr(set[0]),
          op2 = constructExpr(set[1]);
      Expr.Binary binary = new Expr.Binary(token, op1, op2);

      assertEquals(set[0] + " " + token.lexeme + " " + set[1] + " = " + set[2], set[2], interpreter.evaluate(binary));
    }
  }

  @org.junit.Test
  public void visitUnaryExpr() {
  }

  @org.junit.Test
  public void visitLiteralExpr() {
  }

  @org.junit.Test
  public void visitVariableExpr() {
  }

  @org.junit.Test
  public void visitAssignExpr() {
  }

  @org.junit.Test
  public void visitLambdaExpr() {
  }

  @org.junit.Test
  public void visitTernaryExpr() {
  }

  @org.junit.Test
  public void visitArrayExpr() {
  }

  @org.junit.Test
  public void visitStrExpr() {
  }

  @org.junit.Test
  public void visitCharExpr() {
  }

  @org.junit.Test
  public void visitIndexGetExpr() {
  }

  @org.junit.Test
  public void visitIndexSetExpr() {
  }

  @org.junit.Test
  public void visitRangeExpr() {
  }

  @org.junit.Test
  public void visitLetStmt() {
  }

  @org.junit.Test
  public void visitIfStmt() {
  }

  @org.junit.Test
  public void visitWhileStmt() {
  }

  @org.junit.Test
  public void visitForInStmt() {
  }

  @org.junit.Test
  public void visitSendStmt() {
  }

  @org.junit.Test
  public void visitBreakStmt() {
  }

  @org.junit.Test
  public void visitPrintStmt() {
  }

  @org.junit.Test
  public void visitBlockStmt() {
  }
}