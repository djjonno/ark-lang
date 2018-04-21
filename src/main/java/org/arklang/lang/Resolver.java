package org.arklang.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Resolves variables/state to scopes or levels within source code.
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

  private final Interpreter interpreter;
  private final Stack<HashMap<String, Boolean>> scopes = new Stack<>();

  private boolean inLoop = false;

  public Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  void resolve(List<Stmt> stmts) {
    for (Stmt statement : stmts) {
      resolve(statement);
    }
  }

  private void resolve(Stmt stmt) {
    stmt.accept(this);
  }

  private void resolve(Expr expr) {
    expr.accept(this);
  }

  private void beginScope() {
    scopes.push(new HashMap<>());
  }

  private void endScope() {
    scopes.pop();
  }

  private void declare(Token name) {
    if (scopes.isEmpty()) return;

    Map<String, Boolean> scope = scopes.peek();
    if (scope.containsKey(name.lexeme)) {
      Ark.error(name, "Variable with this name already declared in this scope.");
    }

    scope.put(name.lexeme, false);
  }

  private void define(Token name) {
    if (scopes.isEmpty()) return;
    scopes.peek().put(name.lexeme, true);
  }

  private void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; --i) {
      if (scopes.get(i).containsKey(name.lexeme)) {
        interpreter.resolve(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }

  /*
  Visitors Stmt,Exprs
   */

  @Override
  public Void visitOperationExpr(Expr.Operation expr) {
    resolve(expr.target);
    for (Expr e : expr.arguments) {
      resolve(e);
    }
    return null;
  }

  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    if (!scopes.isEmpty() &&
        scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
      Ark.error(expr.name,
          "Cannot read local variable in its own initializer.");
    }

    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitTernaryExpr(Expr.Ternary expr) {
    resolve(expr.condition);
    resolve(expr.expr1);
    resolve(expr.expr2);
    return null;
  }

  @Override
  public Void visitLambdaExpr(Expr.Lambda expr) {
    if (expr.name != null) {
      declare(expr.name);
      define(expr.name);
    }

    beginScope();
    if (expr.parameters != null) {
      for (Token param : expr.parameters) {
        declare(param);
        define(param);
      }
    }
    resolve(expr.body);
    endScope();
    return null;
  }

  @Override
  public Void visitArrayExpr(Expr.Array expr) {
    for (Expr e : expr.items) {
      resolve(e);
    }
    return null;
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null) resolve(stmt.elseBranch);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    boolean previousInLoop = inLoop;
    inLoop = true;

    resolve(stmt.condition);
    resolve(stmt.body);

    inLoop = previousInLoop;
    return null;
  }

  @Override
  public Void visitBreakStmt(Stmt.Break stmt) {
    if (!inLoop) {
      Ark.error(stmt.keyword, "Cannot use 'break' outside of loop.");
    }
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitLetStmt(Stmt.Let stmt) {
    for (int i = 0; i < stmt.names.size(); ++i) {
      declare(stmt.names.get(i));
      if (stmt.initializers.get(i) != null) {
        resolve(stmt.initializers.get(i));
      }
      define(stmt.names.get(i));
    }

    return null;
  }

  @Override
  public Void visitSendStmt(Stmt.Send stmt) {
    resolve(stmt.value);
    return null;
  }
}
