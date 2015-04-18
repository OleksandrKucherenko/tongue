package com.artfulbits.tongue;

import com.artfulbits.benchmark.Meter;
import com.artfulbits.tongue.web.Language;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.security.InvalidParameterException;
import java.util.logging.Level;

import static org.junit.Assert.fail;

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
    Tongue.translateTo(null, Language.Afrikaans);

    Tongue.translateTo(null, Language.Unknown);

    fail("API should not allow calls with Language.Unknown language parameter.");
  }

}
