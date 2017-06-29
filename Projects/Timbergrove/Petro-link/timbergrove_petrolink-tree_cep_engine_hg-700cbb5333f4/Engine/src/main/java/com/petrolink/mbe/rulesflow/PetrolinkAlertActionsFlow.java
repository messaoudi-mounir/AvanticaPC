package com.petrolink.mbe.rulesflow;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnow.alertstatus.flow.EmbeddedAlertActionsFlow;
import com.smartnow.engine.event.ChildEvent;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.event.Event.State;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.nodes.flowcontrol.FlowControlNode.FlowControlBehavior;
import com.smartnow.engine.util.EngineContext;

/**
 * Actions Flow Implementation for Alert Events actions 
 * @author paul
 *
 */
public class PetrolinkAlertActionsFlow extends EmbeddedAlertActionsFlow {
	
	
	private Logger logger = LoggerFactory.getLogger(PetrolinkAlertActionsFlow.class);
	private RuleFlow parent;
	private String actionGroup;

	/**
	 * Constructor
	 * @param rule the Parent Rule Flow
	 */
	public PetrolinkAlertActionsFlow(RuleFlow rule) {
		this.parent = rule;
	}

	/* (non-Javadoc)
	 * @see com.smartnow.engine.xflow.ActionSequenceFlow#cleanup()
	 */
	@Override
	public void cleanup() {
		this.parent = null;
		super.cleanup();
	}

	/**
	 * @return the Parent Rule
	 */
	public RuleFlow getParent() {
		return parent;
	}

	/**
	 * Assigns the Action Group the flow register against in the listeners
	 * @param name
	 */
	public void setActionGroup(String name) {
		this.actionGroup = name;
	}
	
	/**
	 * @return the Flow associated Action Group 
	 */
	public String getActionGroup() {
		return actionGroup;
	}
	
	

	@Override
	public int execute(Event ev, UUID sharedObjects) throws EngineException {
		try {
			return executeEvent(ev, sharedObjects);
		} catch (EngineException eng) {
			logger.error("Error executing AlertActionsFlow {}", getUniqueId(), eng);
			throw eng;
		}
	}
	
	/**
	 * Execute Event
	 * Taken form super.execute from engine to improve Thread safety
	 * @param ev
	 * @param sharedObjects
	 * @return sucess or failure
	 * @throws EngineException
	 */
	public int executeEvent(Event ev, UUID sharedObjects) throws EngineException {
		//Overriden from engine to improve Thread safety
		ev.setStatus(State.RUNNING);

		try {
			EngineContext e = prepareContext(ev, sharedObjects);
			Throwable arg3 = null;

			byte arg6;
			try {
				//To make sure it is safer in multi worker, the work array must be copied first
				//This is as nodeSet, if modified by some external mechanism
				//The local work can still be consistent, within the Flow
				ArrayList<Node> orderedNodeList = new ArrayList<Node>();
				for(Node node: this.nodesSet){
					orderedNodeList.add(node);
				}
				
				//The following initNodes, executeNodes, finalizeNodes ensure
				//there is no recheck to at risk nodesSet which can be modified (say when stopping rule)
				//instead it works on consistent set of context and event
				this.initNodes(orderedNodeList,e);
				int result = this.executeNodes(orderedNodeList,e);
				this.finalizeNodes(orderedNodeList,e);
				
				if (ev instanceof ChildEvent && result != 0) {
					ev.setStatus(State.FAILED);
					int completed1 = result;
					return completed1;
				}

				if (result == 1) {
					logger.trace("Sending Event to be re-queued");
					ev.setExecuted(System.currentTimeMillis());
					ev.setStatus(State.FAILED);
					throw new EngineException("Sending Event to be re-queued");
				}

				boolean completed = true;
				if (ev.hasChildren()) {
					completed = this.waitForChildren(ev);
				}

				if (completed) {
					logger.trace("Event with unique ids (" + ev.getExternalIDAsString() + ") completed successfully");
					byte arg24 = 0;
					return arg24;
				}

				arg6 = -1;
			} catch (Throwable arg19) {
				arg3 = arg19;
				throw arg19;
			} finally {
				if (e != null) {
					if (arg3 != null) {
						try {
							e.close();
						} catch (Throwable arg18) {
							arg3.addSuppressed(arg18);
						}
					} else {
						e.close();
					}
				}

			}

			return arg6;
		} catch (EngineException arg21) {
			throw arg21;
		} catch (Exception arg22) {
			logger.error("Unexpected flow execution error", arg22);
			throw new EngineException(arg22);
		}
	}
	
	/**
	 * Initialize the nodes
	 * @param nodes
	 * @param context
	 * @throws EngineException
	 */
	protected void initNodes(ArrayList<Node> nodes, Map<String, Object> context) throws EngineException {
		for(Node node: nodes) {
			node.init(context);
		}
	}
	
	/**
	 * Execute the nodes
	 * @param nodes
	 * @param context
	 * @return Status of execution
	 */
	protected int executeNodes(ArrayList<Node> nodes, Map<String, Object>  context)  {
		byte result = 0;
		for(Node node: nodes) {
			try {
				int e = node.execute(context);
				if (e != 0) {
					if (this.failControl == FlowControlBehavior.STOPFAIL) {
						result = 1;
						break;
					}

					if (this.failControl == FlowControlBehavior.STOPSUCCESS) {
						result = 0;
						break;
					}

					if (this.failControl == FlowControlBehavior.CONTINUEFAIL) {
						result = 1;
					}
				}
			} catch (EngineException arg5) {
				if (logger.isDebugEnabled()) {
					logger.debug("Unhandled exception " + arg5.getMessage(), arg5);
				} else {
					logger.error("Unhandled exception " + arg5.getMessage());
				}
			}
		}
		return result;
	}
	
	
	/**
	 * Finalize the nodes after all node is executed
	 * @param nodes
	 * @param context
	 * @throws EngineException
	 */
	protected void finalizeNodes(ArrayList<Node> nodes, Map<String, Object> context) throws EngineException {
		for(Node node: nodes) {
			node.finalize(context);
		}
	}
	
	
}
