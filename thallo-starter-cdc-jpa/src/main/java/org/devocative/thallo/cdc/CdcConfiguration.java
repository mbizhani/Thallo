package org.devocative.thallo.cdc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("cdcConfig")
@ConfigurationProperties(prefix = "devocative.thallo.cdc")
public class CdcConfiguration {

	private boolean enabled = true;
	private String topic = "cdc-topic";
	private String errorTopic = "cdc-topic-error";
	private SendEnd send = new SendEnd();
	private ReceiveEnd receive = new ReceiveEnd();

	// ------------------------------

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getErrorTopic() {
		return errorTopic;
	}

	public void setErrorTopic(String errorTopic) {
		this.errorTopic = errorTopic;
	}

	public SendEnd getSend() {
		return send;
	}

	public void setSend(SendEnd send) {
		this.send = send;
	}

	public ReceiveEnd getReceive() {
		return receive;
	}

	public void setReceive(ReceiveEnd receive) {
		this.receive = receive;
	}

	// ------------------------------

	public static abstract class BaseEnd {
		private boolean create = true;
		private boolean update = true;
		private boolean delete = false;

		public boolean isCreate() {
			return create;
		}

		public void setCreate(boolean create) {
			this.create = create;
		}

		public boolean isUpdate() {
			return update;
		}

		public void setUpdate(boolean update) {
			this.update = update;
		}

		public boolean isDelete() {
			return delete;
		}

		public void setDelete(boolean delete) {
			this.delete = delete;
		}
	}

	public static class SendEnd extends BaseEnd {
	}

	public static class ReceiveEnd extends BaseEnd {
		private Map<String, String> mappedClasses = new HashMap<>();

		public Map<String, String> getMappedClasses() {
			return mappedClasses;
		}

		public void setMappedClasses(Map<String, String> mappedClasses) {
			this.mappedClasses = mappedClasses;
		}
	}
}
