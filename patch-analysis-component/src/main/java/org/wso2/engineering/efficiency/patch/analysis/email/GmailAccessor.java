/*
Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
WSO2 Inc. licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package org.wso2.engineering.efficiency.patch.analysis.email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.ConnectionException;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisDataException;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.impl.SendEmailsServiceImpl;
import org.wso2.engineering.efficiency.patch.analysis.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends an email with the JIRA issues and associated patch information.
 */
public class GmailAccessor {

    private static GmailAccessor gmailAccessor = new GmailAccessor();

    private GmailAccessor() {

    }

    public static GmailAccessor getInstance() {

        return gmailAccessor;
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If there is no client_secret.
     */
    private Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {

        try (InputStream in = SendEmailsServiceImpl.class.getResourceAsStream(Constants.Email.CLIENT_SECRET_DIR)) {
            GoogleClientSecrets clientSecrets;
            clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in,
                    Charset.defaultCharset()));
            GoogleAuthorizationCodeFlow flow;
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JacksonFactory.getDefaultInstance(), clientSecrets, Constants.Email.SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(
                            new java.io.File((Constants.Email.CREDENTIALS_FOLDER))))
                    .setAccessType("offline")
                    .build();
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        }
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param subject  subject of the email.
     * @param bodyText body text of the email.
     * @return the MimeMessage to be used to send email.
     * @throws PatchAnalysisDataException email was not created.
     */
    private MimeMessage createEmail(String subject, String bodyText, String emailFrom, String emailTo, String emailCC)
            throws PatchAnalysisDataException {

        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);
            email.setFrom("Engineering Efficiency <" + emailFrom + ">");
            String[] toList = emailTo.split(",");
            for (String aToList : toList) {
                email.addRecipient(javax.mail.Message.RecipientType.TO,
                        new InternetAddress(aToList));
            }
            String[] ccList = emailCC.split(",");
            for (String aCcList : ccList) {
                email.addRecipient(javax.mail.Message.RecipientType.CC,
                        new InternetAddress(aCcList));
            }
            email.setSubject(subject);
            email.setContent(bodyText, Constants.Email.EMAIL_TYPE);
            return email;
        } catch (MessagingException e) {
            throw new PatchAnalysisDataException("Failed to set up email", e);
        }
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent email to be set to raw of message.
     * @return a message containing a base64url encoded email.
     * @throws PatchAnalysisDataException failed to create Message.
     */
    private Message createMessageWithEmail(MimeMessage emailContent) throws PatchAnalysisDataException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            emailContent.writeTo(buffer);
        } catch (IOException | MessagingException e) {
            throw new PatchAnalysisDataException("Failed to extract email content from MimeMessage object", e);
        }
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Sends an email from the user's mailbox to its recipients.
     *
     * @param emailBody body of email.
     * @param subject   subject of the email.
     * @throws PatchAnalysisException email was not sent.
     */
    public void sendMessage(String emailBody, String subject, String emailFrom, String emailTo, String emailCC)
            throws PatchAnalysisException {

        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(httpTransport, JacksonFactory.getDefaultInstance(),
                    getCredentials(httpTransport))
                    .setApplicationName(Constants.Email.APPLICATION_NAME)
                    .build();
            MimeMessage emailContent = createEmail(subject, emailBody, emailFrom, emailTo, emailCC);
            Message message = createMessageWithEmail(emailContent);
            service.users().messages().send("me", message).execute();
        } catch (GeneralSecurityException | IOException e) {
            throw new ConnectionException("Failed to send email", e);
        }
    }
}
