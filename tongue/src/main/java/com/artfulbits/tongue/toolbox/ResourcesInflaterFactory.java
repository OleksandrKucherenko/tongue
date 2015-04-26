package com.artfulbits.tongue.toolbox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Field;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Factory that allows in one call attach automatic translation to any activity or fragment. During inflating the view
 * factory compose cache of resources that after that will be used by {@link com.artfulbits.tongue.Tongue} class for
 * fastest translation.
 */
public final class ResourcesInflaterFactory implements LayoutInflater.Factory {
  /* [ CONSTANTS ] ================================================================================================= */

  /** Track context's to which custom inflater already attached. */
  private final static WeakHashMap<Context, Boolean> sAttachedTo = new WeakHashMap<>();

	/* [ MEMBERS ] =================================================================================================== */

  /** Reference on previously installed factory. */
  private final LayoutInflater.Factory mDefaultFactory;

	/* [ CONSTRUCTORS ] ============================================================================================== */

  /** hidden constructor. */
  private ResourcesInflaterFactory(@Nullable final LayoutInflater.Factory oldFactory) {
    mDefaultFactory = oldFactory;
  }

	/* [ STATIC METHODS ] ============================================================================================ */

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

      if (null != oldFactory && !(oldFactory instanceof ResourcesInflaterFactory)) {
        patch(inflater, new ResourcesInflaterFactory(oldFactory));
      } else {
        inflater.setFactory(new ResourcesInflaterFactory(null));
      }
    }

    sAttachedTo.put(context, true);
  }

  /** Patch runtime values of the inflater instance for making possible of assigning of another factory. */
  private static boolean patch(@NonNull final LayoutInflater inflater,
                               @NonNull final LayoutInflater.Factory newFactory) {
    boolean success = false;
    final Class<?> typeInfo = inflater.getClass();

    try {
      final List<Field> fields = ReflectionUtils.getInheritedPrivateFields(typeInfo);
      final Field fIsSet = ReflectionUtils.findField(fields, "mFactorySet");
      final Field fFactory = ReflectionUtils.findField(fields, "mFactory");

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

  /* [ METHODS ] =================================================================================================== */


	/* [ Interface Factory ] ========================================================================================= */

  /** {@inheritDoc} */
  @Override
  public View onCreateView(final String name, final Context context, final AttributeSet attrs) {
    View v = null;

    if (null != mDefaultFactory) {
      v = mDefaultFactory.onCreateView(name, context, attrs);
    }

    Log.d("INFLATE", "-- View: " + name);
    for (int i = 0, len = attrs.getAttributeCount(); i < len; i++) {
      final String attrName = attrs.getAttributeName(i);
      final String value = attrs.getAttributeValue(i);

      Log.d("INFLATE", "Name: " + attrName + ", value: " + value);

      // TODO: find text, hint and other string resources and register them in lookup hash
    }

    return v;
  }
}
