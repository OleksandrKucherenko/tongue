package com.artfulbits.tongue.web;

import android.support.annotation.NonNull;
import android.view.View;

import com.artfulbits.tongue.ResourceString;
import com.artfulbits.tongue.toolbox.IResourceStringsCache;
import com.artfulbits.tongue.toolbox.SharedPreferencesCache;

import java.util.Locale;

/**
 * Cache provider, it store values in shared preferences. Provider is responsible for converting translation context
 * {@link com.artfulbits.tongue.web.Localization.TaskContext} into static result {@link
 * com.artfulbits.tongue.web.CacheProvider.StaticResult}.
 */
public class CacheProvider implements Localization.Provider {
  /** {@inheritDoc} */
  @Override
  public float priority() {
    return 10.0f;
  }

  /** {@inheritDoc} */
  @Override
  public boolean supported(@NonNull final Locale language) {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Localization.Task localize(@NonNull final Localization.TaskContext tc) {
    final IResourceStringsCache cache = SharedPreferencesCache.getCache(tc.language);
    final ResourceString value = cache.get(tc.resource);

    if (null != value) {
      // if available callback, than raise onSuccess
      if (null != tc.callback) {
        final View view = tc.getView();

        // be sure that view instance is still exists, dead view does not need our callback
        if (null == view) return null;

        tc.callback.onSuccess(view, tc.resource, value);
      }

      return new StaticResult(tc, value);
    }

    // give chance to other providers
    return null;
  }

  /** Task that contains static result, which never changed in time. */
  private static class StaticResult implements Localization.Task {
    private final Localization.TaskContext mContext;
    private final ResourceString mValue;

    public StaticResult(@NonNull final Localization.TaskContext context, @NonNull final ResourceString value) {
      mContext = context;
      mValue = value;
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public Localization.TaskContext getContext() {
      return mContext;
    }

    /** {@inheritDoc} */
    @Override
    @NonNull
    public ResourceString getResult() {
      return mValue;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isComplete() {
      return true;
    }
  }
}