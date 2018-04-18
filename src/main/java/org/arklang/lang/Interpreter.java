package org.arklang.lang;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

  Interpreter() {
  }

  void interpret(List<Stmt> statements) {
    try {
      for (Stmt e : statements) {
        System.out.println(evaluate(((Stmt.Expression) e).expression));
      }
    } catch (RuntimeError error) {
      Ark.runtimeError(error);
    }
  }

  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object visitOperationExpr(Expr.Operation expr) {
    return null;
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      /*
      Number Operations
       */
      case PLUS:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left + (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left + (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left + (int)right;
        }
      case MINUS:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left - (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left - (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left - (int)right;
        }
      case STAR:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left * (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left * (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left * (int)right;
        }
      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left / (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left / (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left / (int)right;
        }
      case PERCENT:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left % (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left % (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left % (int)right;
        }
      case STAR_STAR:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)Math.pow((int)left, (int)right);
        }
        if (left instanceof Integer && right instanceof Double) {
          return Math.pow((int)left, (double)right);
        }
        if (left instanceof Double && right instanceof Integer) {
          return Math.pow((double)left, (int)right);
        }
      case GREATER:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left > (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left > (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left > (int)right;
        }
      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left >= (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left >= (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left >= (int)right;
        }
      case LESS:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left < (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left < (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left < (int)right;
        }
      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left <= (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left <= (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left <= (int)right;
        }
      case BANG_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left != (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left != (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left != (int)right;
        }
      case EQUAL_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        if (left instanceof Integer && right instanceof Integer) {
          return (int)left == (int)right;
        }
        if (left instanceof Integer && right instanceof Double) {
          return (int)left == (double)right;
        }
        if (left instanceof Double && right instanceof Integer) {
          return (double)left == (int)right;
        }
      /*
      Bitwise Operations
       */
      case AMPERSAND:
        checkIntegerOperands(expr.operator, left, right);
        return (int)left & (int)right;
      case CARET:
        checkIntegerOperands(expr.operator, left, right);
        return (int)left ^ (int)right;
      case PIPE:
        checkIntegerOperands(expr.operator, left, right);
        return (int)left | (int)right;
      case LEFT_SHIFT:
        checkIntegerOperands(expr.operator, left, right);
        return (int)left << (int)right;
      case RIGHT_SHIFT:
        checkIntegerOperands(expr.operator, left, right);
        return (int)left >> (int)right;
      case U_RIGHT_SHIFT:
        checkIntegerOperands(expr.operator, left, right);
        return (int)left >>> (int)right;
      /*
      Logical operations
       */
      case AND:
        return isTruthy(left) && isTruthy(right);
      case OR:
        return isTruthy(left) || isTruthy(right);

    }

    return null;
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return (right instanceof Integer) ? -(int)right : -(double)right;
      case TILDE:
        checkIntegerOperand(expr.operator, right);
        return ~(int) right;
    }

    return null;
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return null;
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }

  /*
  Interpreter helpers
   */
  private void checkNumberOperand(Token operator, Object op) {
    if (!(op instanceof Number)) {
      throw new RuntimeError(operator, "Operands must be numeric.");
    }
  }

  private void checkNumberOperands(Token operator, Object op1, Object op2) {
    if (!(op1 instanceof Number) || !(op2 instanceof Number)) {
      throw new RuntimeError(operator, "Operands must be numeric.");
    }
  }

  private void checkIntegerOperand(Token operator, Object op1) {
    if (!(op1 instanceof Integer)) {
      throw new RuntimeError(operator, "Operand must be an integer.");
    }
  }

  private void checkIntegerOperands(Token operator, Object op1, Object op2) {
    if (!(op1 instanceof Integer) || !(op2 instanceof Integer)) {
      throw new RuntimeError(operator, "Operands must be integers.");
    }
  }

  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (Boolean)object;
    if (object instanceof Number) return !(object).equals(0);
    return true;
  }
}
