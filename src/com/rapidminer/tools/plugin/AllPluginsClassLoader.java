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
package com.rapidminer.tools.plugin;

import com.rapidminer.RapidMiner;

/** A class loader that consecutively tries to load classes from all registered plugins. 
 *  It starts with the system class loader and then tries all plugins in the order as returned
 *  by {@link Plugin#getAllPlugins()}. 
 *  
 *  TODO: implement {@link #getResource(String)}?
 * @author Simon Fischer
 *
 */
public class AllPluginsClassLoader extends ClassLoader {

	public AllPluginsClassLoader() {
		super(RapidMiner.class.getClassLoader());
	}
	
	@Override
	public Class<?extends Object> loadClass(String name) throws ClassNotFoundException {
//		try {
//			return AllPluginsClassLoader.class.getClassLoader().loadClass(name);
//		} catch (ClassNotFoundException notFound) {
//		}
		for (Plugin plugin : Plugin.getAllPlugins()) {
			ClassLoader classLoader = plugin.getClassLoader();
			try {
				return classLoader.loadClass(name);
			} catch (ClassNotFoundException notFound) {
			}
		}		
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader != null) {
			return contextClassLoader.loadClass(name);
		} else {
			throw new ClassNotFoundException(name);
		}
	}
}

