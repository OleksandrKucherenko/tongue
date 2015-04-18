package com.artfulbits.tongue.web;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.artfulbits.tongue.ResourceString;
import com.artfulbits.tongue.Tongue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Factory that hides concrete implementation of each web API request and allows connection of multiple provides.
 */
public final class Localization {
  /* [ CONSTANTS ] ================================================================================================= */

  /** Connected providers. */
  private final static ArrayList<Provider> sProviders = new ArrayList<>();

	/* [ CONSTRUCTORS ] ============================================================================================== */

  /** hidden constructor. */
  private Localization() {
    throw new AssertionError("hidden constructor. Calls is not allowed.");
  }

	/* [ REGISTRATION ] ============================================================================================== */

  /**
   * Register localization provider.
   *
   * @param provider the provider instance.
   */
  public static void register(@NonNull final Provider provider) {
    sProviders.add(provider);
    Collections.sort(sProviders, new byPriority());
  }

  /**
   * Unregister localization provider.
   *
   * @param provider the provider instance.
   */
  public static void unregister(@NonNull final Provider provider) {
    sProviders.remove(provider);
  }

	/* [ STATIC METHODS ] ============================================================================================ */

  /** Compose new instance of task context from provided parameters. */
  @NonNull
  public static TaskContext context(@NonNull final View view,
                                    @NonNull final ResourceString resource,
                                    @NonNull final Locale lang,
                                    @Nullable final Tongue.Callback callback) {
    return new Localization.TaskContext(view, resource, lang, callback);
  }

  @Nullable
  public static Task create(@NonNull final TaskContext context) {
    // iterate providers till we find a suitable one
    for (final Provider p : sProviders) {
      if (p.supported(context.language)) {
        final Task task = p.localize(context);

        // in case NULL we should try next provider in loop
        if (null != task) {
          return task;
        }
      }
    }

    // report failure, we cannot find suitable provider
    if (null != context.callback && null != context.getView()) {
      context.callback.onFailure(context.getView(), context.resource);
    }

    return null;
  }

	/* [ NESTED DECLARATIONS ] ======================================================================================= */

  /** Task context. */
  public interface Task {
    /** Reference on Task context. */
    @NonNull
    TaskContext getContext();

    /** Get result of the translation. */
    @NonNull
    ResourceString getResult();

    /** {@code true} - when extraction of result is done, otherwise {@code false}. */
    boolean isComplete();
  }

  /** Localization provider. */
  public interface Provider {
    /** Provider priority. */
    float priority();

    /** Is requested language supported by provider or not. */
    boolean supported(@NonNull Locale language);

    /** Create background task based on provided context. */
    Task localize(@NonNull final TaskContext context);
  }

  public final static class TaskContext {
    public final WeakReference<View> view;

    public final ResourceString resource;

    public final Locale language;

    public final Tongue.Callback callback;

    public TaskContext(@NonNull final View v, @NonNull final ResourceString r,
                       @NonNull final Locale l, @Nullable final Tongue.Callback callback) {
      this.view = new WeakReference<>(v);
      this.resource = r;
      this.language = l;
      this.callback = callback;
    }

    /** Weak reference on view that request location. Can be {@code null} if view already destroyed by the OS. */
    @Nullable
    public View getView() {
      return this.view.get();
    }
  }

  private static class byPriority implements java.util.Comparator<Provider> {
    @Override
    public int compare(final Provider lhs, final Provider rhs) {
      return Float.compare(lhs.priority(), rhs.priority());
    }
  }
}
