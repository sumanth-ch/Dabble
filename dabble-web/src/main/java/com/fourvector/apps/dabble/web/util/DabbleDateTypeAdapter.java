/**
 * 
 */
package com.fourvector.apps.dabble.web.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * @author asharma
 */
public class DabbleDateTypeAdapter extends TypeAdapter<Date> {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DabbleDateTypeAdapter.class);

	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

	@Override
	public void write(JsonWriter out, Date value) throws IOException {
		if (value == null) {
			LOG.debug("Recieved Null Date Object, not translating.");
			String s = null;
			out.value(s);
		} else {
			out.value(simpleDateFormat.format(value));
		}
	}

	@Override
	public Date read(JsonReader in) throws IOException {
		String entry = in.nextString();
		LOG.debug("parsing String {} as date", entry);
		Date result = null;
		try {
			result = simpleDateFormat.parse(entry);
		} catch (ParseException e) {
			LOG.error("Found ParseException in method read", e);
		}
		LOG.debug("String {} parsed as date {}", entry, result);
		return result;
	}

}
