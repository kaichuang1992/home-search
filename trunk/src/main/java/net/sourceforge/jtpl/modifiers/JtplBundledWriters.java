package net.sourceforge.jtpl.modifiers;

import java.util.HashMap;

import net.sourceforge.jtpl.TemplateValueWriter;
import net.sourceforge.jtpl.TemplateValueWriterProvider;

public class JtplBundledWriters implements TemplateValueWriterProvider {

	private HashMap writers = new HashMap();
	
	public JtplBundledWriters() {
		writers.put("encodeUrl", new EncodeUrl());
		writers.put("encodeXml", new EncodeXml());
	}
	
	public TemplateValueWriter getWriter(String variableModifierName) {
		return (TemplateValueWriter) writers.get(variableModifierName);
	}
	
}
