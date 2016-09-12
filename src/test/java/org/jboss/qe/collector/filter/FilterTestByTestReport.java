package org.jboss.qe.collector.filter;

import org.jboss.qe.collector.CachePrepare;
import org.jboss.qe.collector.Main;
import org.jboss.qe.collector.filter.testFilters.TestFilterTestReport;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

/**
 *  Created by fjerabek on 12.9.16.
 */
public class FilterTestByTestReport {
   private PrintStream old = System.out;
   private ByteArrayOutputStream baos;

   @Before
   public void before() {
      baos = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(baos);
      System.setOut(ps);
   }

   @Test
   public void testReportFilterTest() {
      String[] resources = new String[]{"failedJob/lastBuild.json","failedJob/testReport.json"};
      String testName = "eap-70x-maven-repository-check-valid-POM-and-Metadata-files";
      Main.filter = new TestFilterTestReport();

      CachePrepare.prepare(testName,resources, getClass());

      Main.main(new String[]{testName});

      String output = baos.toString();

      String[] split = output.split("\n");

      Pattern pattern = Pattern.compile("\u001B\\[34m");
      Assert.assertTrue("Test filter does not work", split[15].contains("Test filter by test report"));
      Assert.assertTrue("Test filter does not work", pattern.matcher(split[15]).find());

      Assert.assertFalse("Test filter does not work", split[16].contains("Test filter by test report"));
      Assert.assertFalse("Test filter does not work", pattern.matcher(split[16]).find());


   }

   @After
   public void cleanup() {
      System.out.flush();
      System.setOut(old);
   }
}
