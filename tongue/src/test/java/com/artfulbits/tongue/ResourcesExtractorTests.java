package com.artfulbits.tongue;

import android.view.View;
import android.widget.TextView;

import com.artfulbits.tongue.extractors.ResourcesExtractor;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/** Test API of the Resource Extractor. */
@RunWith(RobolectricGradleTestRunner.class)
@Config(
    constants = com.artfulbits.tongue.BuildConfig.class,
    emulateSdk = 21,
    manifest = "./src/main/AndroidManifest.xml")
public class ResourcesExtractorTests {
  @Test
  public void test_00_Initialization() throws Exception {
    final View v = new View(RuntimeEnvironment.application);

    // first call, list should be empty
    List<ResourceString> values = ResourcesExtractor.extract(v);
    assertThat(values, hasSize(0));

    // compose mocked extractor
    final ResourcesExtractor.Extractor extractor = Mockito.mock(ResourcesExtractor.Extractor.class);
    when(extractor.supports(v)).thenReturn(true);
    when(extractor.extract(v)).thenReturn(ResourceString.text(-1, "mock", Locale.getDefault()));

    // attach our mock to main algorithm
    ResourcesExtractor.register(extractor);

    // try extraction
    values = ResourcesExtractor.extract(v);
    assertThat(values, hasSize(1));

    // remove mock
    ResourcesExtractor.unregister(extractor);

    // results should be again empty
    values = ResourcesExtractor.extract(v);
    assertThat(values, hasSize(0));
  }

  @Test
  public void test_01_ExtractFromTextView() {
    final TextView tv = new TextView(RuntimeEnvironment.application);

    // not initialized text view should provide no resources
    final List<ResourceString> values0 = ResourcesExtractor.extract(tv);
    assertThat(values0, hasSize(0));

    // text string resource available
    tv.setText("mock setText()");
    final List<ResourceString> values1 = ResourcesExtractor.extract(tv);
    assertThat(values1, hasSize(1));

    // text and hint string resources are available
    tv.setHint("mock setHint()");
    final List<ResourceString> values2 = ResourcesExtractor.extract(tv);
    assertThat(values2, hasSize(2));
  }
}
