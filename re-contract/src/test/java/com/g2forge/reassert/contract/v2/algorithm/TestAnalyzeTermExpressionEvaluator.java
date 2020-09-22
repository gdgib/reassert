package com.g2forge.reassert.contract.v2.algorithm;

import static com.g2forge.reassert.contract.v2.model.TermOperation.not;
import static com.g2forge.reassert.contract.v2.model.TermOperation.of;
import static com.g2forge.reassert.contract.v2.model.TermOperation.or;

import org.junit.Test;

import com.g2forge.alexandria.java.core.helpers.HCollection;
import com.g2forge.alexandria.test.HAssert;
import com.g2forge.reassert.contract.v2.model.TermOperation;
import com.g2forge.reassert.contract.v2.model.contract.TestLicenseTerm;
import com.g2forge.reassert.contract.v2.model.contract.TestUsageTerm;
import com.g2forge.reassert.contract.v2.model.finding.ExpressionContextFinding;
import com.g2forge.reassert.core.model.contract.terms.TermRelation;

public class TestAnalyzeTermExpressionEvaluator {
	@Test
	public void test() {
		final TermOperation expression = or(not(TestUsageTerm.Term), of(TestLicenseTerm.Permission));
		final ExpressionContextFinding actual = new AnalyzeTermExpressionEvaluator(TermRelation.Included).eval(expression);
		HAssert.assertEquals(new ExpressionContextFinding(expression, HCollection.asSet(TestUsageTerm.Term, TestLicenseTerm.Permission), HCollection.asSet(TestUsageTerm.Term), null), actual);
	}
}
