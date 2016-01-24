package com.ils.sfc.gateway;

import java.io.BufferedWriter;
import java.io.IOException;

import org.python.core.PyObject;

/** A class to support some simple testing assertions for SFCs */
public class TestMgr {
	private java.util.Map<String,TestInfo> testsByName = new java.util.HashMap<String,TestInfo>();
	private int runningTestCount;
	private String reportFilePath = "c:/temp/sfcTestReport.txt";
	private java.text.DateFormat dateFormat = new java.text.SimpleDateFormat();
	
	static class TestInfo {
		private String name;
		private long startMillis;
		private long endMillis;
		private boolean passed = true;
		private String message;
		
		public TestInfo(String name) {
			this.name = name;
		}
		
		public void start() {
			startMillis = System.currentTimeMillis();
		}
		
		public long getElapsedMillis() {
			return isRunning() ? System.currentTimeMillis() - startMillis : endMillis - startMillis;
		}

		public boolean assertTrue(boolean condition, String msg) {
			if(!condition) {
				fail("Assertion failed: " + msg);
				return false;
			}
			else {
				return true;
			}
		}

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

		public void fail(String message) {
			if(!isRunning()) return;
			passed = false;
			endMillis = System.currentTimeMillis();		
			this.message = message;
		}
		
		public void pass() {
			// if this test has already failed, ignore any calls to pass()
			// this can happen if a lower-level chart aborts, because onAbort
			// will not be called on enclosing charts...
			if(!isRunning()) return;
			passed = true;
			endMillis = System.currentTimeMillis();			
		}

		public boolean isRunning() {
			return endMillis == 0;
		}
		
		public boolean passed() {
			return passed && !isRunning();			
		}

		public boolean failed() {
			return !passed && !isRunning();			
		}

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
			out.write('\n');
		}
	}
	
	
	public String getReportFilePath() {
		return reportFilePath;
	}

	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	public TestInfo getTest(String testName) {
		TestInfo info = testsByName.get(testName);
		if(info == null) {
			// test not started ?! create it lazily:
			info = startTest(testName);
		}
		return info;
	}
	
	public void initialize() {
		testsByName.clear();
		runningTestCount = 0;
	}
	
	public TestInfo startTest(String testName) {
		TestInfo newTest = new TestInfo(testName);
		testsByName.put(testName, newTest);
		++runningTestCount;
		newTest.start();
		return newTest;
	}

	public void assertTrue(String testName, boolean condition, String msg) {		
		if(!getTest(testName).assertTrue(condition, msg)) {
			decremementRunningTestCount();			
		}
	}

	public void assertEqual(String testName, PyObject expected, PyObject actual) {
		if(!getTest(testName).assertEqual(expected, actual)) {
			decremementRunningTestCount();						
		}
	}

	public void fail(String testName, String message) {
		getTest(testName).fail(message);
		decremementRunningTestCount();
	}
	
	private void decremementRunningTestCount() {
		--runningTestCount;
		if(runningTestCount == 0) {
			report();
		}		
	}

	public void pass(String testName) {
		getTest(testName).pass();
		decremementRunningTestCount();
	}
	
	public void report() {		
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
			out.write("Report written " + dateFormat.format(System.currentTimeMillis()) + "\n");
			out.write("" + totalCount + " tests ran; " + passedCount + " passed, " + failedCount + " failed, " + runningCount + " are still running.\n");
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
