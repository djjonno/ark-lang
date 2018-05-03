package org.arklang.lang;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

  private final Environment globals = new Environment();
  private Environment environment = globals;
  private final Map<Expr, Integer> locals = new HashMap<>();
  private boolean promptMode = false;

  Interpreter() {
    NativeFunctions.define(globals);
  }

  void interpret(List<Stmt> statements, boolean prompt) {
    promptMode = prompt;
    try {
      for (Stmt stmt : statements) {
        if (promptMode && stmt instanceof Stmt.Expression) {
          Expr expr = ((Stmt.Expression) stmt).expression;
          Object value = evaluate(expr);
          if (value != null && !(expr instanceof Expr.Lambda)) {
            System.out.println(value);
          }
        } else {
          execute(stmt);
        }
      }
    } catch (RuntimeError error) {
      Ark.runtimeError(error);
    }
  }

  public void resolve(Expr expr, int distance) {
    locals.put(expr, distance);
  }

  public void execute(Stmt stmt) {
    stmt.accept(this);
  }

  public void executeBlock(List<Stmt> statements, Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;
      for (Stmt stmt : statements) {
        execute(stmt);
      }
    } finally {
      this.environment = previous;
    }
  }

  public Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object visitOperationExpr(Expr.Operation expr) {
    Object target = evaluate(expr.target);

    if (!(target instanceof ArkCallable)) {
      throw new RuntimeError(expr.token, "Invalid operation target.");
    }

    List<Object> arguments = new ArrayList<>();
    for (Expr arg : expr.arguments) {
      arguments.add(evaluate(arg));
    }

    ArkCallable lambda = (ArkCallable) target;

    if (!lambda.variadic() && arguments.size() != lambda.arity()) {
      throw new RuntimeError(expr.token, "Expected " +
          lambda.arity() +
          " args but got " +
          arguments.size() + ".");
    }

    return lambda.call(this, arguments);
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
        if (left instanceof ArkString || right instanceof ArkString) {
          return left.toString() + right.toString();
        }
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left + (double)right;
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left - (double)right;
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left * (double)right;
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left / (double)right;
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left % (double)right;
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return Math.pow((double)left, (double)right);
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left > (double)right;
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left >= (double)right;
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left < (double)right;
        }
        break;
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left <= (double)right;
        }
        break;
      case BANG_EQUAL:
        if (left instanceof ArkString || right instanceof ArkString) {
          return !left.toString().equals(right.toString());
        }
        if (left instanceof Character || right instanceof Character) {
          return !left.toString().equals(right.toString());
        }
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left != (double)right;
        }
        break;
      case EQUAL_EQUAL:
        if (left instanceof ArkString || right instanceof ArkString) {
          return left.toString().equals(right.toString());
        }
        if (left instanceof Character || right instanceof Character) {
          return left.toString().equals(right.toString());
        }
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
        if (left instanceof Double && right instanceof Double) {
          return (double)left == (double)right;
        }
        break;
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
        if (right instanceof Integer) {
          return -(Integer)right;
        } else {
          return -(Double)right;
        }
      case TILDE:
        checkIntegerOperand(expr.operator, right);
        return ~(int)right;
    }

    return null;
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return lookUpVariable(expr.name, expr);
  }

  @Override
  public Object visitAssignExpr(Expr.Assign expr) {
    Object value = evaluate(expr.value);

    Integer distance = locals.get(expr);
    if (distance != null) {
      environment.assignAt(distance, expr.name, value);
    } else {
      globals.assign(expr.name, value);
    }

    environment.assign(expr.name, value);
    return value;
  }

  @Override
  public Object visitLambdaExpr(Expr.Lambda expr) {
    Lambda lambda = new Lambda(expr, environment);
    if (expr.name != null) {
      environment.define(expr.name.lexeme, lambda);
    }
    return lambda;
  }

  @Override
  public Object visitTernaryExpr(Expr.Ternary expr) {
    Object condition = evaluate(expr.condition);
    return isTruthy(condition) ? evaluate(expr.expr1) : evaluate(expr.expr2);
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }

  @Override
  public Object visitArrayExpr(Expr.Array expr) {
    return new ArkArray(expr.items.stream()
        .map(this::evaluate)
        .collect(Collectors.toList()));
  }

  @Override
  public Object visitStrExpr(Expr.Str expr) {
    return new ArkString(expr.str);
  }

  @Override
  public Object visitCharExpr(Expr.Char expr) {
    return expr.c;
  }

  @Override
  public Object visitIndexGetExpr(Expr.IndexGet expr) {
    Object indexee = evaluate(expr.indexee);

    if (!(indexee instanceof ArkIndexable)) {
      Ark.error(expr.token, "Can only index collection types.");
    } else {
      return ((ArkIndexable) indexee).get(expr.token, evaluate(expr.index));
    }

    return null;
  }

  @Override
  public Object visitIndexSetExpr(Expr.IndexSet expr) {
    Object indexee = evaluate(expr.indexee);

    if (!(indexee instanceof ArkIndexable)) {
      Ark.error(expr.token, "Can only index collection types.");
    } else {
      return ((ArkIndexable) indexee).set(expr.token,
          evaluate(expr.index), evaluate(expr.value));
    }

    return null;
  }

  @Override
  public Object visitRangeExpr(Expr.Range expr) {
    try {
      Integer lower = (Integer)evaluate(expr.lower);
      Integer upper = (Integer)evaluate(expr.upper);

      ArrayList<Object> range = new ArrayList<>();
      for (int i = lower; i < upper; ++i) {
        range.add(i);
      }

      if (expr.closed) {
        range.add(upper);
      }

      return new ArkArray(range);
    } catch (ClassCastException e) {
      Ark.error(expr.token,
          "Lower & Upper bounds of range expression must be integers.");
      return null;
    }
  }

  @Override
  public Void visitLetStmt(Stmt.Let stmt) {
    for (int i = 0; i < stmt.names.size(); ++i) {
      Object value = null;
      if (stmt.initializers.get(i) != null) {
        value = evaluate(stmt.initializers.get(i));
      }
      environment.define(stmt.names.get(i).lexeme, value);
    }

    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    Object condition = evaluate(stmt.condition);
    if (isTruthy(condition)) {
      execute(stmt.thenBranch);
    } else if (stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    while (isTruthy(evaluate(stmt.condition))) {
      try {
        execute(stmt.body);
      } catch (BreakJump b) {
        break;
      }
    }
    return null;
  }

  @Override
  public Void visitForInStmt(Stmt.ForIn stmt) {

    Object enumerable = evaluate(stmt.enumerable);
    if (!(enumerable instanceof ArkEnumerable)) {
      Ark.error(stmt.token, "for stmt target must be enumerable.");
      return null;
    }

    Integer index = 0;

    for (Object o : (ArkEnumerable)enumerable) {
      environment.define(stmt.itemIterator.lexeme, o);
      if (stmt.indexIterator != null) {
        environment.define(stmt.indexIterator.lexeme, index++);
      }

      try {
        execute(stmt.body);
      } catch (BreakJump e) {
        break;
      }
    }

    return null;
  }

  @Override
  public Void visitSendStmt(Stmt.Send stmt) {
    Object value = evaluate(stmt.value);
    throw new SendJump(value);
  }

  @Override
  public Void visitBreakStmt(Stmt.Break stmt) {
    throw new BreakJump();
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    Object value = evaluate(stmt.expression);
    System.out.println(value);
    return null;
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    executeBlock(stmt.statements, new Environment(environment));
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

  private Object lookUpVariable(Token name, Expr expr) {
    Integer distance = locals.get(expr);
    if (distance != null) {
      return environment.getAt(distance, name.lexeme);
    } else {
      return globals.get(name);
    }
  }
}
