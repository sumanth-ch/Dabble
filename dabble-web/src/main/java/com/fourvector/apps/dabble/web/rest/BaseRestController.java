/**
 * 
 */
package com.fourvector.apps.dabble.web.rest;

import com.fourvector.apps.dabble.web.util.DabbleTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author asharma
 */
public class BaseRestController {
	public static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new DabbleTypeAdapterFactory()).setPrettyPrinting().create();

	/**
	 * 
	 */
	public BaseRestController() {
		super();
	}

}
