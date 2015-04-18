package com.artfulbits.tongue.web;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.artfulbits.tongue.ResourceString;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidParameterException;
import java.util.Locale;

/**
 * Implementation of the translation over the Google Translate API call via Volley library.
 * <p>
 * Don't forget to configure application manifest file:
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot;?&gt;
 * &lt;manifest ... &gt;
 *   &lt;application ... &gt;
 *     &lt;meta-data android:name=&quot;com.google.translate.api.key&quot; android:value=&quot;...&quot; /&gt;
 *   &lt;/application&gt;
 * &lt;/manifest&gt;
 * </pre>
 *
 * @see <a href="https://cloud.google.com/translate/v2/using_rest">Google Translate API</a>
 */
public final class GoogleTranslateVolley implements Localization.Provider {
  /* [ CONSTANTS ] ================================================================================================= */

  public static final String GOOGLE_TRANSLATE_API_KEY = "com.google.translate.api.key";

  /** Default encoding to use. */
  private static final String ENCODING = "UTF-8";
  /** If string is bigger 5Kb we should use POST call. */
  private static final int GET_MAX = 5 * 1024;
  /** Google Translate API basic URL. */
  private static final String URL_TEMPLATE = "https://www.googleapis.com/language/translate/v2?key=%s";
  /** Query parameters. Example: &source=en&target=de&q=Hello%20Word */
  private static final String URL_QUERY = "&source=%s&target=%s&q=%s";

	/* [ STATIC MEMBERS ] ============================================================================================ */

  private static RequestQueue sQueue;

	/* [ MEMBERS ] =================================================================================================== */

  private final String mApiKey;

	/* [ CONSTRUCTORS ] ============================================================================================== */

  public GoogleTranslateVolley(@NonNull final Context context) {
    // create queue
    if (null == sQueue) {
      synchronized (GoogleTranslateVolley.class) {
        if (null == sQueue) {
          sQueue = Volley.newRequestQueue(context);
          sQueue.start();
        }
      }
    }

    mApiKey = getApiKey(context);

    if (TextUtils.isEmpty(mApiKey)) {
      throw new InvalidParameterException("In AndroidManifest.xml file should be declared Google Translate API key.");
    }
  }

	/* [ STATIC METHODS ] ============================================================================================ */

  /**
   * Gets api key for Google Translate from application metadata.
   *
   * @param context the context
   * @return the api key
   */
  public static String getApiKey(@NonNull final Context context) {
    try {
      final PackageManager manager = context.getPackageManager();
      final String packageName = context.getPackageName();
      final ApplicationInfo ai = manager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
      final Bundle bundle = ai.metaData;

      return bundle.getString(GOOGLE_TRANSLATE_API_KEY);
    } catch (final Throwable ignored) {
      VolleyLog.e(ignored, "Cannot extract google translate api key from AndroidManifest.xml");
    }

    return "";
  }

  private String url(@NonNull final String apiKey, @NonNull final Localization.TaskContext context) {
    try {
      final Language langSrc = Language.fromLocale(context.resource.language);
      final Language langDest = Language.fromLocale(context.language);

      return String.format(Locale.US, URL_TEMPLATE, apiKey) +
          String.format(Locale.US, URL_QUERY,
              langSrc.code, langDest.code,
              URLEncoder.encode(context.resource.value, ENCODING));

    } catch (final UnsupportedEncodingException ignored) {
      VolleyLog.wtf(ignored, "System failure!");

      // rethrow, this is FATAL
      throw new RuntimeException(ignored);
    }
  }

	/* [ Interface Provider ] ======================================================================================== */

  /** {@inheritDoc} */
  @Override
  public float priority() {
    return 100.0f;
  }

  /** {@inheritDoc} */
  @Override
  public boolean supported(@NonNull final Locale language) {
    try {
      return null != Language.fromLocale(language);
    } catch (final Throwable ignored) {
      // unsupported by google language
    }

    return false;
  }

  /** {@inheritDoc} */
  @NonNull
  @Override
  public Localization.Task localize(@NonNull final Localization.TaskContext context) {
    // TODO: implement POST calls
    if (context.resource.value.length() >= GET_MAX) {
      throw new InvalidParameterException("Strings longer 5Kb are not supported right now.");
    }

    final String urlGet = url(mApiKey, context);
    final TranslateGetCall request = TranslateGetCall.newInstance(urlGet, context);

    sQueue.add(request);

    return request;
  }

  /* [ NESTED DECLARATIONS ] ======================================================================================= */

  private final static class TranslateGetCall extends JsonObjectRequest implements Localization.Task {
    private Localization.TaskContext mContext;
    private boolean mIsComplete;
    private ResourceString mResult = ResourceString.EMPTY;

    private TranslateGetCall(@NonNull final String url, @NonNull final Listeners listeners) {
      super(Method.GET, url, listeners, listeners);

      listeners.setTarget(this);
    }

    public static TranslateGetCall newInstance(@NonNull final String url, @NonNull final Localization.TaskContext ctx) {
      final Listeners listeners = new Listeners();

      return new TranslateGetCall(url, listeners).withContext(ctx);
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public Localization.TaskContext getContext() {
      return mContext;
    }

    @Override
    public ResourceString getResult() {
      return mResult;
    }

    @Override
    public boolean isComplete() {
      return mIsComplete;
    }

    /**
     * Raise on successful network response.
     *
     * @param response extracted JSON response.
     */
    /* package */ void onSuccess(@NonNull final JSONObject response) {
      mIsComplete = true;

      // TODO: convert JSON!
      final String value = "";
      mResult = ResourceString.translation(mContext.resource.id, value, mContext.language);

      // raise callback if its still actual
      if (null != mContext.callback) {
        final View view = mContext.getView();

        // only if view exists and we have callback reference do the call
        if (null != view) {

          mContext.callback.onSuccess(view, mContext.resource, mResult);
        }
      }
    }

    /**
     * Raise on Network error.
     *
     * @param error the reason of error.
     */
    /* package */ void onError(@NonNull final VolleyError error) {
      mIsComplete = true;

      if (null != mContext.callback) {
        final View view = mContext.getView();

        // only if view exists and we have callback reference do the call
        if (null != view) {
          mContext.callback.onFailure(view, mContext.resource);
        }
      }
    }

    /* package */ TranslateGetCall withContext(@NonNull final Localization.TaskContext context) {
      mContext = context;
      return this;
    }
  }

  /** Dummy delegation. Implements redirection of success and failure methods to {@see TranslateGetCall} instance. */
  private final static class Listeners implements Response.Listener<JSONObject>, Response.ErrorListener {
    /** target of delegation. */
    private TranslateGetCall mTarget;

    @Override
    public final void onErrorResponse(final VolleyError error) {
      if (null != mTarget) {
        mTarget.onError(error);
      }
    }

    @Override
    public final void onResponse(final JSONObject response) {
      if (null != mTarget) {
        mTarget.onSuccess(response);
      }
    }

    /** Assign delegation target. */
    /* package */ void setTarget(@NonNull final TranslateGetCall redirection) {
      mTarget = redirection;
    }
  }
}
