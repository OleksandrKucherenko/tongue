package com.artfulbits.tongue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.util.WeakHashMap;

/** Factory that allows in one call attach automatic translation to any activity or fragment. */
/* package */ final class TongueFactory implements LayoutInflater.Factory {
  /** Single instance. */
  public static final TongueFactory INSTANCE = new TongueFactory();

  /** Track context's to which custom inflater already attached. */
  private final static WeakHashMap<Context, Boolean> sAttachedTo = new WeakHashMap<>();

  /** hidden constructor. */
  private TongueFactory() {
    // do nothing.
  }

  /**
   * Initialize custom inflater, it is safe to call it several times for one context instance.
   *
   * @param context the context
   */
  public static void initialize(@NonNull final Context context) {
    if (!sAttachedTo.containsKey(context)) {
      LayoutInflater.from(context).setFactory(TongueFactory.INSTANCE);
      sAttachedTo.put(context, true);
    }
  }

  /** {@inheritDoc} */
  @Override
  public View onCreateView(final String name, final Context context, final AttributeSet attrs) {

    // TODO: track string resources for every created view

    return null;
  }
}
