package com.petrolink.mbe.rulesflow.variables;

import org.jdom2.Element;
import org.mvel2.integration.VariableResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.rulesflow.RuleFlow;
import com.smartnow.engine.exceptions.EngineException;

/**
 * @author paul
 * Variable abstract class for Rule Level Variables 
 */
public abstract class Variable {
	/**
	 * Prefix for channel variable when referred by script.
	 */
	public static final String RESOLVABLE_VARIABLE_PREFIX = "$";
	
	/**
	 * Variable Scope constant GLOBAL
	 */
	public static final char GLOBAL_SCOPE = 'G';
	/**
	 * Variable Scope constant CONDITION (Only available at condition evaluation and used to fire the rule execution)
	 */
	public static final char CONDITION_SCOPE = 'C';
	/**
	 * Variable Scope constant FILTERING (Only available to filter condition evaluation)
	 */
	public static final char FILTERING_SCOPE = 'F';
	/**
	 * Variable Scope constant REFERENCE (Only available to condition evaluation)
	 */
	public static final char REFERENCE_SCOPE = 'R';
	protected String alias;
	protected int sequence = 0;
	protected char scope;
	protected Logger logger = LoggerFactory.getLogger(Variable.class);
	transient protected RuleFlow rule;
	private VariableResolver variableResolver;
	
	/**
	 * @return the scope
	 */
	public char getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(char scope) {
		this.scope = scope;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	/**
	 * @return the Variable Value depending on the specific implementation
	 */
	public abstract Object getValue();
	
	/**
	 * @return the allowed type of the value
	 */
	public abstract Class<?> getValueType();

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Loads the Variable from the definition XML Element and parent rule
	 * @param rule
	 * @param e
	 * @throws EngineException TODO
	 */
	public void load(RuleFlow rule, Element e) throws EngineException {
		this.rule = rule;
		this.alias = e.getAttributeValue("alias");
		if (e.getAttribute("sequence") != null)
			this.sequence = Integer.parseInt(e.getAttributeValue("sequence"));
		String scope = e.getAttributeValue("scope");
		
		if ("global".equals(scope)) {
			this.scope = Variable.GLOBAL_SCOPE;
		} else if ("condition".equals(scope)) {
			this.scope = Variable.CONDITION_SCOPE;
		} else if ("reference".equals(scope)) {
			this.scope = Variable.REFERENCE_SCOPE;
		} else if ("filtering".equals(scope)) {
			this.scope = Variable.FILTERING_SCOPE;
		} else {
			this.scope = Variable.GLOBAL_SCOPE;
		}
	}
	
	/**
	 * @return the Variable MVEL Variable Resolver
	 */
	public VariableResolver getVariableResolver() {
		if (variableResolver == null)
			variableResolver = createVariableResolver();
		return variableResolver;
	}
	
	/**
	 * @return the XML Representation of the Variable
	 */
	public abstract Element toElement();
	
	@Override
	public String toString() {
		return getAlias() + ":" + getClass().getSimpleName();
	}
	
	protected VariableResolver createVariableResolver() {
		return new SimpleVariableResolver();
	}
	
	/**
	 * The Rule Variables MVEL Revolver 
	 * @author paul
	 *
	 */
	@SuppressWarnings("rawtypes")
	public final class SimpleVariableResolver implements VariableResolver {
		private static final long serialVersionUID = -8581643393113201660L;
		
		SimpleVariableResolver() {
		}
	
		@Override
		public String getName() {
			return alias;
		}
	
		@Override
		public Class getType() {
			return getValueType();
		}
	
		@Override
		public void setStaticType(Class type) {
			// Non-applicable
		}
	
		@Override
		public int getFlags() {
			return 0;
		}
	
		@Override
		public Object getValue() {
			return Variable.this.getValue();
		}
	
		@Override
		public void setValue(Object value) {
			 throw new UnsupportedOperationException("This resolver is read-only");		
		}
	}
}
