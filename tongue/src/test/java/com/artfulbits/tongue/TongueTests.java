package com.artfulbits.tongue;

import android.content.Context;

import com.artfulbits.benchmark.Meter;
import com.artfulbits.tongue.web.CacheProvider;
import com.artfulbits.tongue.web.Language;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.security.InvalidParameterException;
import java.util.logging.Level;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/** jUnit tests. */
@SuppressWarnings("PMD")
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 21
, reportSdk = 18
, manifest = "./src/test/AndroidManifest.xml"
, constants = BuildConfig.class)
public class TongueTests {

  /* [ MEMBERS ] =================================================================================================== */

  private Meter.Output mOutput;

  /* [ INJECTIONS ] ================================================================================================ */
  @Rule
  public TestName mTestName = new TestName();

  /* [ IMPLEMENTATION & HELPERS ] ================================================================================== */

  //region Setup and TearDown
  @BeforeClass
  public static void setUpClass() {
  }

  @Before
  public void setUp() {
    mOutput = new Meter.Output() {
      private StringBuilder mLog = new StringBuilder(64 * 1024).append("\r\n");

      @Override
      public void log(final Level level, final String tag, final String msg) {
        mLog.append(level.toString().charAt(0)).append(" : ")
            .append(tag).append(" : ")
            .append(msg).append("\r\n");
      }

      @Override
      public String toString() {
        return mLog.toString();
      }
    };

    mOutput.log(Level.INFO, "->", mTestName.getMethodName());
  }

  @After
  public void tearDown() {
    mOutput.log(Level.INFO, "<-", mTestName.getMethodName());
    System.out.append(mOutput.toString());
  }

  @AfterClass
  public static void tearDownClass() {
    // do nothing for now
  }

  //endregion

  /* [ TESTS ] ===================================================================================================== */

  @Test(expected = InvalidParameterException.class)
  public void test_00_WrongTranslationLanguage() {
    Tongue.translateTo(null, Language.Unknown.toLocale());

    fail("API should not allow calls with Language.Unknown language parameter.");
  }

  @Test(expected = IllegalStateException.class)
  @Ignore
  public void test_01_WrongInitialization() {
    Tongue.getCache();

    fail("Disk cache should not be available until the Tongue.initialize() is called.");
  }

  @Test
  public void test_02_NormalInitialization() throws Exception {
    final Context context = RuntimeEnvironment.application;

    Tongue.initialize(context);
    final CacheProvider cache = Tongue.getCache();

    assertThat(cache, notNullValue());
  }
}
