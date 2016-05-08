/**
 * 
 */
package com.fourvector.apps.dabble.web.util;

import java.util.Calendar;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * @author asharma
 */
public class DabbleTypeAdapterFactory implements TypeAdapterFactory {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DabbleTypeAdapterFactory.class);

	/**
	 * 
	 */
	public DabbleTypeAdapterFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.TypeAdapterFactory#create(com.google.gson.Gson, com.google.gson.reflect.TypeToken)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (type.getRawType() == Date.class) {
			return (TypeAdapter<T>) new DabbleDateTypeAdapter();
		}

		if (type.getRawType() == Calendar.class) {
			return (TypeAdapter<T>) new DabbleCalendarTypeAdapter();
		}

		return null;
	}
}
