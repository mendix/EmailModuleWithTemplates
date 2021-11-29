package emailmodulewithtemplates.mail;

public class Message 
{
	private String plain;
	private String html;

	/**
	 * @return the plain
	 */
	public String getPlainBody() 
	{
		return this.plain;
	}

	/**
	 * @param plain the plain to set
	 */
	public void setPlainBody(String plain) 
	{
		this.plain = plain;
	}

	/**
	 * @return the html
	 */
	public String getHtmlBody() 
	{
		return this.html;
	}

	/**
	 * @param html the html to set
	 */
	public void setHtmlBody(String html) 
	{
		this.html = html;
	}
}