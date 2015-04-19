package com.artfulbits.tongue.web;

import android.support.annotation.NonNull;
import android.view.View;

import com.artfulbits.tongue.ResourceString;
import com.artfulbits.tongue.toolbox.MemoryCache;

import java.util.Locale;

/** In memory cache provider. */
public class CacheProvider implements Localization.Provider {
  @Override
  public float priority() {
    return 10.0f;
  }

  /** {@inheritDoc} */
  @Override
  public boolean supported(@NonNull final Locale language) {
    // force initialization of cache instance
    final MemoryCache cache = MemoryCache.getCache(language);

    return null != cache;
  }

  @Override
  public Localization.Task localize(@NonNull final Localization.TaskContext context) {
    final MemoryCache cache = MemoryCache.getCache(context.language);
    final ResourceString value = cache.get(context.resource);

    if (null != value) {
      // if available callback, than raise onSuccess
      if (null != context.callback) {
        final View view = context.getView();

        // be sure that view instance is still exists
        if (null != view) {
          context.callback.onSuccess(view, context.resource, value);
        }
      }

      return new Localization.Task() {
        @NonNull
        @Override
        public Localization.TaskContext getContext() {
          return context;
        }

        @Override
        public ResourceString getResult() {
          return value;
        }

        @Override
        public boolean isComplete() {
          return true;
        }
      };
    }

    // give chance to other providers
    return null;
  }
}