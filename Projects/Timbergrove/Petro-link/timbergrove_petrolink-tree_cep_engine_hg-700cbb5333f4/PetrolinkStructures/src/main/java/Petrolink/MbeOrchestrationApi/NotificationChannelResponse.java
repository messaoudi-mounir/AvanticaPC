package Petrolink.MbeOrchestrationApi;

import java.util.List;
import java.util.UUID;

public class NotificationChannelResponse {
	private List<UUID> channels;

	/**
	 * @return the channels
	 */
	public final List<UUID> getChannels() {
		return channels;
	}

	/**
	 * @param channels the channels to set
	 */
	public final void setChannels(final List<UUID> newChannels) {
		this.channels = newChannels;
	} 
}
