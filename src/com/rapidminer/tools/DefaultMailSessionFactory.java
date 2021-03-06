/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2011 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.tools;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.rapidminer.RapidMiner;

/** Makes a session based on RapidMiner properties.
 * 
 * @author Simon Fischer
 *
 */
public class DefaultMailSessionFactory implements MailSessionFactory {

    @Override
    public Session makeSession() {
        String host = ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_TOOLS_SMTP_HOST);
        if (host == null) {
            LogService.getRoot().warning("Must specify SMTP host in "+RapidMiner.PROPERTY_RAPIDMINER_TOOLS_SMTP_HOST+" to use SMTP.");
            return null;
        } else {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.from", "no-reply@rapidminer.com");
            final String user = ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_TOOLS_SMTP_USER);
            props.put("mail.user", user);
            final String passwd = ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_TOOLS_SMTP_PASSWD);
            Authenticator authenticator = null;
            if (passwd != null && passwd.length() > 0) {
                props.setProperty("mail.smtp.submitter", user);
                props.setProperty("mail.smtp.auth", "true");
                authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, passwd);
                    }
                };
            }
            String port = ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_TOOLS_SMTP_PORT);
            if (port != null) {
                props.setProperty("mail.smtp.port", port);
            }
            return Session.getInstance(props, authenticator);
        }
    }

}
