package com.artfulbits.tongue.extractors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.artfulbits.tongue.ResourceString;
import com.artfulbits.tongue.toolbox.LocaleHelper;

import java.util.Locale;

/** Extracts resource string from provided TextView. {@see TextView#getText} */
public final class TextExtractor implements ResourcesExtractor.Extractor {

  public final static TextExtractor INSTANCE = new TextExtractor();

  private TextExtractor() {
    // do nothing
  }

  /** {@inheritDoc} */
  @Override
  public boolean supports(@NonNull final View v) {
    return TextView.class.isInstance(v);
  }

  /** {@inheritDoc} */
  @NonNull
  @Override
  public ResourceString extract(@NonNull final View v) {
    final Context context = v.getContext(); // FIXME: can be NULL

    final Locale locale = new LocaleHelper(context).getRuntimeLocale();
    final TextView t = (TextView) v;
    final String value = t.getText().toString();

    return (TextUtils.isEmpty(value)) ? ResourceString.EMPTY :
        ResourceString.text(resolveId(v), value, locale);
  }

  @Override
  public boolean update(@NonNull View v, @NonNull ResourceString r) {
    // TODO: implement me

    return false;
  }

  private int resolveId(@NonNull final View v) {
    return ResourceString.NO_ID;
  }
}
