package daora;

import java.io.File;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendAluguelToSantiago
{
    private static final String MY_EMAIL = "<YOUR-EMAIL>";
    private static final String MY_PASSWORD = "<YOUR-PASSWORD>";
    private static final String SANTIAGO_EMAIL = "<SANTIAGO'S-EMAIL>";
    private static final String ALUGUEL = "src/main/resources/aluguel.jpg";

    public static void main(
        final String[] args )
        throws Exception
    {
        final int numberOfAlugueisToSend = 10;
        send( numberOfAlugueisToSend );
    }

    public static void send(
        final int numberOfAlugueisToSend )
        throws Exception
    {
        final Properties properties = getEmailProperties();
        final Session session = Session.getInstance( properties, buildPasswordAuthenticator() );
        final Message message = buildMessage( session, new File( ALUGUEL ) );
        for( int aluguel = 0; aluguel < numberOfAlugueisToSend; aluguel++ ) {
            System.out.println( "sending aluguel..." );
            Transport.send( message );
        }
    }

    private static Message buildMessage(
        final Session session,
        final File aluguel )
        throws Exception
    {
        final Message message = new MimeMessage( session );
        message.setFrom( new InternetAddress( MY_EMAIL ) );
        message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( SANTIAGO_EMAIL ) );
        message.setSubject( "ow mano, olha q imagem foda" );
        message.setContent( buildMultipartFromAluguel( aluguel ) );
        message.setSentDate( Date.from( Instant.now() ) );
        return message;
    }

    private static Multipart buildMultipartFromAluguel(
        final File aluguel )
        throws Exception
    {
        final MimeBodyPart messagePart = new MimeBodyPart();
        messagePart.setContent( "<img src=\"cid:aluguel\" />", "text/html" );
        final MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.setHeader( "Content-ID", "aluguel" );
        imagePart.setDisposition( MimeBodyPart.INLINE );
        imagePart.attachFile( aluguel );
        final Multipart multipart = new MimeMultipart();
        multipart.addBodyPart( messagePart );
        multipart.addBodyPart( imagePart );
        return multipart;
    }

    private static Authenticator buildPasswordAuthenticator()
    {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication( MY_EMAIL, MY_PASSWORD );
            }
        };
    }

    private static Properties getEmailProperties()
    {
        final Properties properties = System.getProperties();
        properties.put( "mail.smtp.host", "smtp.office365.com" );
        properties.put( "mail.smtp.auth", "true" );
        properties.put( "mail.smtp.port", "587" );
        properties.put( "mail.smtp.starttls.enable", "true" );
        return properties;
    }
}