package org.arklang.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.arklang.lang.TokenType.*;

public class Scanner {

  private final String source;
  private final List<Token> tokens;
  private int start;
  private int current;
  private int line;
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("let",     LET);
    keywords.put("if",      IF);
    keywords.put("else",    ELSE);
    keywords.put("and",     AND);
    keywords.put("or",      OR);
    keywords.put("@",       ARG_POS);
    keywords.put("send",    SEND);
    keywords.put("while",   WHILE);
    keywords.put("break",   BREAK);
    keywords.put("true",    TRUE);
    keywords.put("false",   FALSE);
    keywords.put("nil",     NIL);
    keywords.put("id",      ID);
    keywords.put("int",     INT);
    keywords.put("double",  DOUBLE);
    keywords.put("char",    CHAR);
    keywords.put("string",  STRING);
    keywords.put("bool",    BOOL);
    keywords.put("list",    LIST);
    keywords.put("lambda",  LAMBDA);
    keywords.put("dict",    DICT);
    keywords.put("print",   PRINT);
  }

  public Scanner(String inSource) {
    source = inSource;
    tokens = new ArrayList<>();
    start = 0;
    current = 0;
    line = 1;
  }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case '[': addToken(LEFT_BRACKET); break;
      case ']': addToken(RIGHT_BRACKET); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case ':': addToken(COLON); break;
      case '?': addToken(QUESTION_MARK); break;
      case '+': addToken(PLUS); break;
      case '-': addToken(match('>') ? RIGHT_ARROW : MINUS); break;
      case '*': addToken(match('*') ? STAR_STAR : STAR); break;
      case '/': addToken(SLASH); break;
      case '%': addToken(PERCENT); break;
      case '|': addToken(PIPE); break;
      case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
      case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
      case '^': addToken(CARET); break;
      case '&': addToken(AMPERSAND); break;
      case '~': addToken(TILDE); break;
      case '<': {
        if (match('=')) {
          addToken(LESS_EQUAL);
        } else if (match('<')) {
          addToken(LEFT_SHIFT);
        } else {
          addToken(LESS);
        }
      } break;
      case '>': {
        if (match('=')) {
          addToken(GREATER_EQUAL);
        } else if (match('>')) {
          if (match('>')) {
            addToken(U_RIGHT_SHIFT);
          } else {
            addToken(RIGHT_SHIFT);
          }
        } else {
          addToken(GREATER);
        }

      } break;
      case ';': {
        if (!match(';')) {
          Ark.error(line, "Unexpected character. Did you mean ';;'?");
          return;
        }
        while (peek() != '\n' && !isAtEnd()) advance();
      }

      // whitespace
      case ' ': case '\r': case '\t':
        break;
      case '\n':
        line++;
        break;

      case '"': string(); break;

      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Ark.error(line, "Unexpected character.");
        }
    }
  }

  private char advance() {
    current++;
    return source.charAt(current - 1);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(current) != expected) return false;
    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }
    // Unterminated string
    if (isAtEnd()) {
      Ark.error(line, "Unterminated string.");
      return;
    }

    // The closing ".
    advance();

    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private boolean isDigit(char c) {
    return (c >= '0' && c <= '9');
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_';
  }

  private void number() {
    boolean fractional = false;
    while (isDigit(peek())) advance();

    // Check for fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      fractional = true;
      advance(); // consume the '.'
      while (isDigit(peek())) advance();
    }

    // TODO: test this
    if (fractional) {
      addToken(DOUBLE, Double.parseDouble(source.substring(start, current)));
    } else {
      addToken(INT, Integer.parseInt(source.substring(start, current)));
    }
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private void identifier() {
    while (isAlphaNumeric(peek())) advance();

    String text = source.substring(start, current);

    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addToken(type);

  }
}
