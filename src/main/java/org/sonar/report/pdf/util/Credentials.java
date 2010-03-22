/*
 * Sonar PDF Plugin, open source plugin for Sonar
 * Copyright (C) 2009 GMV-SGI
 * Copyright (C) 2010 klicap - ingeniería del puzle
 *
 * Sonar PDF Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.report.pdf.util;

public class Credentials {

    private static String username = null;
    private static String password = null;

    /**
     * @return the username
     */
    public static String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public static void setUsername(String username) {
        Credentials.username = username;
    }

    /**
     * @return the password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public static void setPassword(String password) {
        Credentials.password = password;
    }

}
