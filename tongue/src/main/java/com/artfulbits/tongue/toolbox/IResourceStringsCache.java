package com.artfulbits.tongue.toolbox;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artfulbits.tongue.ResourceString;

/** Basic implementation of localization cache. */
public interface IResourceStringsCache {

  /** Check that requested resource string exists in cache. */
  boolean contains(@NonNull final ResourceString resource);

  /**
   * Get resource string by provided reference on original resource.
   *
   * @param key the key resource
   * @return the found resource string otherwise {@code null}.
   */
  @Nullable
  ResourceString get(@NonNull final ResourceString key);

  /**
   * Put localized resource into cache for future use.
   *
   * @param key the key, original resource
   * @param value the value, translated version.
   */
  void put(@NonNull final ResourceString key, @NonNull final ResourceString value);
}
