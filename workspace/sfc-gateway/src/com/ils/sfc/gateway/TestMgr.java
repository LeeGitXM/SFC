package com.ils.sfc.gateway;

import java.io.BufferedWriter;
import java.io.IOException;

import org.python.core.PyObject;

/** A class to support some simple testing assertions for SFCs */
public class TestMgr {
	private java.util.Map<String,TestInfo> testsByName = new java.util.HashMap<String,TestInfo>();
	private String reportFilePath = "c:/temp/sfcTestReport.txt";
	private java.text.DateFormat dateFormat = new java.text.SimpleDateFormat();
	
	static class TestInfo {
		private String name;  // name of the test
		private long startMillis; // time the test started
		private long endMillis; // time the test ended
		private boolean passed = true;  // did the test pass?
		private String message;   // usually the cause of the failure
		
		public TestInfo(String name) {
			this.name = name;
		}
		
		/** Start a test. */
		public void start() {
			startMillis = System.currentTimeMillis();
		}
		
		/** Get how long the test has been running (or ran, if it ended). */
		public long getElapsedMillis() {
			return isRunning() ? System.currentTimeMillis() - startMillis : endMillis - startMillis;
		}

		/** If the given flag is false, fail the test and record the given
		 *  message as the reason.
		 */
		public boolean assertTrue(boolean condition, String msg) {
			if(!condition) {
				fail("Assertion failed: " + msg);
				return false;
			}
			else {
				return true;
			}
		}

		/** If the given objects are not equal, fail the test and record the 
		 * inequality as the reason.
		 */
		public boolean assertEqual(PyObject expected, PyObject actual) {
			if(!expected.equals(actual)) {
				String msg = expected.toString() + " != " + actual.toString();
				fail("Assertion failed: " + msg);
				return false;
			}
			else {
				return true;
			}
		}

		/** Fail the test for the given reason. */
		public void fail(String message) {
			if(!isRunning()) return;
			passed = false;
			endMillis = System.currentTimeMillis();		
			this.message = message;
		}
		
		/** Pass the test. */
		public void pass() {
			// if this test has already failed, ignore any calls to pass()
			// this can happen if a lower-level chart aborts, because onAbort
			// will not be called on enclosing charts...
			if(!isRunning()) return;
			passed = true;
			endMillis = System.currentTimeMillis();			
		}

		/** Return whether the test is still running. */
		public boolean isRunning() {
			return endMillis == 0;
		}
		
		/** Return whether the test has passed. */
		public boolean passed() {
			return passed && !isRunning();			
		}

		/** Return whether the test has failed. */
		public boolean failed() {
			return !passed && !isRunning();			
		}

		/** Report on the status of all tests. */
		public void report(BufferedWriter out) throws IOException {
			out.write(name);
			out.write(": ");
			if(isRunning()) {
				out.write("Running. ");				
			}
			else if(passed()) {
				out.write("Passed. ");
			}
			else {  // failed
				out.write("FAILED:  ");				
				out.write(message);				
			}
			out.newLine();
		}
	}
		
	/** Get the filepath for the report. */
	public synchronized String getReportFilePath() {
		return reportFilePath;
	}

	/** Set the filepath for the report. */
	public synchronized void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	/** Get a test by name. */
	private TestInfo getTest(String testName) {
		TestInfo info = testsByName.get(testName);
		if(info == null) {
			throw new IllegalArgumentException("test " + testName + " not found");
		}
		return info;
	}
	
	/** Return whether any tests are still running. */
	public synchronized boolean testsAreRunning() {
		for(TestInfo test: testsByName.values()) {
			if(test.isRunning()) return true;
		}
		return false;
	}
	
	/** Remove all old tests and prepare for new ones. */
	public synchronized void initialize() {
		testsByName.clear();
	}
	
	/** Start the test with the given name. */
	public synchronized TestInfo startTest(String testName) {
		TestInfo newTest = new TestInfo(testName);
		testsByName.put(testName, newTest);
		newTest.start();
		return newTest;
	}

	/** Convenience method combining lookup by name and assertTrue. */
	public synchronized void assertTrue(String testName, boolean condition, String msg) {
		boolean testsRunningBefore = testsAreRunning();
		getTest(testName).assertTrue(condition, msg);
		boolean testsRunningAfter = testsAreRunning();
		if(testsRunningBefore && !testsRunningAfter) {
			report();
		}
	}

	/** Convenience method combining lookup by name and assertEqual. */
	public synchronized void assertEqual(String testName, PyObject expected, PyObject actual) {
		boolean testsRunningBefore = testsAreRunning();
		getTest(testName).assertEqual(expected, actual);
		boolean testsRunningAfter = testsAreRunning();
		if(testsRunningBefore && !testsRunningAfter) {
			report();
		}
	}

	/** Convenience method combining lookup by name and fail. */
	public synchronized void fail(String testName, String message) {
		boolean testsRunningBefore = testsAreRunning();
		getTest(testName).fail(message);
		boolean testsRunningAfter = testsAreRunning();
		if(testsRunningBefore && !testsRunningAfter) {
			report();
		}
	}

	/** Convenience method combining lookup by name and pass; decrements cont. */
	public synchronized void pass(String testName) {
		boolean testsRunningBefore = testsAreRunning();
		getTest(testName).pass();
		boolean testsRunningAfter = testsAreRunning();
		if(testsRunningBefore && !testsRunningAfter) {
			report();
		}
	}

	/** Fail any currently running tests with a reason of Timed Out. */
	public synchronized void timeoutRunningTests() {
		boolean testsRunningBefore = testsAreRunning();
		if(testsRunningBefore) {
			for(TestInfo test: testsByName.values()) {
				if(test.isRunning()) {
					test.fail("timed out");
				}
			}
			report();
		}
	}
	
	/** Print a report file summarizing the current status of all tests. */
	public synchronized void report() {		
		try {
			int totalCount = 0;
			int passedCount = 0;
			int failedCount = 0;
			int runningCount = 0;
			for(TestInfo test: testsByName.values()) {
				++totalCount;
				if(test.passed() ) {
					++passedCount;
				}
				else if(test.failed()) {
					++failedCount;
				}
				else if(test.isRunning()) {
					++runningCount;
				}
			}
			java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.FileWriter(reportFilePath));
			out.write("Report written " + dateFormat.format(System.currentTimeMillis()));
			out.newLine();
			out.write("" + totalCount + " tests ran; " + passedCount + " passed, " + failedCount + " failed, " + runningCount + " are still running.");
			out.newLine();
			for(TestInfo test: testsByName.values()) {
				test.report(out);
			}
			out.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

}
