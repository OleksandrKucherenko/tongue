package com.artfulbits.tongue;

import java.util.Locale;

/** Resource string that knows own value, resource id and language. */
public final class ResourceString {
  /* [ CONSTANTS ] ================================================================================================= */

  /** Use this constant if resource string does not have unique resource identifier. */
  public static final int NO_ID = -1;
  /** Empty instance. Use it instead of NULL value. */
  public static final ResourceString EMPTY = new ResourceString(Types.Translation, NO_ID, null, null);

  /** Known types. */
  public enum Types {
    /**  */
    Text,
    /**  */
    Hint,
    /**  */
    Description,
    /**  */
    Translation
  }

	/* [ MEMBERS ] =================================================================================================== */

  /** Unique resource id. */
  public final int id;
  /** Value of the extracted string resource. */
  public final String value;
  /** The language of the string resource. */
  public final Locale language;
  /** type of the resource. */
  public final Types type;

	/* [ CONSTRUCTORS ] ============================================================================================== */

  /**
   * Instantiates a new Resource string.
   *
   * @param id the resource id
   * @param value the extracted resource value
   * @param language the language to which belong string.
   */
  public ResourceString(final Types type, final int id, final String value, final Locale language) {
    this.type = type;
    this.id = id;
    this.language = language;
    this.value = value;
  }

  public static ResourceString translation(final int id, final String value, final Locale language) {
    return new ResourceString(Types.Translation, id, value, language);
  }

  public static ResourceString text(final int id, final String value, final Locale language) {
    return new ResourceString(Types.Text, id, value, language);
  }

  public static ResourceString hint(final int id, final String value, final Locale language) {
    return new ResourceString(Types.Hint, id, value, language);
  }

  public static ResourceString description(final int id, final String value, final Locale language) {
    return new ResourceString(Types.Description, id, value, language);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;

    if (!(o instanceof ResourceString)) return false;

    final ResourceString r = (ResourceString) o;

    // compare field by field
    return (this.id == r.id &&
        this.language == r.language &&
        (null == this.value ? null == r.value : this.value.equals(r.value))
    );
  }
}
