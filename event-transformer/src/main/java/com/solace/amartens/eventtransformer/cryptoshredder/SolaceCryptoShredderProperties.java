package com.solace.amartens.eventtransformer.cryptoshredder;

import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix = "solace.event.transformer.crypto-shredder")
public class SolaceCryptoShredderProperties {
	private String sourceEventVersionId;
	private String targetEventVersionId;
	private String vaultTransitEncryptionKey;

	public String getSourceEventVersionId() {
		return sourceEventVersionId;
	}

	public void setSourceEventVersionId(String sourceEventVersionId) {
		this.sourceEventVersionId = sourceEventVersionId;
	}

	public String getTargetEventVersionId() {
		return targetEventVersionId;
	}

	public void setTargetEventVersionId(String targetEventVersionId) {
		this.targetEventVersionId = targetEventVersionId;
	}

	public String getVaultTransitEncryptionKey() {
		return vaultTransitEncryptionKey;
	}

	public void setVaultTransitEncryptionKey(String vaultTransitEncryptionKey) {
		this.vaultTransitEncryptionKey = vaultTransitEncryptionKey;
	}
}