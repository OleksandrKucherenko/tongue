package com.artfulbits.tongue.web;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Locale;

/** Extract string resources from embedded into application assets. */
public final class EmbedResources implements Localization.Provider {

  private final Context mContext;

  public EmbedResources(@NonNull final Context context) {
    mContext = context;
  }

  @Override
  public float priority() {
    return 0.0f;
  }

  @Override
  public boolean supported(@NonNull Locale language) {
    // TODO: try to detect that in resources exists embedded localized string

    return false;
  }

  @Override
  public Localization.Task localize(@NonNull Localization.TaskContext context) {
    return null;
  }
}
