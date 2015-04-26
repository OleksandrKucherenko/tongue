package com.artfulbits.tongue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.artfulbits.tongue.extractors.ResourcesExtractor;
import com.artfulbits.tongue.toolbox.ResourcesInflaterFactory;
import com.artfulbits.tongue.toolbox.ViewsUpdater;
import com.artfulbits.tongue.web.CacheProvider;
import com.artfulbits.tongue.web.EmbedResources;
import com.artfulbits.tongue.web.GoogleTranslateVolley;
import com.artfulbits.tongue.web.Localization;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** Major API. Allows to convert any string resource into it translated version. */
public final class Tongue {
  /* [ CONSTANTS ] ================================================================================================= */

  /** Quantity of requests that are scheduled for processing. */
  private final static AtomicInteger IN_PROGRESS = new AtomicInteger(0);
  /** {@code true} - provider initialize, otherwise {@code false}. */
  private final static AtomicBoolean IS_INITIALIZED = new AtomicBoolean(false);

  /** Instance of the cache. We use it for storing newly extracted resources on disk. */
  private static CacheProvider sCache;

	/* [ CONSTRUCTORS ] ============================================================================================== */

  /** hidden constructor. */
  private Tongue() {
    throw new AssertionError("hidden constructor. Not designed for inheritance.");
  }

	/* [ STATIC METHODS ] ============================================================================================ */

  /**
   * Initialize context by custom inflater that tracks all resources for localization.
   *
   * @param context the context
   */
  public static void initialize(@NonNull final Context context) {
    // register all possible providers
    if (!IS_INITIALIZED.getAndSet(true)) {
      // step #1: try embedded resources
      Localization.register(new EmbedResources(context));

      // step #2: try cached from previous sessions localizations
      Localization.register(sCache = new CacheProvider());

      // step #3: by Google Translate provider
      Localization.register(new GoogleTranslateVolley(context));
    }

    ResourcesInflaterFactory.initialize(context);
  }

  /**
   * Ask library to start translation of the UI in background process.
   *
   * @param v root view for translation
   * @param l concrete destination language.
   * @return number of scheduled localization strings extracting
   */
  public static int translateTo(@NonNull final View v, @NonNull final Locale l) {
    return translateTo(v, l, new ViewsUpdater());
  }

  /** Get reference on shared cache. */
  public static CacheProvider getCache() {
    if (!IS_INITIALIZED.get()) {
      throw new IllegalStateException("Initialize module first by calling Tongue.initialize(...).");
    }

    return sCache;
  }

	/* [ IMPLEMENTATION ] ============================================================================================ */

  /**
   * Ask library to start translation of the UI in background process.
   *
   * @param v root view for translation
   * @param l concrete destination language.
   * @param callback callback interface used for notifying UI that translation happens.
   * @return number of scheduled localization strings extracting
   */
  private static int translateTo(@NonNull final View v, @NonNull final Locale l, @Nullable final Tongue.Callback callback) {
    int counter = 0;

    final List<Pair<View, List<ResourceString>>> found = resources(views(v));

    for (final Pair<View, List<ResourceString>> p : found) {
      counter += schedule(p.first, p.second, l, callback);
    }

    IN_PROGRESS.addAndGet(counter);

    return counter;
  }

  /**
   * Find all views from specified root.
   *
   * @param v the instance of root view
   * @return the list of extracted views
   */
  @NonNull
  private static List<View> views(@NonNull final View v) {
    final List<View> results = new ArrayList<>();
    final Stack<View> stack = new Stack<>();
    stack.push(v);

    View next;

    while (null != (next = stack.pop())) {
      if (next instanceof ViewGroup) {
        final int len = ((ViewGroup) v).getChildCount();

        for (int i = 0; i < len; i++) {
          final View nextChild = ((ViewGroup) v).getChildAt(i);

          stack.push(nextChild);
        }
      } else {
        results.add(next);
      }
    }

    return results;
  }

  /**
   * Extract resources for list of views.
   *
   * @param views list of views for processing.
   */
  @NonNull
  private static List<Pair<View, List<ResourceString>>> resources(@NonNull final List<View> views) {
    final List<Pair<View, List<ResourceString>>> results = new ArrayList<>();

    for (final View v : views) {
      final List<ResourceString> resources = ResourcesExtractor.extract(v);

      if (!resources.isEmpty()) {
        results.add(new Pair<>(v, resources));
      }
    }

    return results;
  }

  /**
   * Schedule extraction of translated string from cache or web api.
   *
   * @param view instance of view that request localization
   * @param resources resource strings associated with view
   * @param lang destination language
   * @param callback callback interface
   */
  private static int schedule(@NonNull final View view,
                              @NonNull final List<ResourceString> resources,
                              @NonNull final Locale lang,
                              @Nullable final Callback callback) {
    int counter = 0;

    for (final ResourceString r : resources) {
      final Localization.TaskContext c = Localization.context(view, r, lang, callback);

      // NOTE: factory iterate over registered localization providers and if possible -
      // extract translation. If extraction is done from cache, than provider raise
      // Callback.onSuccess immediately, otherwise its queued for background processing
      // and will be raised from a background thread.
      final Localization.Task task = Localization.create(c);

      if (null != task && !task.isComplete()) {
        counter++; // increase counter
      }
    }

    return counter;
  }

	/* [ NESTED DECLARATIONS ] ======================================================================================= */

  /** Callback interface that allows to monitor progress of the UI translation. */
  public interface Callback {
    void onSuccess(final View v, final ResourceString old, final ResourceString $new);

    void onFailure(final View v, final ResourceString value);
  }
}
