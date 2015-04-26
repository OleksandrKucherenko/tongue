package com.artfulbits.tongue.toolbox;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artfulbits.tongue.ResourceString;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Cache localized strings in memory. */
public class SharedPreferencesCache implements IResourceStringsCache {
  /* [ CONSTANTS ] ================================================================================================= */

  /** All instances of localization cache's. */
  private final static Map<Locale, IResourceStringsCache> sCaches = new HashMap<>();

	/* [ MEMBERS ] =================================================================================================== */

  /** Language associated with this Cache instance. */
  public final Locale Language;

  private final SharedPreferences mStorage;

	/* [ CONSTRUCTORS ] ============================================================================================== */

  public SharedPreferencesCache(@NonNull final Locale lang) {
    Language = lang;
    mStorage = null;
  }

	/* [ STATIC METHODS ] ============================================================================================ */

  /**
   * Gets cache for specified language.
   *
   * @param lang the language
   * @return the cache instance
   */
  @NonNull
  public static IResourceStringsCache getCache(@NonNull final Locale lang) {
    if (!sCaches.containsKey(lang)) {
      synchronized (sCaches) {
        if (!sCaches.containsKey(lang)) {
          // create instance of cache if thread safe manner
          sCaches.put(lang, new SharedPreferencesCache(lang));
        }
      }
    }

    return sCaches.get(lang);
  }

	/* [ IMPLEMENTATION & HELPERS ] ================================================================================== */

  /** {@inheritDoc} */
  @Override
  public boolean contains(@NonNull final ResourceString resource) {
    // TODO: check embedded localization resources

    // TODO: check own disk cache

    return false;
  }

  /** {@inheritDoc} */
  @Override
  @Nullable
  public ResourceString get(@NonNull final ResourceString key) {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void put(@NonNull final ResourceString key, @NonNull final ResourceString value) {
    // TODO: store value in cache
  }
}


