package emailmodulewithtemplates.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.mail.EmailException;

import com.mendix.core.CoreException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

/**
 * 
 * This class can be used to send email. All methods behave in a similar way, there are
 * just multiple methods for your convenience.
 *
 */

public class EmailModule 
{
	/**
	 * Sends an email to <i>to</i> with subject <i>subject</i> and message <i>msg</i>. 
	 * @param msg The message to send
	 * @param subject The subject of the email
	 * @param to The recipient
	 * @throws CoreException
	 * @throws EmailException 
	 */
	public static void mail(SMTPConfiguration configuration, String msg, String subject, String to) throws CoreException, EmailException 
	{
		List<String> toAddresses = new ArrayList<String>();
		toAddresses.add(to);
		
		mail(configuration, msg, null, subject, toAddresses, null, null, null, null, null);
	}
	/**
	 * Sends an email to <i>to</i> with subject <i>subject</i> and message as html, the shown message will be <i>htmlmsg</i>. If the receipts don't support html messages the <i>plainmsg</i> will be shown.
	 * @param configuration
	 * @param htmlmsg
	 * @param plainmsg
	 * @param subject
	 * @param to
	 * @throws CoreException
	 * @throws EmailException 
	 */
	public static void mail(SMTPConfiguration configuration, String htmlmsg, String plainmsg, String subject, String to) throws CoreException, EmailException 
	{
		List<String> toAddresses = new ArrayList<String>();
		toAddresses.add(to);
		
		mail(configuration, htmlmsg, plainmsg, subject, toAddresses, null, null, null, null, null);
	}
	
	/**
	 * @param configuration an SMTPConfiguration which contains all information to send your email.
	 * @param msg the message body that will be send.
	 * @param subject the subject of the email.
	 * @param to the recipient of the email message.
	 * @param context this context will be used to check if the current user has the rights to access to the attachments.
	 * @param attachment the attachment to be sent along with the email.
	 * @throws EmailException 
	 */
	public static void mail(SMTPConfiguration configuration, String msg, String subject, String to, IContext context, IMendixObject attachment) throws CoreException, EmailException 
	{
		List<IMendixObject> attachments = new ArrayList<IMendixObject>();
		attachments.add(attachment);
		
		List<String> toAddresses = new ArrayList<String>();
		toAddresses.add(to);
		
		mail(configuration, msg, subject, toAddresses, null, null, context, attachments);
	}
	
	/**
	 * @param configuration an SMTPConfiguration which contains all information to send your email.
	 * @param htmlmsg the message body as html text this is what most of the recipients will see. 
	 * @param plainmsg the message in plain text (this will be shown if the recipient doesn't accept html messages).
	 * @param subject the subject of the email.
	 * @param to the recipient of the email message.
	 * @param context this context will be used to check if the current user has the rights to access to the attachments. 
	 * @param attachment the attachment to be sent along with the email.
	 * @throws EmailException 
	 */
	public static void mail(SMTPConfiguration configuration, String htmlmsg, String plainmsg, String subject, String to, IContext context, IMendixObject attachment) throws CoreException, EmailException 
	{
		List<IMendixObject> attachments = new ArrayList<IMendixObject>();
		attachments.add(attachment);
		
		List<String> toAddresses = new ArrayList<String>();
		toAddresses.add(to);
		
		mail(configuration, htmlmsg, plainmsg, subject, toAddresses, null, null, context, attachments, null);
	}

	/**
	 * Sends an email to <i>to, cc and bcc</i> with subject <i>subject</i> and message <i>msg</i> width attachments <i>attachments</i> using settings in <i>configuration</i>.
	 * @param configuration an SMTPConfiguration to send your email with.
	 * @param msg the message to send.
	 * @param subject the subject of the email.
	 * @param to the recipients of the email message.
	 * @param cc the CC recipients.
	 * @param bcc the BCC recipients.
	 * @param context this context will be used to check if the current user has the rights to access to the attachments. 
	 * @param attachment the attachment you want to send along with the email.
	 * @throws EmailException 
	 */
	public static void mail(SMTPConfiguration configuration, String msg, String subject, List<String> to, List<String> cc, List<String> bcc, IContext context, List<IMendixObject> attachments) throws CoreException, EmailException 
	{
		mail(configuration, null, msg, subject, to, cc, bcc, context, attachments, null);
	}
	
	/**
	 * @param configuration, An SMTPConfiguration which contains all information to send your email
	 * @param htmlmsg  the message body as html text this is what most of the recipients will see. 
	 * @param plainmsg the message in plain text (this will be shown if the recipient doesn't accept html messages).
	 * @param subject the subject of the email.
	 * @param to the recipients of the email message.
	 * @param cc the CC recipients.
	 * @param bcc the BCC recipients.
	 * @param context this context will be used to check if the current user has the rights to access to the attachments. 
	 * @param attachments the attachments to be sent along with the email.
	 * @throws EmailException 
	 */
	public static void mail(final SMTPConfiguration configuration, String htmlmsg, 
			String plainmsg, final String subject, final List<String> to, final List<String> cc, 
			final List<String> bcc, IContext context, final List<IMendixObject> attachments,
			final Map<String, String> headers) throws CoreException, EmailException 
	{
		final Message message = new Message();
		
		if(plainmsg != null) {
			message.setPlainBody(plainmsg);
		}
		
		if(htmlmsg != null) {
			message.setHtmlBody(htmlmsg);
		}
		
		final Sender sender = new Sender(context);
		sender.send(configuration, message, subject, to, cc, bcc, attachments, headers);
	}
}