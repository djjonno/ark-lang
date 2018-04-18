package org.arklang.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Ark {

  private static final Interpreter interpreter = new Interpreter();

  final static String version = "0.1";
  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws Exception {
    if (args.length > 1) {
      System.out.println("usage: hype [script]");
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    System.out.println();
    System.out.println("   _ _ / ");
    System.out.println("  (// /( " + version);
    System.out.println();

    for (;;) {
      System.out.print(")  ");
      run(reader.readLine());

      hadError = false;
    }
  }

  private static void run(String source) {
    List<Token> tokens = new Scanner(source).scanTokens();

    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();
    if (hadError) return;

    Resolver resolver = new Resolver(interpreter);
    resolver.resolve(statements);
    if (hadError) return;

    interpreter.interpret(statements);
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  private static void report(int line, String where, String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message
    );
    hadError = true;
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }

}
