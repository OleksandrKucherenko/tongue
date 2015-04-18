package com.artfulbits.tongue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.security.InvalidParameterException;

/** Major API. Allows to convert any string resource into it translated version. */
public final class Tongue {

  /** hidden constructor. */
  private Tongue() {
    throw new AssertionError("hidden constructor. Not designed for inheritance.");
  }

  /**
   * Initialize context by custom inflater that tracks all resources for localization.
   *
   * @param context the context
   */
  public static void initialize(@NonNull final Context context) {
    TongueFactory.initialize(context);
  }

  /**
   * Ask library to start translation of the UI in background process.
   *
   * @param v root view for translation
   * @param lang concrete destination language.
   */
  public static void translateTo(@NonNull final View v, @NonNull final Language lang) {
    if (Language.Unknown == lang) {
      throw new InvalidParameterException("Should be specified concrete language. Language.Unknown is not allowed.");
    }

    translateTo(v, lang, null);
  }

  /**
   * Ask library to start translation of the UI in background process.
   *
   * @param v root view for translation
   * @param lang concrete destination language.
   * @param callback callback interface used for notifying UI that translation happens.
   */
  public static void translateTo(@NonNull final View v, @NonNull final Language lang, @Nullable final Callback callback) {
    if (Language.Unknown == lang) throw new AssertionError("Should be specified concrete language.");

    // TODO: implement me

  }

  /** Callback interface that allows to monitor progress of the UI translation. */
  public interface Callback {
    void onSuccess(final View v, final ResourceString old, final ResourceString $new);

    void onFailure(final View v, final ResourceString value);
  }
}
