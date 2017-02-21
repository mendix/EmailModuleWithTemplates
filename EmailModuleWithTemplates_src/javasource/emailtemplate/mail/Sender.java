package emailtemplate.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Authenticator;
import javax.mail.Session;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.mail.DataSourceResolver;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.MendixRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

public class Sender 
{
	static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private final static String FILE_DOCUMENT_NAME = "Name";
	private final static String FILE_DOCUMENT = "System.FileDocument";
	
	private final IContext context;
	
	public Sender(IContext _context) 
	{
		this.context = _context;
	}
	
	public void send(SMTPConfiguration configuration, Message message, String subject, List<String> toAddressList, 
			List<String> ccAddressList, List<String> bccAddressList, List<IMendixObject> attachments,
			Map<String, String> headers) 
					throws CoreException, EmailException 
	{
		ImageHtmlEmail mail = new ImageHtmlEmail();
	    mail.setCharset(EmailConstants.UTF_8);
	    
	    for (Entry<String, String> header : headers.entrySet()) {
	    	mail.addHeader(header.getKey(), header.getValue());
	    }
	    
	    // this resolves data uris of inline images
	    mail.setDataSourceResolver(new DataURIResolver());
		
	    this.setConnectionInfo(mail, configuration);

	    // set basic message content
		mail.setSubject(subject);
		
        if(message.getHtmlBody() != null) {
        	if(message.getPlainBody() != null) {
                mail.setTextMsg(message.getPlainBody());
            }
            mail.setHtmlMsg(message.getHtmlBody());
        }
        else {
        	mail.setMsg(message.getPlainBody());
        }
        	
        
        // add sender
        String fromAddress = configuration.getFromAddress();
		try {
		    mail.setFrom(fromAddress); 
	    } catch (EmailException e) {
			throw new CoreException("Incorrect from address: " + fromAddress + ".", e);
		}
		
		String replyToAddress = configuration.getReplyToAddress();
		if (replyToAddress != null && !"".equals(replyToAddress)) {
			try {
				mail.addReplyTo(replyToAddress);
			} catch (EmailException e) {
				throw new CoreException("Incorrect reply to address: " + replyToAddress + ".", e);
			}
		}
		
		// add addressees
		if(toAddressList == null && ccAddressList == null && bccAddressList == null) {
			throw new CoreException("No recipients given, cannot send email.");
		}
		
		if(toAddressList != null) {
			for (String toAddress : toAddressList) {
				mail.addTo(toAddress);
			}
		}
		
		if(ccAddressList != null) {
			for (String ccAddress : ccAddressList) {
				mail.addCc(ccAddress);
			}
		}
		
		if(bccAddressList != null) {
			for (String bccAddress : bccAddressList) {
				mail.addBcc(bccAddress);
			}
		}
		mail.setSentDate(new Date());
		
		// add attachments
        setAttachments(attachments, mail);
       
        // send message
        mail.send();
	}
	
	private void setAttachments(List<IMendixObject> attachments, MultiPartEmail multipart) throws CoreException 
	{
		if(this.context == null && attachments != null)
			throw new MendixRuntimeException("Context should not be null when sending e-mails with attachments");
		
		if(attachments != null && this.context != null) {
			int i = 1;
		
			for (IMendixObject attachment : attachments) {
				if(attachment != null) {
					if(!Core.isSubClassOf(FILE_DOCUMENT, attachment.getType())) {
						throw new CoreException("Attachment is no fileDocument");
					}
					
					String mimeType = (new MimetypesFileTypeMap()).getContentType((String) attachment.getValue(this.context, FILE_DOCUMENT_NAME));
					InputStream content = Core.getFileDocumentContent(this.context, attachment);
		
					try {
						if(content != null && content.available() > 0) {
							DataSource source = new ByteArrayDataSource(content, mimeType);
							String fileName = (String) attachment.getValue(this.context, FILE_DOCUMENT_NAME);
							
							if("".equals(fileName)) fileName = "Attachment" + i;
							
							multipart.attach(source, fileName, fileName);
						}
					
					} catch (Exception e) 
					{
						throw new CoreException("Unable to attach attachment " + (String) attachment.getValue(this.context, FILE_DOCUMENT_NAME) + ".", e);
					}
				}
				i++;
			}
		}
	}
	
	private void setConnectionInfo(Email mail, SMTPConfiguration configuration) 
	{
	    mail.setHostName(configuration.getSMTPHost());
	    mail.setSmtpPort(configuration.getSMTPPort());
	    Authenticator authenticator = null;
	    if (configuration.getUserName() != null && !configuration.getUserName().equals("")) {
	        authenticator = new DefaultAuthenticator(configuration.getUserName(), configuration.getUserPass());
	        mail.setAuthenticator(authenticator);
	    }
	    
	    if(configuration.useSSLSMTP()) {
            mail.setSSLOnConnect(true);
            mail.setSslSmtpPort(Integer.toString(configuration.getSMTPPort()));
	    }
	    
	    if(configuration.useTLSSMTP()) {
	        mail.setStartTLSEnabled(true);
	    }
	    
	    /* because the default session mechanisms uses System.getProperties() (which is not allowed 
	     * in the cloud), we need to construct a session ourselves. The code to do this was lifted
	     * from Email.java in the commons-email package.
	     */
	    Properties properties = new Properties();
        properties.setProperty(EmailConstants.MAIL_TRANSPORT_PROTOCOL, EmailConstants.SMTP);

        properties.setProperty(EmailConstants.MAIL_PORT, mail.getSmtpPort());
        properties.setProperty(EmailConstants.MAIL_HOST, mail.getHostName());

        properties.setProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE,
                mail.isStartTLSEnabled() ? "true" : "false");
        properties.setProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_REQUIRED,
                mail.isStartTLSRequired() ? "true" : "false");

        if (authenticator != null) {
            properties.setProperty(EmailConstants.MAIL_SMTP_AUTH, "true");
        }

        if (mail.isSSLOnConnect()) {
            properties.setProperty(EmailConstants.MAIL_PORT, mail.getSslSmtpPort());
            properties.setProperty(EmailConstants.MAIL_SMTP_SOCKET_FACTORY_PORT, mail.getSslSmtpPort());
            properties.setProperty(EmailConstants.MAIL_SMTP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
            properties.setProperty(EmailConstants.MAIL_SMTP_SOCKET_FACTORY_FALLBACK, "false");
        }

        if ((mail.isSSLOnConnect() || mail.isStartTLSEnabled()) && mail.isSSLCheckServerIdentity()) {
            properties.setProperty(EmailConstants.MAIL_SMTP_SSL_CHECKSERVERIDENTITY, "true");
        }

        if (configuration.getFromAddress() != null) {
            properties.setProperty(EmailConstants.MAIL_SMTP_FROM, configuration.getFromAddress());
        }

        if (mail.getSocketTimeout() > 0) {
            properties.setProperty(EmailConstants.MAIL_SMTP_TIMEOUT, Integer.toString(mail.getSocketTimeout()));
        }

        if (mail.getSocketConnectionTimeout() > 0) {
            properties.setProperty(EmailConstants.MAIL_SMTP_CONNECTIONTIMEOUT, Integer.toString(mail.getSocketConnectionTimeout()));
        }

        Session session = Session.getInstance(properties, authenticator);
        
        mail.setMailSession(session);
	}
	
	private class DataURIResolver implements DataSourceResolver {
        @Override
        public DataSource resolve(String resourceLocation) throws IOException {
            return resolve(resourceLocation, true);
        }

        @Override
        public DataSource resolve(String resourceLocation, boolean isLenient) throws IOException {
            DataSource result = null;
            if (resourceLocation.startsWith("data:")) {
                // format: data:[<MIME-type>][;charset=<encoding>][;base64],<data>
                // example: data:image/jpeg;base64,snjgsndg
                String mimeType = resourceLocation.substring(5, // after "data:"
                        resourceLocation.indexOf(';'));         // to first ';'
                String data = resourceLocation.substring(resourceLocation.indexOf(',')+1);
                // Assume Base64 encoding for now
                result = new Base64DataSource(data, mimeType);
            }

            return result;
        }
	}

	private class Base64DataSource implements DataSource {
        String data;
        String mimeType;
        String name;
        
        public Base64DataSource(String data, String mimeType) {
            super();
            this.data = data;
            this.mimeType = mimeType;
            this.name = UUID.randomUUID().toString(); 
        }
        
        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            InputStream stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
            return new Base64InputStream(stream);
        }
        
        @Override
        public String getContentType() {
            return mimeType;
        }
	}
}