package com.artfulbits.tongue.toolbox;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artfulbits.tongue.ResourceString;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Cache localized strings in memory. */
public class MemoryCache {
  /* [ CONSTANTS ] ================================================================================================= */

  /** All instances of localization cache's. */
  private final static Map<Locale, MemoryCache> sCaches = new HashMap<>();

	/* [ MEMBERS ] =================================================================================================== */

  /** Language associated with this Cache instance. */
  public final Locale Language;

	/* [ CONSTRUCTORS ] ============================================================================================== */

  public MemoryCache(@NonNull final Locale lang) {
    Language = lang;
  }

	/* [ STATIC METHODS ] ============================================================================================ */

  /**
   * Gets cache for specified language.
   *
   * @param lang the language
   * @return the cache instance
   */
  @NonNull
  public static MemoryCache getCache(@NonNull final Locale lang) {
    if (!sCaches.containsKey(lang)) {
      synchronized (sCaches) {
        if (!sCaches.containsKey(lang)) {
          // create instance of cache if thread safe manner
          sCaches.put(lang, new MemoryCache(lang));
        }
      }
    }

    return sCaches.get(lang);
  }

	/* [ IMPLEMENTATION & HELPERS ] ================================================================================== */

  /** Check that requested resource string exists in cache. */
  public boolean contains(@NonNull final ResourceString resource) {
    // TODO: check embedded localization resources

    // TODO: check own disk cache

    return false;
  }

  @Nullable
  public ResourceString get(@NonNull final ResourceString r) {
    return null;
  }

  public void put(@NonNull final ResourceString key, @NonNull final ResourceString value) {
    // TODO: store value in cache
  }
}


