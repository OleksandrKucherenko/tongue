package com.artfulbits.tongue;

import com.artfulbits.benchmark.Meter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.security.InvalidParameterException;
import java.util.logging.Level;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/** Language API testing. */
public class LanguageTests {

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

  @Test
  public void test_00_byCode() {
    final Language en = Language.byCode("en");
    final Language english = Language.byCode("eN");

    // validation
    assertThat(Language.English, equalTo(en));
    assertThat(english, equalTo(en));
    assertThat("English", equalTo(en.englishName));

    final Language zhCn = Language.byCode("zh-cn");
    mOutput.log(Level.INFO, "--", "name: '" + zhCn.englishName + "' toString: " + zhCn.toString());

    // test that in names underscore char replaced by space
    assertThat("Chinese Simplified", equalTo(zhCn.englishName));
  }

  @Test(expected = InvalidParameterException.class)
  public void test_01_byCode_WrongParams() {
    final Language xxx = Language.byCode("xxx");

    fail("Expected exception on wrong ISO code.");
  }
}
