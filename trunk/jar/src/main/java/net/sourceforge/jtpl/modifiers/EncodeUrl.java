package net.sourceforge.jtpl.modifiers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sourceforge.jtpl.TemplateValueWriter;

public class EncodeUrl implements TemplateValueWriter {

	public static final String encoding = "UTF-8";
	
	public String write(Object assignedValue) {
		try {
			return URLEncoder.encode(assignedValue.toString(), encoding).replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Failed to encode template value usin " + encoding, e);
		}
	}

}
