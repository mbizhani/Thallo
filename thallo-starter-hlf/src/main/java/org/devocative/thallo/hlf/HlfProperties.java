package org.devocative.thallo.hlf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "thallo.hlf")
public class HlfProperties {
	private String channel;
	private String chaincode;
	private String connectionProfileFile;
	private String identityWalletDir;
	private String orgMspId;
	private CAServerProperties caServer;

	@Getter
	@Setter
	public static class CAServerProperties {
		private String pemFile;
		private String url;
		private String username;
		private String password;
	}
}
