package com.petrolink.mbe.rulesflow.variables;

import java.util.HashSet;
import java.util.Set;

import org.mvel2.UnresolveablePropertyException;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;

import com.petrolink.mbe.rulesflow.RuleFlow;

/**
 * The Rule Variable Factory for MVEL evaluation
 * @author paul
 *
 */
public class RuleVariableResolverFactory implements VariableResolverFactory {
	private static final long serialVersionUID = -4916666709655915487L;
	
	private final RuleFlow rule;
	private VariableResolverFactory nextFactory;
	
	
	/**
	 * @param ruleFlow the Flow
	 * @param nextFactory the Following factory
	 */
	public RuleVariableResolverFactory(RuleFlow ruleFlow, VariableResolverFactory nextFactory) {
		this.rule = ruleFlow;
		this.setNextFactory(nextFactory);
	}

	@Override
	public VariableResolver createVariable(String name, Object value) {
		 throw new UnsupportedOperationException("This resolver is read-only");		
	}

	@Override
	public VariableResolver createIndexedVariable(int index, String name, Object value) {
		 throw new UnsupportedOperationException("This resolver is read-only");		
	}

	@Override
	public VariableResolver createVariable(String name, Object value, Class<?> type) {
		 throw new UnsupportedOperationException("This resolver is read-only");		
	}

	@Override
	public VariableResolver createIndexedVariable(int index, String name, Object value, Class<?> typee) {
		 throw new UnsupportedOperationException("This resolver is read-only");		
	}

	@Override
	public VariableResolver setIndexedVariableResolver(int index, VariableResolver variableResolver) {
		 throw new UnsupportedOperationException("This resolver is read-only");		
	}

	@Override
	public VariableResolverFactory getNextFactory() {
		return nextFactory;
	}

	@Override
	public VariableResolverFactory setNextFactory(VariableResolverFactory resolverFactory) {
		this.nextFactory = resolverFactory;
		return nextFactory;
	}

	@Override
	public VariableResolver getVariableResolver(String name) {
		String vname = name;
		boolean dataPoint = false;
		if (name.startsWith(Variable.RESOLVABLE_VARIABLE_PREFIX)) {
			vname = name.substring(1);
			dataPoint = true;
		}
		
		if (rule.getConditionVariables().containsKey(vname)) {
			Variable v = rule.getConditionVariables().get(vname);
			if (!dataPoint && v instanceof ChannelAlias) {
				return ((ChannelAlias) v).getDataPointValueResolver();
			} else {
				return v.getVariableResolver();
			}
		} else if(rule.getAlertClassConditionVariable().containsKey(vname)){
			AlertClassVariable v = rule.getAlertClassConditionVariable().get(vname);
			if (!dataPoint ) {
				return v.getAlertSnapshotResolver();
			} else {
				return v.getVariableResolver();
			}
		} else {
			
			if (this.nextFactory != null) {
				return this.nextFactory.getVariableResolver(name);
			}
			
			return null;
		}
	}

	@Override
	public VariableResolver getIndexedVariableResolver(int index) {
		return null;
	}

	@Override
	public boolean isTarget(String name) {
		return false;
	}

	@Override
	public boolean isResolveable(String name) {
		String vname = name;
		boolean dataPoint = false;
		if (name.startsWith(Variable.RESOLVABLE_VARIABLE_PREFIX)) {
			vname = name.substring(1);
			dataPoint = true;
		}
		
		if (rule.getConditionVariables().containsKey(vname)) {
			if (!dataPoint)
				return true;
			else if (dataPoint && (rule.getConditionVariables().get(vname) instanceof ChannelAlias))
				return true;
		} else if (rule.getAlertClassConditionVariable().containsKey(vname)) {
			if (!dataPoint)
				return true;
			else if (dataPoint && (rule.getAlertClassConditionVariable().get(vname) instanceof AlertClassVariable))
				return true;
		} else {
			return ((this.nextFactory != null) && (this.nextFactory.isResolveable(name)));
		}
		return false;
	}

	@Override
	public Set<String> getKnownVariables() {
		Set<String> varNames = new HashSet<String>();
		varNames.addAll(rule.getConditionVariables().keySet());
		varNames.addAll(rule.getAlertClassConditionVariable().keySet());
		return varNames;
	}

	@Override
	public int variableIndexOf(String name) {
		return 0;
	}

	@Override
	public boolean isIndexedFactory() {
		return false;
	}

	@Override
	public boolean tiltFlag() {
		return false;
	}

	@Override
	public void setTiltFlag(boolean tilt) {
	}
}
