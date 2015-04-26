package com.artfulbits.tongue.web;

import android.support.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.Locale;

/**
 * Supported languages.
 *
 * @see <a href="https://cloud.google.com/translate/v2/using_rest#language-params">Google Translate API Language
 * Codes</a>
 */
public enum Language {
  /**  */
  Unknown("--"),
  /**  */
  Afrikaans("af"),
  /**  */
  Albanian("sq"),
  /**  */
  Arabic("ar"),
  /**  */
  Azerbaijani("az"),
  /**  */
  Basque("eu"),
  /**  */
  Bengali("bn"),
  /**  */
  Belarusian("be"),
  /**  */
  Bulgarian("bg"),
  /**  */
  Catalan("ca"),
  /**  */
  Chinese_Simplified("zh-CN"),
  /**  */
  Chinese_Traditional("zh-TW"),
  /**  */
  Croatian("hr"),
  /**  */
  Czech("cs"),
  /**  */
  Danish("da"),
  /**  */
  Dutch("nl"),
  /**  */
  English("en"),
  /**  */
  Esperanto("eo"),
  /**  */
  Estonian("et"),
  /**  */
  Filipino("tl"),
  /**  */
  Finnish("fi"),
  /**  */
  French("fr"),
  /**  */
  Galician("gl"),
  /**  */
  Georgian("ka"),
  /**  */
  German("de"),
  /**  */
  Greek("el"),
  /**  */
  Gujarati("gu"),
  /**  */
  Haitian_Creole("ht"),
  /**  */
  Hebrew("iw"),
  /**  */
  Hindi("hi"),
  /**  */
  Hungarian("hu"),
  /**  */
  Icelandic("is"),
  /**  */
  Indonesian("id"),
  /**  */
  Irish("ga"),
  /**  */
  Italian("it"),
  /**  */
  Japanese("ja"),
  /**  */
  Kannada("kn"),
  /**  */
  Korean("ko"),
  /**  */
  Latin("la"),
  /**  */
  Latvian("lv"),
  /**  */
  Lithuanian("lt"),
  /**  */
  Macedonian("mk"),
  /**  */
  Malay("ms"),
  /**  */
  Maltese("mt"),
  /**  */
  Norwegian("no"),
  /**  */
  Persian("fa"),
  /**  */
  Polish("pl"),
  /**  */
  Portuguese("pt"),
  /**  */
  Romanian("ro"),
  /**  */
  Russian("ru"),
  /**  */
  Serbian("sr"),
  /**  */
  Slovak("sk"),
  /**  */
  Slovenian("sl"),
  /**  */
  Spanish("es"),
  /**  */
  Swahili("sw"),
  /**  */
  Swedish("sv"),
  /**  */
  Tamil("ta"),
  /**  */
  Telugu("te"),
  /**  */
  Thai("th"),
  /**  */
  Turkish("tr"),
  /**  */
  Ukrainian("uk"),
  /**  */
  Urdu("ur"),
  /**  */
  Vietnamese("vi"),
  /**  */
  Welsh("cy"),
  /**  */
  Yiddish("yi");

  /** ISO code of the language. Always in lower case. */
  @NonNull
  public final String code;
  /** English name of the Language. Human readable format. */
  @NonNull
  public final String englishName;

  /** Construct enum with ISO code. */
  Language(@NonNull final String code) {
    this.code = code.toLowerCase(Locale.US);
    this.englishName = this.toString().replace('_', ' ');
  }

  /** Create locale instance from Language. */
  @NonNull
  public Locale toLocale() {
    if (this == Language.Unknown) {
      throw new InvalidParameterException("Unknown Language cannot have any corresponding Locale.");
    }

    return new Locale(code);
  }

  /**
   * Get Language by it code.
   *
   * @param code the code
   * @return the found language
   */
  public static Language byCode(@NonNull final String code) {
    final String normalize = code.toLowerCase(Locale.US);

    for (final Language lang : values()) {
      if (lang.code.equals(normalize)) {
        return lang;
      }
    }

    throw new InvalidParameterException("Unknown code. Code: " + code);
  }

  /**
   * Convert instance of locale to enum value.
   *
   * @param locale the locale instance
   * @return found language
   */
  public static Language fromLocale(@NonNull final Locale locale) {
    return byCode(locale.getCountry());
  }
}
