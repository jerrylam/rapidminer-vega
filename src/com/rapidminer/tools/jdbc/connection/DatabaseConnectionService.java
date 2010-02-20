/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2010 by Rapid-I and the contributors
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
package com.rapidminer.tools.jdbc.connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.cipher.CipherTools;
import com.rapidminer.tools.jdbc.DatabaseHandler;
import com.rapidminer.tools.jdbc.DatabaseService;

/**
 * The central service for registering DatabaseConnections. They are used for
 * connection selection on all database related operators as well as for the import
 * wizards.
 * 
 * @author Tobias Malbrecht, Sebastian Land
 */
public class DatabaseConnectionService {
	
	public static final String PROPERTY_CONNECTIONS_FILE = "connections";
	
	private static List<FieldConnectionEntry> connections = new LinkedList<FieldConnectionEntry>();
	
	private static DatabaseHandler handler = null;

	public static void init() {
		File connectionsFile = getConnectionsFile(); 
		if (!connectionsFile.exists()) {
			try {
				connectionsFile.createNewFile();
			} catch (IOException ex) {
				// do nothing
			}
		} else {
			connections = readConnectionEntries(connectionsFile);
		}
	}
	
	private static File getConnectionsFile() {
		return ParameterService.getUserConfigFile(PROPERTY_CONNECTIONS_FILE);
	}
	
	public static Collection<FieldConnectionEntry> getConnectionEntries() {
		return connections;
	}
	
	public static ConnectionEntry getConnectionEntry(String name) {
		for (ConnectionEntry entry : connections) {
			if (entry.getName().equals(name)) {
				return entry;
			}
		}
		return null;
	}

	public static void addConnectionEntry(FieldConnectionEntry entry) {
		connections.add(entry);
		Collections.sort(connections, ConnectionEntry.COMPARATOR);
		writeConnectionEntries(connections);
	}

	public static void deleteConnectionEntry(ConnectionEntry entry) {
		connections.remove(entry);
		if (entry != null) {
			writeConnectionEntries(connections);
		}
	}

//	public static void renameConnectionEntry(ConnectionEntry entry, String name) {
//		if (entry != null) {
//			entry.setName(name);
//			writeConnectionEntries(connections, connectionsFile);		
//		}
//	}
	
	public static List<FieldConnectionEntry> readConnectionEntries(File connectionEntriesFile) {
		LinkedList<FieldConnectionEntry> connectionEntries = new LinkedList<FieldConnectionEntry>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(connectionEntriesFile));
			String line = in.readLine();
			if (line != null) {
				int numberOfEntries = Integer.parseInt(line);
				for (int i = 0; i < numberOfEntries; i++) {
					String name = in.readLine();
					String system = in.readLine();
					String host = in.readLine();
					String port = in.readLine();
					String database = in.readLine();
					String user = in.readLine();
					String password = CipherTools.decrypt(in.readLine());
					if (name != null && system != null) {
						connectionEntries.add(new FieldConnectionEntry(name, DatabaseService.getJDBCProperties(system), host, port, database, user, password.toCharArray()));
					}
				}
			}
			in.close();
			Collections.sort(connectionEntries, ConnectionEntry.COMPARATOR);
		} catch (Exception e) {
			connectionEntries.clear();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// should not happen
				}
			}
		}
		return connectionEntries;
	}
	
	public static void writeConnectionEntries(Collection<FieldConnectionEntry> connectionEntries) {
		File connectionEntriesFile = getConnectionsFile();
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(connectionEntriesFile));
			// searching number of not dynamic entries to store it's number
			int numberOfEntries = 0;
			for (FieldConnectionEntry entry : connectionEntries) {
				if (!entry.isDynamic())
					numberOfEntries++;
			}
			out.println(numberOfEntries);
			
			// outputting each single non dynamic entry
			for (FieldConnectionEntry entry : connectionEntries) {
				if (!entry.isDynamic()) {
					out.println(entry.getName());
					out.println(entry.getProperties().getName());
					out.println(entry.getHost());
					out.println(entry.getPort());
					out.println(entry.getDatabase());
					out.println(entry.getUser());
					out.println(CipherTools.encrypt(new String(entry.getPassword())));
				}
			}
			out.close();
		} catch (Exception e) {
			// do nothing
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	public static boolean testConnection(ConnectionEntry entry) throws SQLException {
    	if (entry != null) {
    		if (handler != null) {
    			handler.disconnect();
    		}
            handler = DatabaseHandler.getConnectedDatabaseHandler(entry);
            if (handler != null) {
            	handler.disconnect();
            }
            return true;
    	}
    	return false;
	}
	
//	public static void main(String[] args) throws SQLException {
//		RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.EMBEDDED_WITHOUT_UI);
//		DatabaseService.init();
//		DatabaseConnectionService.init();
//		System.out.println("#entries = "+DatabaseConnectionService.getConnectionEntries());
//		for (ConnectionEntry entry : DatabaseConnectionService.getConnectionEntries()) {
//			DatabaseHandler handler = DatabaseConnectionService.connect(entry);
//			Connection con = handler.getConnection();
//			DatabaseMetaData meta = con.getMetaData();
//			ResultSet typeResult = meta.getTypeInfo();
//			ResultSetMetaData rsmd = typeResult.getMetaData();
//			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//				System.out.println(rsmd.getColumnName(i));
//			}
//			while (typeResult.next()) {
//				System.out.print("data type: "+typeResult.getString("DATA_TYPE"));
//				System.out.println("; type name: "+typeResult.getString("TYPE_NAME"));
//				int type = Types.BIGINT;
//			}
//		}
//	}
}
