package emailtemplate.mail;

public class SMTPConfiguration 
{
	private int SMTPPort;
	private String SMTPHost;
	
	private String userName;
	private String userPass;
	
	private boolean useSSLSMTP;
	private boolean useSSLCheckServerIdentity;
	private boolean useTLSSMTP;
	
	private String fromAddress;
	private String fromDisplayName;
	private String replyToAddress;
	
	public SMTPConfiguration() 
	{
		// Basic configuration
		this.useSSLSMTP = false;
		this.useTLSSMTP = false;
		this.useSSLCheckServerIdentity = false;
		this.SMTPPort = 25;
		this.SMTPHost = "";
		this.userName = "";
		this.userPass = "";
		this.fromAddress = "";
		this.fromDisplayName = "";
		this.replyToAddress = "";
	}
		
	public boolean useSSLSMTP() 
	{
		return this.useSSLSMTP;
	}
	
	public void setUseSSLSMTP(boolean useSSLSMTP) 
	{
		this.useSSLSMTP = useSSLSMTP;
	}

	public boolean useTLSSMTP() 
	{
		return this.useTLSSMTP;
	}
		
	public void setUseTLSSMTP(boolean useTLSSMTP) {
		this.useTLSSMTP = useTLSSMTP;		
	}
	
	public int getSMTPPort() 
	{
		return this.SMTPPort;
	}

	public void setSMTPPort(int port) 
	{
		this.SMTPPort = port;
	}

	public String getSMTPHost() 
	{
		return this.SMTPHost;
	}

	public void setSMTPHost(String host) 
	{
		this.SMTPHost = host;
	}

	public String getUserName() 
	{
		return this.userName;
	}
	
	public void setUserName(String userName) 
	{
		this.userName = userName;
	}

	public String getUserPass() 
	{
		return this.userPass;
	}
	
	public void setUserPass(String userPass) 
	{
		this.userPass = userPass;
	}
	
	public String getFromAddress() 
	{
		return this.fromAddress;
	}

	public void setFromAddress(String fromAddress) 
	{
		this.fromAddress = fromAddress;
	}

	public String getFromDisplayName() 
	{
		return this.fromDisplayName;
	}

	public void setFromDisplayName(String fromDisplayName) 
	{
		this.fromDisplayName = fromDisplayName;
	}

	
	public String getReplyToAddress() 
	{
		return this.replyToAddress;
	}

	public void setReplyToAddress(String replyToAddress) 
	{
		this.replyToAddress = replyToAddress;
	}

	public boolean useSSLCheckServerIdentity() 
	{
		return this.useSSLCheckServerIdentity;
	}

	public void setUseSSLCheckServerIdentity(boolean useSSLCheckServerIdentity) 
	{
		this.useSSLCheckServerIdentity = useSSLCheckServerIdentity;
	}


}