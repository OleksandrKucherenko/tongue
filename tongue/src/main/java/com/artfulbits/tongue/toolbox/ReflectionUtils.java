package com.artfulbits.tongue.toolbox;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Methods for manipulating classes via reflection. */
public final class ReflectionUtils {
  /**
   * Find in list of fields specific one by its name.
   *
   * @param fields list of fields. Should be sorted by name!
   * @param name field name which we want to find.
   * @return found field, otherwise {@code null}.
   */
  @Nullable
  public static Field findField(@NonNull final List<Field> fields, @NonNull final String name) {
    final int index = Collections.binarySearch(fields, name, new SearchByNameComparator());

    // field found
    if (index >= 0 && index < fields.size()) {
      return fields.get(index);
    }

    return null;
  }

  /**
   * Extract all inherited fields from class. Results are sorted by name.
   *
   * @param type type to check
   * @return list of found fields.
   */
  @NonNull
  public static ArrayList<Field> getInheritedPrivateFields(@NonNull final Class<?> type) {
    final ArrayList<Field> result = new ArrayList<Field>();

    Class<?> i = type;
    while (i != null && i != Object.class) {
      for (Field field : i.getDeclaredFields()) {
        if (!field.isSynthetic()) {
          result.add(field);
        }
      }

      i = i.getSuperclass();
    }

    Collections.sort(result, new ByName());

    return result;
  }

  /** Sort fields by name. */
  private static final class ByName implements java.util.Comparator<Field> {
    @Override
    public int compare(final Field lhs, final Field rhs) {
      return lhs.getName().compareTo(rhs.getName());
    }
  }

  /** Search in fields collection by field name. */
  private static final class SearchByNameComparator implements Comparator<Object> {
    @Override
    public int compare(Object lhs, Object rhs) {
      if (lhs instanceof Field && rhs instanceof String) {
        return ((Field) lhs).getName().compareTo((String) rhs);
      }

      if (lhs instanceof String && rhs instanceof Field) {
        return ((String) lhs).compareTo(((Field) rhs).getName());
      }

      throw new AssertionError("unexpected");
    }
  }
}
