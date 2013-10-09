package net.sourceforge.jtpl;

/**
 * Default value output when replacing template variable with assigned value.
 */
public class ToString implements TemplateValueWriter {

	public String write(Object assignedValue) {
		return assignedValue.toString();
	}

}
