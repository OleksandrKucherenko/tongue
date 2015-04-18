package com.artfulbits.tongue.extractors;

import android.support.annotation.NonNull;
import android.view.View;

import com.artfulbits.tongue.ResourceString;

import java.util.ArrayList;
import java.util.List;

/** Provider that knows how to extract specific resource string from instance of View. */
public final class ResourcesExtractor {
  /* [ CONSTANTS ] ================================================================================================= */

  /** List of registered providers. */
  private final static List<Extractor> sProviders = new ArrayList<>();

	/* [ STATIC METHODS ] ============================================================================================ */

  /** static constructor, register several most typical extractors. */
  static {
    register(TextExtractor.INSTANCE);
    register(HintExtractor.INSTANCE);
  }

	/* [ REGISTRATION ] ============================================================================================== */

  /**
   * Register extractor in resource provider.
   *
   * @param extractor the instance of extractor
   */
  public static void register(@NonNull final Extractor extractor) {
    sProviders.add(extractor);
  }

  /**
   * Unregister extractor in resource provider.
   *
   * @param provider the provider
   */
  public static void unregister(@NonNull final Extractor provider) {
    sProviders.remove(provider);
  }

	/* [ STATIC METHODS ] ============================================================================================ */

  /**
   * Extract list of resource strings that used by specified instance of view.
   *
   * @param v the instance of view
   * @return the list of extracted resource strings.
   */
  public static List<ResourceString> extract(@NonNull final View v) {
    final List<ResourceString> results = new ArrayList<>();

    for (final Extractor extractor : sProviders) {
      if (extractor.supports(v)) {
        results.add(extractor.extract(v));
      }
    }

    return results;
  }

  public static void update(@NonNull final View v, @NonNull final ResourceString r) {
    for (final Extractor extractor : sProviders) {
      if (extractor.supports(v)) {
        if (extractor.update(v, r)) {
          break;
        }
      }
    }
  }

	/* [ NESTED DECLARATIONS ] ======================================================================================= */

  /**
   * Inherit this interface for making possible resource string extracting. If view supports more than one resource
   * string, than implement a new specialized provider and {@see #register} it.
   */
  public interface Extractor {
    /**
     * Is provided view supported by this extractor or not.
     *
     * @param v the view instance to test
     * @return {@code true} - view type supported, otherwise false.
     */
    boolean supports(@NonNull final View v);

    /**
     * Extract resource string from provided instance.
     *
     * @param v the view instance
     * @return the resource string extracted from view.
     */
    @NonNull
    ResourceString extract(@NonNull final View v);

    /**
     * Update control by new resource string.
     *
     * @param v the view instance
     * @param r resource string instance
     * @return {@code true} - on successful update, otherwise {@code false}.
     */
    boolean update(@NonNull final View v, @NonNull final ResourceString r);
  }
}
