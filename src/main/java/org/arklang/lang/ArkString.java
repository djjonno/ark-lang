package org.arklang.lang;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArkString implements ArkIndexable, ArkEnumerable<Character> {

  private List<Character> string = new ArrayList<Character>();
  private String natural;

  public ArkString(String str) {
    natural = str;
    for (Character c : str.toCharArray()) {
      string.add(c);
    }
  }

  @Override
  public Iterator<Character> iterator() {
    return string.iterator();
  }

  @Override
  public Object get(Token token, Object index) {
    try {
      return string.get(indexToInteger(token, index));
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeError(token, "Array index out of bounds.");
    }
  }

  @Override
  public Object set(Token token, Object index, Object value) {
    try {
      Character c = (Character)value;
      string.set(indexToInteger(token, index), c);
      updateNatural();
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeError(token, "Array index out of bounds.");
    } catch (ClassCastException e) {
      throw new RuntimeError(token, "Can only assign character to indexed string.");
    }
    return value;
  }

  @Override
  public int length() {
    return string.size();
  }

  @Override
  public String toString() {
    return natural;
  }

  private void updateNatural() {
    StringBuilder b = new StringBuilder();
    for (Character c : string) b.append(c);
    natural = b.toString();
  }
}
