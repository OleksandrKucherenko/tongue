package com.artfulbits.tongue;

/** Resource string that knows own value, resource id and language. */
public final class ResourceString {
  public final int id;
  public final String value;
  public final Language language;

  public ResourceString(final int id, final Language language) {
    this(id, null, language);
  }

  public ResourceString(final String value, final Language language) {
    this(-1, value, language);
  }

  public ResourceString(final int id, final String value, final Language language) {
    this.id = id;
    this.language = language;
    this.value = value;
  }
}
