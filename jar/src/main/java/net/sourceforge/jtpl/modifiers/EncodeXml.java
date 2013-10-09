package net.sourceforge.jtpl.modifiers;

import net.sourceforge.jtpl.TemplateValueWriter;

public class EncodeXml implements TemplateValueWriter {

	private static final String[][] XML_ESCAPE = {
			{ "&", "&amp;" },   // & - ampersand
			{ "\"", "&quot;" }, // " - double-quote
			{ "<", "&lt;" },    // < - less-than
			{ ">", "&gt;" },    // > - greater-than
			{ "'", "&apos;" }   // XML apostrophe
	};

	public String write(Object assignedValue) {
		String v = assignedValue.toString();
		for (int i = 0; i < XML_ESCAPE.length; i++) {
			v = v.replace(XML_ESCAPE[i][0], XML_ESCAPE[i][1]);
		}
		return v;
	}

	
	
}
