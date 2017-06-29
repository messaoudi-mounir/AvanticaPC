package com.petrolink.mbe.services;

import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;

import com.rabbitmq.client.Channel;
import com.smartnow.engine.Engine;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.nodes.actions.Action;
import com.smartnow.engine.xflow.Flow;
import com.smartnow.rabbitmq.service.RMQRestfulService;

/**
 * Exposes the Action (and Nodes in general) update capability through RabbitMQ
 * Scope is limited to Action Mode updates and node global settings
 * @author paul
 *
 */
public class ActionModeService extends RMQRestfulService {
	protected class ActionModeServiceConsumer extends RMQRestfulServiceConsumer {
		public ActionModeServiceConsumer(Channel channel) {
			super(channel);
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#doPost(java.util.Map, org.jdom2.Element)
		 */
		@Override
		protected Document doPost(Map<String, Object> map, Element root) {
			// TODO Change Action Mode
			// Flow Name (Rule UUID), Action Id and Action Mode is expected in the xml element
			// Returns the same incoming structure but including only the SetMode nodes 
			// that successfully changed the mode

			for (Element set : root.getChildren("SetMode", root.getNamespace())) {
				String flowId = set.getAttributeValue("flow");
				String action = set.getAttributeValue("action");
				Flow flow = (Flow) Engine.getInstance().getFlow(flowId);
				Node node = flow.getNode(action);
				if (node instanceof Action) {
					String mode = set.getAttributeValue("mode");
					switch (mode) {
					case "test":
						((Action) node).setOperationMode(Engine.OperationStatus.TEST);
						break;
					case "active":
						((Action) node).setOperationMode(Engine.OperationStatus.ACTIVE);
						break;
					case "passive":
						((Action) node).setOperationMode(Engine.OperationStatus.PASSIVE);
						break;
					}
				}
			}
			
			return null;
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#doGet(java.util.Map, org.jdom2.Element)
		 */
		@Override
		protected Document doGet(Map<String, Object> map, Element root) {
			// TODO Get Action Mode or Actions Mode for a given Rule
			// Map expects most include Flow Name (Rule UUID)
			// Map could include Action ID (either Name or Unique Id)

			// If Action Id comes it return a <ActionMode flow="" id="" mode=""/> element
			// If not it return the same <ActionMode> node for each Action in the Flow
			
			return null;
		}
	}
	
	@Override
	protected GenericRPCServiceConsumer getConsumer(Channel channel) {
		return new ActionModeServiceConsumer(channel);
	}
}
