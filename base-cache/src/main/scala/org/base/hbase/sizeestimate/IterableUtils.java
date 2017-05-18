package org.base.hbase.sizeestimate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for Iterable including null-safe handlers.
 */
public class IterableUtils {

  private static final List<Object> EMPTY_LIST = Collections
      .unmodifiableList(new ArrayList<Object>(0));

  @SuppressWarnings("unchecked")
  public static <T> Iterable<T> nullSafe(Iterable<T> in) {
    if (in == null) {
      return (List<T>) EMPTY_LIST;
    }
    return in;
  }

}
