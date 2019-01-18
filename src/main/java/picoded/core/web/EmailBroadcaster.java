package picoded.core.web;


import java.util.Properties;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.Flags.Flag;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import picoded.core.struct.GenericConvertMap;
import picoded.core.struct.GenericConvertHashMap;
import picoded.core.struct.ProxyGenericConvertMap;

/**
 * Email broad casting module, used to send out emails =)
 *
 * This is embedded inside CommonsPage
 **/
public class EmailBroadcaster {

	private Session session;
	private String fromEmail;
	private String adminEmail;

	/**
	 * Email constructor, used to setup the SMTP connection
	 *
	 * @param  SMTP Url address (with port)
	 * @param  Username for sender account
	 * @param  Password for sender account
	 * @param  Sending email address
	 **/
	public EmailBroadcaster(final String smtpUrl, final String username, final String password,
							final String fromAddress) {
		this(smtpUrl, username, password, fromAddress, false);
	}

	public EmailBroadcaster(final String smtpUrl, final String username, final String password,
							final String fromAddress, boolean isSSL) {
		this(smtpUrl, username, password, fromAddress, isSSL, false);
	}

	/**
	 * Email constructor, used to setup the SMTP over SSL connection
	 *
	 * @param  SMTP Url address (with port)
	 * @param  Username for sender account
	 * @param  Password for sender account
	 * @param  Sending email address
	 * @param  Use SSL over SMTP
	 **/
	public EmailBroadcaster(final String smtpUrl, final String username, final String password,
							final String fromAddress, boolean isSSL, boolean enableSTARTTLS) {

		fromEmail = fromAddress;
		Properties props = new Properties();

		String[] parts = smtpUrl.split(":");
		String smtpAdd = parts[0];
		String smtpPort = (parts.length > 1) ? parts[1] : "465";

		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", smtpAdd);
		if ((smtpAdd.contains("gmail") || smtpAdd.contains("live.com")) || enableSTARTTLS) {
			props.put("mail.smtp.starttls.enable", "true");
		}

		if (isSSL) {
			props.put("mail.smtp.socketFactory.port", smtpPort);
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}

		props.put("mail.smtp.port", smtpPort);

		if (username != null && username.trim().length() > 0) {
			props.put("mail.smtp.auth", "true");
			session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
		} else {
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.auth.login.disable", "true");

			session = Session.getInstance(props, null);
		}

		// System.out.println("Finished setting up emailbroadcaster object");

	}

	public EmailBroadcaster(GenericConvertMap<String, Object> inSmtpConfigMap) {
		GenericConvertMap<String, Object> smtpConfigMap = ProxyGenericConvertMap
				.ensure(inSmtpConfigMap);

		if (smtpConfigMap == null) {
			System.out.println("EmailBroadcaster -> smtpConfigMap is null");
			return;
		}

		fromEmail = smtpConfigMap.getString("emailFrom", "");

		String smtpUrl = smtpConfigMap.getString("host", "");
		String[] parts = smtpUrl.split(":");

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");

		String smtpAdd = parts[0];
		props.put("mail.smtp.host", smtpAdd);

		String smtpPort = (parts.length > 1) ? parts[1] : "465";
		props.put("mail.smtp.port", smtpPort);

		boolean enableSTARTTLS = smtpConfigMap.getBoolean("starttls", false);
		if ((smtpAdd.contains("gmail") || smtpAdd.contains("live.com")) || enableSTARTTLS) {
			props.put("mail.smtp.starttls.enable", "true");
		}

		boolean isSSL = smtpConfigMap.getBoolean("ssl", false);
		if (isSSL) {
			props.put("mail.smtp.socketFactory.port", smtpPort);
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}

		String username = smtpConfigMap.getString("username", "");
		String password = smtpConfigMap.getString("password", "");
		if (username != null && username.trim().length() > 0) {
			props.put("mail.smtp.auth", "true");
			session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});
		} else {
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.auth.login.disable", "true");

			session = Session.getInstance(props, null);
		}

		// System.out.println("Finished setting up emailbroadcaster object");
	}

	/**
	 * Full featured sendEmail function, all other "overloaded" functions are convinence functiosn based on this
	 *
	 * @param  email subject
	 * @param  email HTML content
	 * @param  sending to email addresses array
	 * @param  CC to email addresses array
	 * @param  BCC to email adresses array
	 * @param  File attachment Map<String filename, String absolute file path>
	 * @param  Overwrite default, "from address"
	 **/
	public boolean sendEmail(String subject, String htmlContent, String toAddresses[],
							 String ccAddresses[], String bccAddresses[], Map<String, String> fileAttachments,
							 String fromAddress, Map<String, Object> inextraSendOptions) throws Exception {
		// Actual message contianer used by the function
		MimeMessage message = new MimeMessage(session);

		if (inextraSendOptions == null) {
			inextraSendOptions = new HashMap<String, Object>();
		}
		GenericConvertMap<String, Object> extraSendOptions = ProxyGenericConvertMap
				.ensure(inextraSendOptions);

		// Process "FROM" address field
		if (fromAddress != null) {
			message.setFrom(new InternetAddress(fromAddress));
		} else {
			message.setFrom(new InternetAddress(fromEmail));
		}

		// Process "TO" address field
		if (toAddresses == null || toAddresses.length <= 0) {
			throw new Exception("Sending to email address is not allowed to be 'empty'");
		}
		InternetAddress[] addressTo = new InternetAddress[toAddresses.length];
		for (int i = 0; i < toAddresses.length; i++) {
			addressTo[i] = new InternetAddress(toAddresses[i]);
		}
		message.setRecipients(RecipientType.TO, addressTo);

		// Process "CC" address field
		if (ccAddresses != null && ccAddresses.length > 0) {
			InternetAddress[] addressCC = new InternetAddress[ccAddresses.length];
			for (int i = 0; i < ccAddresses.length; i++) {
				addressCC[i] = new InternetAddress(ccAddresses[i]);
			}
			if (addressCC.length > 0) {
				message.setRecipients(RecipientType.CC, addressCC);
			}
		}

		String[] extraBcc = extraSendOptions.getStringArray("alwaysBCCto", new String[0]);
		extraBcc = parseExtraBccAddresses(extraBcc);
		boolean sendExtra = (extraBcc != null && extraBcc.length > 0);

		// Process "BCC" address field
		if ((bccAddresses != null && bccAddresses.length > 0) || sendExtra) {
			List<InternetAddress> combinedBccAddresses = new ArrayList<InternetAddress>();
			if (sendExtra) {
				for (int i = 0; i < extraBcc.length; ++i) {
					if (!extraBcc[i].isEmpty()) {
						combinedBccAddresses.add(new InternetAddress(extraBcc[i]));
					}
				}
			}
			if (bccAddresses != null) {
				for (int i = 0; i < bccAddresses.length; ++i) {
					combinedBccAddresses.add(new InternetAddress(bccAddresses[i]));
				}
			}

			InternetAddress[] finalBccAddresses = new InternetAddress[combinedBccAddresses.size()];
			combinedBccAddresses.toArray(finalBccAddresses); //converts to array and stores in finalBccAddresses

			if (finalBccAddresses.length > 0) {
				message.setRecipients(RecipientType.BCC, finalBccAddresses);
			}
		}

		// Set email SUBJECT
		message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));

		// Message container (for body, and attachments)
		Multipart multipart = new MimeMultipart();

		// Set email HTML CONTENT
		if (htmlContent != null) {
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(htmlContent, "text/html");
			multipart.addBodyPart(messageBodyPart);
		}

		// Loops through file attachments, and add it
		if (fileAttachments != null) {
			for (Map.Entry<String, String> entry : fileAttachments.entrySet()) {
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				String key = entry.getKey();
				String value = entry.getValue();

				DataSource source = new FileDataSource(value);
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(key);
				multipart.addBodyPart(messageBodyPart);
			}
		}

		// Apply message content
		message.setContent(multipart);

		//Sends the message
		Transport.send(message);

		return true;
	}

	/**
	 * Shorten convinence function (does not need testing, test the core function directly instead)
	 **/
	public boolean sendEmail(String subject, String htmlContent, String toAddresses[],
							 String ccAddresses[], String bccAddresses[], HashMap<String, String> fileAttachments)
			throws Exception {
		return sendEmail(subject, htmlContent, toAddresses, ccAddresses, bccAddresses,
				fileAttachments, null);

	}

	/**
	 * Shorten convinence function (does not need testing, test the core function directly instead)
	 **/
	public boolean sendEmail(String subject, String htmlContent, String toAddresses,
							 String ccAddresses, String bccAddresses, HashMap<String, String> fileAttachments)
			throws Exception {
		return sendEmail(subject, htmlContent, ((toAddresses != null) ? (toAddresses.split(","))
						: null), ((ccAddresses != null) ? (ccAddresses.split(",")) : null),
				((bccAddresses != null) ? (bccAddresses.split(",")) : null), fileAttachments, null);
	}

	/**
	 * Shorten convinence function (does not need testing, test the core function directly instead)
	 **/
	public boolean sendEmail(String subject, String htmlContent, String toAddresses,
							 HashMap<String, String> fileAttachments) throws Exception {
		return sendEmail(subject, htmlContent, ((toAddresses != null) ? (toAddresses.split(","))
				: null), null, null, fileAttachments, null);
	}

	/**
	 * Shorten convinence function (does not need testing, test the core function directly instead)
	 **/
	public boolean sendEmail(String subject, String htmlContent, String toAddresses[],
							 String ccAddresses[], String bccAddresses[], HashMap<String, String> fileAttachments,
							 Map<String, Object> inextraSendOptions) throws Exception {
		return sendEmail(subject, htmlContent, toAddresses, ccAddresses, bccAddresses,
				fileAttachments, null, inextraSendOptions);
	}

	//some utils
	private String[] parseExtraBccAddresses(String[] extraBcc) {
		String[] ret = null;
		if (extraBcc != null) {
			List<String> retList = new ArrayList<String>();
			for (int i = 0; i < extraBcc.length; ++i) {
				if (!extraBcc[i].isEmpty() && extraBcc[i].contains("@")) {
					retList.add(extraBcc[i]);
				}
			}
			if (retList.size() > 0) {
				ret = new String[retList.size()];
				retList.toArray(ret);
			}
		}
		return ret;
	}

	public void setAdminEmail(String email) {
		adminEmail = email;
	}

	public String getAdminEmail() {
		return adminEmail;
	}
}
