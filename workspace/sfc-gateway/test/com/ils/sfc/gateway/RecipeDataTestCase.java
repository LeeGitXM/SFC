package com.ils.sfc.gateway;

import com.ils.sfc.common.IlsSfcNames;
import com.inductiveautomation.sfc.api.PyChartScope;

import junit.framework.TestCase;

public class RecipeDataTestCase extends TestCase {
	private PyChartScope chartScope = new PyChartScope();
	private PyChartScope stepScope = new PyChartScope();
	private String value = "value";

	public void testSimplePathMissing() {
		try {
			String path = "key";
			IlsGatewayScripts.pathSet(chartScope, path, value);
			assertEquals(value, IlsGatewayScripts.pathGet(chartScope, path));
			fail();
		}
		catch(IllegalArgumentException e) {}
	}

	public void testSimplePath() {
		String path = "key";
		chartScope.put(path, "");
		standardAssertion(path, value);
	}

	public void testDoublePathMissing() {
		try {
			String path = "a.b";
			chartScope.put("a", new PyChartScope());
			standardAssertion(path, value);
			fail();
		}
		catch(IllegalArgumentException e) {}
	}

	public void testDoublePathBadType() {
		try {
			String path = "a.b";
			chartScope.put("a", "");
			standardAssertion(path, value);
			fail();
		}
		catch(IllegalArgumentException e) {}
	}

	public void testDoublePath() {
		String path = "a.b";
		PyChartScope subScope = new PyChartScope();
		chartScope.put("a", subScope);
		subScope.put("b", "");
		standardAssertion(path, value);
	}
	
	private void standardAssertion(String path, Object value) {
		IlsGatewayScripts.pathSet(chartScope, path, value);
		assertEquals(value, IlsGatewayScripts.pathGet(chartScope, path));
	}
	
	public void testLocalScope() {
		assertEquals(stepScope, IlsGatewayScripts.resolveScope(chartScope, stepScope, IlsSfcNames.LOCAL));
	}

	public void testPreviousScope() {
		PyChartScope prevScope = new PyChartScope();
		stepScope.put(IlsSfcNames.PREVIOUS, prevScope);
		assertEquals(prevScope, IlsGatewayScripts.resolveScope(chartScope, stepScope, IlsSfcNames.PREVIOUS));
	}

	public void testSuperiorScope() {
		PyChartScope encStepScope = new PyChartScope();
		chartScope.put(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY, encStepScope);
		assertEquals(encStepScope, IlsGatewayScripts.resolveScope(chartScope, stepScope, IlsSfcNames.SUPERIOR));
	}

	public void testNamedScope() {
		PyChartScope superChartScope = new PyChartScope();
		chartScope.put(IlsSfcNames.PARENT, superChartScope);
		PyChartScope superStepScope = new PyChartScope();
		chartScope.put(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY, superStepScope);
		superStepScope.put(IlsSfcNames.S88_LEVEL, IlsSfcNames.OPERATION);
		assertEquals(superStepScope, IlsGatewayScripts.resolveScope(chartScope, stepScope, IlsSfcNames.OPERATION));
	}

	public void testGlobalScope() {
		PyChartScope superChartScope = new PyChartScope();
		chartScope.put(IlsSfcNames.PARENT, superChartScope);
		chartScope.put(IlsSfcNames.ENCLOSING_STEP_SCOPE_KEY, stepScope);
		assertEquals(stepScope, IlsGatewayScripts.resolveScope(chartScope, stepScope, IlsSfcNames.GLOBAL));
	}
	
}
