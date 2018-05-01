package org.arklang.lang;

import java.util.Iterator;
import java.util.List;

public class ArkArray implements ArkIndexable, ArkEnumerable {
  private final List<Object> items;

  ArkArray(List<Object> items) {
    this.items = items;
  }

  @Override
  public Object get(Token token, Object index) {
    try {
      return items.get(indexToInteger(token, index));
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeError(token, "Array index out of bounds.");
    }
  }

  @Override
  public Object set(Token token, Object index, Object value) {
    try {
      items.set(indexToInteger(token, index), value);
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeError(token, "Array index out of bounds.");
    }
    return value;
  }

  @Override
  public int length() {
    return items.size();
  }

  @Override
  public String toString() {
    return items.toString();
  }

  @Override
  public Iterator<Object> iterator() {
    return items.iterator();
  }

}
