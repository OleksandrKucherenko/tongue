package com.artfulbits.tongue.toolbox;

import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;

import com.artfulbits.tongue.ResourceString;
import com.artfulbits.tongue.Tongue;
import com.artfulbits.tongue.extractors.ResourcesExtractor;

/** Handler that knows how to update controls from main thread. */
public class ViewsUpdater implements Tongue.Callback, Handler.Callback {
  /** Unique message identifier. */
  private static final int MSG_UPDATE = -1;
  /** Handler that process messages in main thread. */
  private final Handler mHandler = new Handler(this);

  @Override
  public void onSuccess(final View v, final ResourceString old, final ResourceString $new) {
    final ResourceString composed = new ResourceString(old.type, $new.id, $new.value, $new.language);
    final Pair<View, ResourceString> args = Pair.create(v, composed);
    final Message message = mHandler.obtainMessage(MSG_UPDATE, args);

    mHandler.sendMessage(message);
  }

  @Override
  public void onFailure(final View v, final ResourceString value) {
    // do nothing
  }

  @Override
  public boolean handleMessage(final Message msg) {
    if (MSG_UPDATE == msg.what) {
      final Pair<View, ResourceString> p = (Pair<View, ResourceString>) msg.obj;

      ResourcesExtractor.update(p.first, p.second);

      return true;
    }

    return false;
  }
}
