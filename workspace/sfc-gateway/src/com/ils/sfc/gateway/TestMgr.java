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

		public boolean assertTrue(boolean condition, String stepName, String msg) {
			if(!condition) {
				fail("Assertion failed on step " + stepName +": " + msg);
				return false;
			}
			else {
				return true;
			}
		}

		public boolean assertEqual(PyObject expected, PyObject actual, String stepName) {
			if(!expected.equals(actual)) {
				String msg = expected.toString() + " != " + actual.toString();
				fail("Assertion failed in step " + stepName +": " + msg);
				return false;
			}
			else {
				return true;
			}
		}

		public void fail(String message) {
			passed = false;
			endMillis = System.currentTimeMillis();		
			this.message = message;
		}
		
		public void pass() {
			// if this test has already failed, ignore any calls to pass()
			// this can happen if a lower-level chart aborts, because onAbort
			// will not be called on enclosing charts...
			if(!passed) return;
			passed = true;
			endMillis = System.currentTimeMillis();			
		}

		public boolean passed() {
			return passed && endMillis > 0;
			
		}
		public boolean report(BufferedWriter out) throws IOException {
			out.write(name);
			out.write(": ");
			if(endMillis == 0) {
				out.write("Running. ");				
			}
			else if(passed) {
				out.write("Passed. ");
			}
			else {  // failed
				out.write("FAILED:  ");				
				out.write(message);				
			}
			out.write('\n');
			/*
			out.write(" Started ");
			out.write(dateFormat.format(startMillis));
			long elapsedMillis = 0;
			if(endMillis != 0) {
				out.write(" Ended ");
				out.write(dateFormat.format(endMillis));
				elapsedMillis = endMillis - startMillis;
			}
			else {
				elapsedMillis = System.currentTimeMillis() - startMillis;
			}
			out.write(" Run time: " + ((int)(elapsedMillis / 1000)) + "s");
			out.write('\n');
			*/
			return passed;
		}
	}
	
	
	public String getReportFilePath() {
		return reportFilePath;
	}

	public void setReportFilePath(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	public TestInfo getTest(String testName) {
		return testsByName.get(testName);
	}
	
	public void initialize() {
		testsByName.clear();
		runningTestCount = 0;
	}
	
	public void startTest(String testName) {
		TestInfo newTest = new TestInfo(testName);
		testsByName.put(testName, newTest);
		++runningTestCount;
		newTest.start();
	}

	public void assertTrue(String testName, String stepName, boolean condition, String msg) {		
		if(!getTest(testName).assertTrue(condition, stepName, msg)) {
			decremementRunningTestCount();			
		}
	}

	public void assertEqual(String testName, String stepName, PyObject expected, PyObject actual) {
		if(!getTest(testName).assertEqual(expected, actual, stepName)) {
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
			int passCount = 0;
			for(TestInfo test: testsByName.values()) {
				++totalCount;
				if(test.passed() ) {
					++passCount;
				}
			}
			java.io.BufferedWriter out = new java.io.BufferedWriter(new java.io.FileWriter(reportFilePath));
			out.write("Report written " + dateFormat.format(System.currentTimeMillis()) + "\n");
			out.write("" + totalCount + " tests ran; " + passCount + " passed, " + (totalCount - passCount) + " failed\n");
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
