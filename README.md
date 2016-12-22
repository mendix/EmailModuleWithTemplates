# EmailModuleWithTemplates
This e-mail template module lets you send e-mails with or without templates. E-mail templates can be managed with the possibility of using tokens. With the tokens available in the email template, attributes and references of an object will be filled in automatically and correctly.

## Typical usage scenario
Send emails with an email template system and the possibility of using tokens to handle all your outgoing standard emails.

## Contributing
For more information on contributing to this repository visit Contributing to a GitHub repository

## Configuration
- Download the Mx Model reflection module from the App Store.
- Download and configure the Encryption module from the App Store.
- Add the snippet Administration to a custom page in a different module.
- View the example in the '_USE_ME > _Examples' folder, create a duplicate of it to your own module and make it fit your needs.
- Sub_CreateAndQueueEmail (preferred for normal environments, this will send the email in the background using a scheduled event)
- Sub_CreateAndSendEmail (preferred for sandbox environments, sends an email directly; this approach will block the users flow and doesn't include retry when sending failed).
- Post-deployment: After deploying, you have to set up your email settings and insert your own email templates using the newly created navigation items under Administrator.
- Go to the 'MxObjects_Overview' and synchronize the objects. Make sure you do this every time when you have added new objects.
 
## Dependencies

**Mendix modules**
- MXModelReflection module
- Encryption module

**Java libraries:**
- commons-codec-1.10.jar
~~- commons-email-1.3.1~~
- commons-email-1.4.0.jar
- com.sun.mail.javax.mail-1.4.5.jar
