package org.arklang.lang;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

  Interpreter() {
  }

  void interpret(List<Stmt.Expression> statements) {
    try {
      for (Stmt.Expression e : statements) {
        System.out.println(evaluate(e.expression));
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
        if (right instanceof Integer) {
          return -(int)right;
        }
        if (right instanceof Double) {
          return -(double)right;
        }
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

  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (Boolean)object;
    return true;
  }
}
