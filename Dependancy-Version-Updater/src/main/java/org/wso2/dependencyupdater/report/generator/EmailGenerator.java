/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.dependencyupdater.report.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.dependencyupdater.Constants;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
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

/**
 * Contains methods for sending email with generated reports
 */
public class EmailGenerator {

    private static final Logger log = LoggerFactory.getLogger(EmailGenerator.class);

    /**
     * Setting up the email connection to send report
     *
     * @return
     */
    private static Message setUp() {

        Properties props = new Properties();
        props.put("mail.smtp.socketFactory.port", Constants.EMAIL.MAILPORT);
        props.put("mail.smtp.socketFactory.class", Constants.EMAIL.MAILCLASS);
        props.put("mail.smtp.auth", Constants.EMAIL.MAILAUTH);
        props.put("mail.smtp.host", Constants.EMAIL.MAILSERVER);
        props.put("mail.smtp.port", Constants.EMAIL.MAILPORT);
        Session session;
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(Constants.EMAIL.EMAIL_USERNAME, Constants.EMAIL.EMAIL_PASSWORD);
            }
        });
        try {
            Message message = new MimeMessage(session);
            try {
                message.setFrom(new InternetAddress(Constants.EMAIL.EMAIL_USERNAME, Constants.EMAIL.EMAIL_TITLE));
            } catch (UnsupportedEncodingException ex) {
                message.setFrom(new InternetAddress(Constants.EMAIL.EMAIL_USERNAME));
            }
            return message;
        } catch (MessagingException e) {
            log.error("Exception", e);
            throw new RuntimeException("Email setup failed", e);
        }
    }

    /**
     * Method for sending email for a given receiver with set of generated reports
     *
     * @param receiverEmail email address of receiver
     * @param filePath      directory containing the reports
     * @param timestamp     timestamp to indicate the date and time dependency update happened
     * @return boolean value indicating the success of email sending process
     */
    public boolean sendEmail(String receiverEmail, String filePath, long timestamp) {

        Date date = new Date(timestamp);
        ArrayList<String> listOfFiles = getAllFiles(filePath);
        if (listOfFiles.size() != 0) {
            try {
                Message message = setUp();
                message.setFrom(new InternetAddress(Constants.EMAIL.EMAIL_USERNAME));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
                message.setSubject(Constants.EMAIL.EMAIL_TITLE + date.toString());
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText("Please find the attached reports.");
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);

                for (String filename : listOfFiles) {
                    DataSource source = new FileDataSource(filePath + File.separator + filename);
                    messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(filename);
                    multipart.addBodyPart(messageBodyPart);
                }
                message.setContent(multipart);
                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                log.error("Failed to sent report email using port {} ", Constants.EMAIL.MAILPORT, e);

            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No reports generated for the component.");
            }
            ;
        }

        return false;

    }

    /**
     * Retrieve all the files in a given directory
     *
     * @param directory Path to the report directory
     * @return List of file names in the directory
     */
    private ArrayList<String> getAllFiles(String directory) {

        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileList = new ArrayList<>();
        if (fileList.size() != 0) {
            for (int index = 0; index < listOfFiles.length; index++) {
                if (listOfFiles[index].isFile()) {
                    fileList.add(listOfFiles[index].getName());
                }
            }
        }

        return fileList;
    }

}
