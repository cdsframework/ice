package org.opencds.vmr.v1_0.mappings.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

public class TSDateFormatTest
{
  private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

  @Test
  public void testWithoutOffset() throws ParseException
  {
    String input = "2021";
    Date expectedDate = sdf.parse("2021-01-01 00:00:00.000+0000");
    testInput(input, "yyyy", expectedDate);

    input = "20219";
    expectedDate = sdf.parse("2021-09-01 00:00:00.000+0000");
    testInput(input, "yyyyM", expectedDate);

    input = "202109";
    expectedDate = sdf.parse("2021-09-01 00:00:00.000+0000");
    testInput(input, "yyyyMM", expectedDate);

    input = "2021091";
    expectedDate = sdf.parse("2021-09-01 00:00:00.000+0000");
    testInput(input, "yyyyMMd", expectedDate);

    input = "20210912";
    expectedDate = sdf.parse("2021-09-12 00:00:00.000+0000");
    testInput(input, "yyyyMMdd", expectedDate);

    input = "202109122";
    expectedDate = sdf.parse("2021-09-12 02:00:00.000+0000");
    testInput(input, "yyyyMMddH", expectedDate);

    input = "2021091220";
    expectedDate = sdf.parse("2021-09-12 20:00:00.000+0000");
    testInput(input, "yyyyMMddHH", expectedDate);

    input = "20210912205";
    expectedDate = sdf.parse("2021-09-12 20:05:00.000+0000");
    testInput(input, "yyyyMMddHHm", expectedDate);

    input = "202109122056";
    expectedDate = sdf.parse("2021-09-12 20:56:00.000+0000");
    testInput(input, "yyyyMMddHHmm", expectedDate);

    input = "2021091220569";
    expectedDate = sdf.parse("2021-09-12 20:56:09.000+0000");
    testInput(input, "yyyyMMddHHmms", expectedDate);

    input = "20210912205635";
    expectedDate = sdf.parse("2021-09-12 20:56:35.000+0000");
    testInput(input, "yyyyMMddHHmmss", expectedDate);

    input = "20210912205635.1";
    expectedDate = sdf.parse("2021-09-12 20:56:35.001+0000");
    testInput(input, "yyyyMMddHHmmss.S", expectedDate);

    input = "20210912205635.12";
    expectedDate = sdf.parse("2021-09-12 20:56:35.012+0000");
    testInput(input, "yyyyMMddHHmmss.SS", expectedDate);

    input = "20210912205635.123";
    expectedDate = sdf.parse("2021-09-12 20:56:35.123+0000");
    testInput(input, "yyyyMMddHHmmss.SSS", expectedDate);
  }

  @Test
  public void testWithOffset() throws ParseException
  {
    String input = "2021+0000";
    Date expectedDate = sdf.parse("2021-01-01 00:00:00.000+0000");
    testInput(input, "yyyyZZZZZ", expectedDate);

    input = "20219+0000";
    expectedDate = sdf.parse("2021-09-01 00:00:00.000+0000");
    testInput(input, "yyyyMZZZZZ", expectedDate);

    input = "202109+0000";
    expectedDate = sdf.parse("2021-09-01 00:00:00.000+0000");
    testInput(input, "yyyyMMZZZZZ", expectedDate);

    input = "2021091+0000";
    expectedDate = sdf.parse("2021-09-01 00:00:00.000+0000");
    testInput(input, "yyyyMMdZZZZZ", expectedDate);

    input = "20210912+0000";
    expectedDate = sdf.parse("2021-09-12 00:00:00.000+0000");
    testInput(input, "yyyyMMddZZZZZ", expectedDate);

    input = "202109122+0000";
    expectedDate = sdf.parse("2021-09-12 02:00:00.000+0000");
    testInput(input, "yyyyMMddHZZZZZ", expectedDate);

    input = "2021091220+0000";
    expectedDate = sdf.parse("2021-09-12 20:00:00.000+0000");
    testInput(input, "yyyyMMddHHZZZZZ", expectedDate);

    input = "20210912205+0000";
    expectedDate = sdf.parse("2021-09-12 20:05:00.000+0000");
    testInput(input, "yyyyMMddHHmZZZZZ", expectedDate);

    input = "202109122056+0000";
    expectedDate = sdf.parse("2021-09-12 20:56:00.000+0000");
    testInput(input, "yyyyMMddHHmmZZZZZ", expectedDate);

    input = "2021091220569+0000";
    expectedDate = sdf.parse("2021-09-12 20:56:09.000+0000");
    testInput(input, "yyyyMMddHHmmsZZZZZ", expectedDate);

    input = "20210912205635+0000";
    expectedDate = sdf.parse("2021-09-12 20:56:35.000+0000");
    testInput(input, "yyyyMMddHHmmssZZZZZ", expectedDate);

    input = "20210912205635.1+0000";
    expectedDate = sdf.parse("2021-09-12 20:56:35.001+0000");
    testInput(input, "yyyyMMddHHmmss.SZZZZZ", expectedDate);

    input = "20210912205635.12+0000";
    expectedDate = sdf.parse("2021-09-12 20:56:35.012+0000");
    testInput(input, "yyyyMMddHHmmss.SSZZZZZ", expectedDate);

    input = "20210912205635.123+0000";
    expectedDate = sdf.parse("2021-09-12 20:56:35.123+0000");
    testInput(input, "yyyyMMddHHmmss.SSSZZZZZ", expectedDate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidFormat()
  {
    TSDateFormat.forInput("202109122056351");
  }

  private void testInput(String input, String expectedPattern, Date expectedDate) throws ParseException
  {
    String format = TSDateFormat.forInput(input);
    Assert.assertEquals(expectedPattern, format);
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    Assert.assertEquals(expectedDate, sdf.parse(input));
  }
}
