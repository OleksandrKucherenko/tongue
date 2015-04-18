package com.artfulbits.tongue.toolbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.Locale;

/** Helper that allows to identify Locale of the application in runtime. */
public class LocaleHelper {
  /* [ CONSTANTS ] ================================================================================================= */

  private static final String DEFAULT_LIB_SETTINGS = "lib-tongue-default";
  private static final String DEFAULT_LANG = "lib-tongue-default-language";
  private static final String DEFAULT_COUNTRY = "lib-tongue-default-country";
  private static final String DEFAULT_VARIANT = "lib-tongue-default-variant";

	/* [ MEMBERS ] =================================================================================================== */

  private final Context mContext;
  private final SharedPreferences mStorage;

	/* [ CONSTRUCTORS ] ============================================================================================== */

  public LocaleHelper(@NonNull final Context context) {
    this(context, context.getSharedPreferences(DEFAULT_LIB_SETTINGS, Context.MODE_PRIVATE));
  }

  public LocaleHelper(@NonNull final Context context, @NonNull final SharedPreferences storage) {
    mContext = context;
    mStorage = storage;
  }

	/* [ STATIC METHODS ] ============================================================================================ */

  /**
   * Is currently active locale is RTL.
   *
   * @return {@code true} - Right to left locale, otherwise false.
   */
  public static boolean isRTL(@NonNull final Locale locale) {
    boolean result = false;

    final String displayName = locale.getDisplayName(locale);

    if (!TextUtils.isEmpty(displayName)) {
      final char c = displayName.charAt(0);

      result = (Character.getDirectionality(c) == Character.DIRECTIONALITY_RIGHT_TO_LEFT);
    }

    return result;
  }

	/* [ GETTER / SETTER METHODS ] =================================================================================== */

  /**
   * Get locale that is currently stored in application settings.
   *
   * @param context application context.
   * @return extracted locale. Always a new instance. Not null.
   */
  public Locale getRuntimeLocale() {
    final String language = getSettings().getString(DEFAULT_LANG, "");
    final Locale locale;

    // if language is not set yet, then return thread default
    if (TextUtils.isEmpty(language)) {
      locale = Locale.getDefault();
    } else {
      final String country = getSettings().getString(DEFAULT_COUNTRY, "");
      final String variant = getSettings().getString(DEFAULT_VARIANT, "");

      locale = new Locale(language, country, variant);
    }

    return locale;
  }

  public void setRuntimeLocale(@NonNull final Locale locale) {
    final SharedPreferences.Editor edit = getSettings().edit();
    edit.putString(DEFAULT_LANG, locale.getLanguage());
    edit.putString(DEFAULT_COUNTRY, locale.getCountry());
    edit.putString(DEFAULT_VARIANT, locale.getVariant());
    edit.apply();
  }

  @NonNull
  private SharedPreferences getSettings() {
    return mStorage;
  }

	/* [ IMPLEMENTATION & HELPERS ] ================================================================================== */

  /**
   * Get a Resources instance based on the provided locale. If the provided locale is unknown the default locale is
   * returned.
   *
   * @param context The application context
   * @param locale A Locale
   * @return A Locale specific Resources instance. Always a new instance.
   */
  public Resources getResourcesByLocale(@Nullable final Locale locale) {
    if (locale == null) {
      return mContext.getResources();
    }

    final Resources resources = mContext.getResources();
    final AssetManager assets = resources.getAssets();
    final DisplayMetrics metrics = resources.getDisplayMetrics();

    // get configuration and update it locale settings
    final Configuration config = new Configuration(resources.getConfiguration());
    config.locale = locale;

    // get new instance of the resources.
    return new Resources(assets, metrics, config);
  }
}
