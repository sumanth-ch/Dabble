/**
 * 
 */
package com.fourvector.apps.dabble.web.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * @author asharma
 */
public class DabbleCalendarTypeAdapter extends TypeAdapter<Calendar> {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DabbleCalendarTypeAdapter.class);

	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

	@Override
	public void write(JsonWriter out, Calendar value) throws IOException {
		if (value == null) {
			LOG.debug("Recieved Null Calendar Date Object, not translating.");
			String s = null;
			out.value(s);
		} else {
			out.value(simpleDateFormat.format(value.getTime()));
		}
	}

	public static void main(String[] args) throws Exception{
		String val = "20-OCT-2015 18:15:43 EST";
		System.out.println(simpleDateFormat.parse(val));

	}

	@Override
	public Calendar read(JsonReader in) throws IOException {
		String entry = in.nextString();
		LOG.debug("parsing String {} as Calendar date", entry);
		Calendar result = new GregorianCalendar();
		try {
			result.setTime(simpleDateFormat.parse(entry));
		} catch (ParseException e) {
			LOG.error("Found ParseException in method read", e);
		}
		LOG.debug("String {} parsed as Calendar date {}", entry, result);
		return result;
	}

}
