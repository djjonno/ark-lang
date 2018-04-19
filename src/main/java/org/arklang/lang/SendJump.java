package org.arklang.lang;

public class SendJump extends RuntimeException {
  final Object value;

  public SendJump(Object value) {
    super(null, null, false, false);
    this.value = value;
  }
}
