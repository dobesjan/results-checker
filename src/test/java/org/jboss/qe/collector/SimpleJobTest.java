package org.jboss.qe.collector;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by fjerabek on 8.9.16.
 */

public class SimpleJobTest {
   private String filename = "test";
   private String data = "This is testing string";
   private ByteArrayOutputStream baos;
   private PrintStream old;
   @Test
   public void cacheMayCreateFile() {
      Cache cache = new Cache(filename);
      cache.add(data);
      Assert.assertTrue(cache.exist());
   }

   @Test
   public void cacheMayWriteTheSameAsRead() {
      Cache cache = new Cache(filename);
      cache.add(data);
      String read = cache.getAll();
      Assert.assertEquals(data,read);
   }

   @Test
   public void cacheMayBeActual() {
      Cache cache = new Cache(filename);
      cache.add("Actual");
      Assert.assertTrue(cache.isActual(1));
   }
   @Before
   public void prepare() {
      baos = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(baos);
      old = System.out;
      System.setOut(ps);
   }

   @Test
   public void testBasicFunctionality() {
      String test = "eap-70x-maven-repository-check-valid-POM-and-Metadata-files";


      Path cashPath = Paths.get(System.getProperty("java.io.tmpdir"),test);
      try {
         FileOutputStream os = new FileOutputStream(cashPath.toString());
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }

      new Main().main(new String[]{test});
      String output = baos.toString();
      String[] split = output.split("\n");
      for (int i = 0; i < split.length; i++) {
         split[i] = split[i].replaceAll("\u001B\\[[;\\d]*m", "");
      }
      Assert.assertEquals("There is possibly filter in use", " - no filter in use", split[1]);
      Assert.assertEquals("Legend was not shown", " - POSSIBLE REGRESSION", split[4]);
      Assert.assertEquals("Legend was not shown", " - KNOWN ISSUE", split[5]);
      Assert.assertEquals("Legend was not shown", " - ENVIRONMENT ISSUES AND OTHERS WITHOUT BZ/JIRA", split[6]);
      Assert.assertEquals("Wrong name of job shown", " - " + test, split[9]);
      Assert.assertEquals("Wrong name of job shown", test, split[11]);
      Assert.assertEquals("Wrong URL", "https://jenkins.mw.lab.eng.bos.redhat.com/hudson/job/eap-70x-maven-repository-check-valid-POM-and-Metadata-files/9/", split[12]);
      Assert.assertEquals("Bad print of test status", " - PASSED: 308, FAILED: 2, SKIPPED: 0", split[13]);
      Assert.assertEquals("Error was not shown", " - infinispan-directory-provider-8.1.4.Final-redhat-1#infinispan-directory-provider-8.1.4.Final-redhat-1", split[14]);
      Assert.assertEquals("Error was not shown", " - infinispan-parent-8.1.4.Final-redhat-1#infinispan-parent-8.1.4.Final-redhat-1", split[15]);

   }

   @After
   public void cleanup() {
      System.out.flush();
      System.setOut(old);
   }
}