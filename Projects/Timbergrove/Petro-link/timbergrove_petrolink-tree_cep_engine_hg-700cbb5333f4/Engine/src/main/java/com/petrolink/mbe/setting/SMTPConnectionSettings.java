package com.petrolink.mbe.setting;

import javax.mail.Authenticator;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;

import com.smartnow.engine.util.NamedValueResource;

/**
 * Class Containing SendMailConnection configuration.
 * @author aristo
 *
 */
public class SMTPConnectionSettings {
	private static final int MAX_PORT_NUMBER = 65535;
	private static final int DEFAULT_PORT_SMTP = 25;
	private static final int DEFAULT_PORT_SMTPS = 465;

	private static final String SMTP_PROTOCOL = "smtp";
	private static final String SMTPS_PROTOCOL = "smtps";

	private boolean sslEnabled = false;
	private boolean sslCheckServerIdentity = false;
	private String hostName;
	private int smtpPort = 0;
	private Authenticator mailAuthenticator;
	private boolean startTlsEnabled = false;
	private boolean startTlsRequired = false;
	
	/**
	 * Constructor of SMTP Connection from an Engine Connection
	 * @param settings
	 */
	public SMTPConnectionSettings(NamedValueResource settings) {
		this.hostName = (String) settings.get("Host");
		this.smtpPort = Integer.valueOf(settings.get("Port").toString());
		
		if (settings.containsKey("User")) {
			mailAuthenticator = new DefaultAuthenticator(
					(String) settings.get("User"), 
					(String) settings.get("Password"));
		}
		
		if (settings.containsKey("sslEnabled")) {	
			this.sslEnabled = Boolean.valueOf(settings.get("sslEnabled").toString());
		}
		
		if (settings.containsKey("sslCheckServerIdentity")) {
			this.sslCheckServerIdentity = Boolean.valueOf(settings.get("sslCheckServerIdentity").toString());
		}

		if (settings.containsKey("startTlsEnabled")) {
			this.startTlsEnabled = Boolean.valueOf(settings.get("startTlsEnabled").toString());
		}
		
		if (settings.containsKey("startTlsRequired")) {
			this.startTlsRequired = Boolean.valueOf(settings.get("startTlsRequired").toString());
		}
	}

	/**
	 * Generic Constructor
	 */
	public SMTPConnectionSettings() {
	}

	/**
	 * @return the smtpProtocol, by default is "smtp" if not set;
	 */
	public final String getSmtpProtocol() {
		if (isSslEnabled()) { 
			return SMTPS_PROTOCOL;
		}
		return SMTP_PROTOCOL;
	}

	/**
	 * @return the hostName
	 */
	public final String getHostName() {
		return hostName;
	}
	/**
	 * @param newHostName the hostName to set
	 */
	public final void setHostName(final String newHostName) {
		this.hostName = newHostName;
	}
	/**
	 * @return the smtpPort
	 */
	public final int getSmtpPort() {
		if ((smtpPort > 0) && (smtpPort < MAX_PORT_NUMBER)) {
			return smtpPort;
		} else if (isSslEnabled()) {
			return DEFAULT_PORT_SMTPS;
		} else {
			return DEFAULT_PORT_SMTP;
		}
	}
	
	/**
	 * @param newSmtpPort the smtpPort to set
	 */
	public final void setSmtpPort(final int newSmtpPort) {
		this.smtpPort = newSmtpPort;
	}
	
	/**
	 * @return the sslEnabled
	 */
	public final boolean isSslEnabled() {
		return sslEnabled;
	}


	/**
	 * @param enableSSL Whether to enable SSL connection
	 */
	public final void setSslEnabled(final boolean enableSSL) {
		this.sslEnabled = enableSSL;
	}
	
	/**
	 * @return the startTlsEnabled
	 */
	public final boolean isStartTlsEnabled() {
		return startTlsEnabled;
	}


	/**
	 * @param enableStartTLS Whether to enable START TLS
	 */
	public final void setStartTlsEnabled(final boolean enableStartTLS) {
		this.startTlsEnabled = enableStartTLS;
	}
	
	/**
	 * @return the startTlsRequired
	 */
	public final boolean isStartTlsRequired() {
		return startTlsRequired;
	}


	/**
	 * @param requireStartTls the startTlsRequired to set
	 */
	public final void setStartTlsRequired(final boolean requireStartTls) {
		this.startTlsRequired = requireStartTls;
	}
	
	/**
	 * @return the mailAuthenticator
	 */
	public final Authenticator getMailAuthenticator() {
		return mailAuthenticator;
	}
	/**
	 * @param newMailAuthenticator the mailAuthenticator to set
	 */
	public final void setMailAuthenticator(final Authenticator newMailAuthenticator) {
		this.mailAuthenticator = newMailAuthenticator;
	}
	/**
	 * @return the sslCheckServerIdentity
	 */
	public final boolean isSslCheckServerIdentity() {
		return sslCheckServerIdentity;
	}

	/**
	 * @param newSslCheckServerIdentity the sslCheckServerIdentity to set
	 */
	public final void setSslCheckServerIdentity(final boolean newSslCheckServerIdentity) {
		this.sslCheckServerIdentity = newSslCheckServerIdentity;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SendMailActionConnection{");
		String protocol = this.getSmtpProtocol();
		builder.append(protocol);
		builder.append("://");
		
		Authenticator auth = getMailAuthenticator();
		if (auth != null) {
			builder.append(auth.toString());
			builder.append("@");
		}
		builder.append(this.getHostName());
		builder.append(":");
		builder.append(this.getSmtpPort());
		builder.append("?sslOnConnect=");
		builder.append(this.isSslEnabled());
		builder.append("&startTls=");
		builder.append(this.isStartTlsEnabled());
		builder.append("&startTlsRequired=");
		builder.append(this.isStartTlsRequired());
		builder.append("&sslCheckServerIdentity=");
		builder.append(this.isSslCheckServerIdentity());
		builder.append("}");
		return builder.toString();
	}

	
	/**
	 * Set email parameters such as Connection configuration, security etc.
	 * @param email 
	 */
	public void configureEmailConnection(final Email email) {		
		// - Setting Email Properties
		email.setSSLOnConnect(this.isSslEnabled());
		if (this.isSslEnabled()) {
			email.setSslSmtpPort(String.valueOf(this.getSmtpPort()));
		} else {
			email.setSmtpPort(this.getSmtpPort());
		}
		email.setHostName(this.getHostName());
		email.setStartTLSEnabled(this.isStartTlsEnabled());
		email.setStartTLSEnabled(this.isStartTlsRequired());
		email.setSSLCheckServerIdentity(this.isSslCheckServerIdentity());
		
		Authenticator auth = this.getMailAuthenticator();
		if (auth != null) {
			email.setAuthenticator(auth);	
		}
	}	
	
}
