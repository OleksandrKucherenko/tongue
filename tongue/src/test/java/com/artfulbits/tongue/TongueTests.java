package com.artfulbits.tongue;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.artfulbits.benchmark.Meter;
import com.artfulbits.tongue.web.CacheProvider;
import com.artfulbits.tongue.web.Language;

import org.junit.*;
import org.junit.rules.*;
import org.mockito.Mockito;

import java.security.InvalidParameterException;
import java.util.logging.Level;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/** jUnit tests. */
@SuppressWarnings("PMD")
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
  public void test_01_WrongInitialization() {
    Tongue.getCache();

    fail("Disk cache should not be available until the Tongue.initialize() is called.");
  }

  @Test
  public void test_02_NormalInitialization() throws Exception {
    final Context context = new android.test.mock.MockContext() {
      @Override
      public String getPackageName() {
        return "com.test";
      }

      @Override
      public PackageManager getPackageManager() {
        final PackageManager pm = Mockito.mock(PackageManager.class);
        final PackageInfo pi = Mockito.mock(PackageInfo.class);

        try {
          when(pm.getPackageInfo(anyString(), anyInt())).thenReturn(pi);
        } catch (final PackageManager.NameNotFoundException ignored) {

        }
        return pm;
      }
    };

    Tongue.initialize(context);
    final CacheProvider cache = Tongue.getCache();

    assertThat(cache, notNullValue());
  }
}
