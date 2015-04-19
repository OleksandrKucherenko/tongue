package com.artfulbits.tongue.toolbox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;

/** Factory that allows in one call attach automatic translation to any activity or fragment. */
public final class TongueFactory implements LayoutInflater.Factory {
  /** Track context's to which custom inflater already attached. */
  private final static WeakHashMap<Context, Boolean> sAttachedTo = new WeakHashMap<>();
  /** Reference on previously installed factory. */
  private final LayoutInflater.Factory mDeafultFactory;

  /** hidden constructor. */
  private TongueFactory(@Nullable final LayoutInflater.Factory oldFactory) {
    mDeafultFactory = oldFactory;
  }

  /**
   * Initialize custom inflater, it is safe to call it several times for one context instance.
   *
   * @param context the context
   */
  public static void initialize(@NonNull final Context context) {
    if (!sAttachedTo.containsKey(context)) {
      final LayoutInflater inflater = LayoutInflater.from(context);
      final LayoutInflater.Factory oldFactory = inflater.getFactory();

      // TODO: Factory2() is not supported yet!

      if (null != oldFactory && !(oldFactory instanceof TongueFactory)) {
        patch(inflater, new TongueFactory(oldFactory));
      } else {
        inflater.setFactory(new TongueFactory(null));
      }
    }

    sAttachedTo.put(context, true);
  }

  private static boolean patch(@NonNull final LayoutInflater inflater,
                               @NonNull final LayoutInflater.Factory newFactory) {
    boolean success = false;
    final Class<?> typeInfo = inflater.getClass();

    try {
      final List<Field> fields = getInheritedPrivateFields(typeInfo);
      final Field fIsSet = findField(fields, "mFactorySet");
      final Field fFactory = findField(fields, "mFactory");

      if (null != fIsSet && null != fFactory) {
        fIsSet.setAccessible(true);
        Boolean isSet = (Boolean) fIsSet.get(inflater);

        if (isSet) {
          fFactory.setAccessible(true);
          fFactory.set(inflater, newFactory);
        } else {
          inflater.setFactory(newFactory);
        }

        success = true;
      }
    } catch (final Throwable ignored) {
      // do nothing
    }

    return success;
  }

  /**
   * Find in list of fields specific one by its name.
   *
   * @param fields list of fields. Should be sorted by name!
   * @param name field name which we want to find.
   * @return found field, otherwise {@code null}.
   */
  @Nullable
  private static Field findField(@NonNull final List<Field> fields, @NonNull final String name) {

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
  private static ArrayList<Field> getInheritedPrivateFields(@NonNull final Class<?> type) {
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

  /** {@inheritDoc} */
  @Override
  public View onCreateView(final String name, final Context context, final AttributeSet attrs) {
    View v = null;

    if (null != mDeafultFactory) {
      v = mDeafultFactory.onCreateView(name, context, attrs);
    }

    Log.d("INFLATE", "-- View: " + name);
    for (int i = 0, len = attrs.getAttributeCount(); i < len; i++) {
      final String attrName = attrs.getAttributeName(i);
      final String value = attrs.getAttributeValue(i);

      Log.d("INFLATE", "Name: " + attrName + ", value: " + value);
    }

    return v;
  }

  private static class ByName implements java.util.Comparator<Field> {
    @Override
    public int compare(final Field lhs, final Field rhs) {
      return lhs.getName().compareTo(rhs.getName());
    }
  }

  private static class SearchByNameComparator implements Comparator<Object> {
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
