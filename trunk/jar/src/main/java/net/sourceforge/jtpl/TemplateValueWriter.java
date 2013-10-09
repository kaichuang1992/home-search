package net.sourceforge.jtpl;

/**
 * Produces the value used when replacing template variable with assigned value.
 * Writer/modifier can be selected by appending "|modifierName" after the
 * variable name inside the brackets.
 */
public interface TemplateValueWriter {

	/**
	 * @param assignedValue not-null value assigned to template instance,
	 *  strings in Jtpl2 but could be arbitrary objects in the future
	 * @return the value to fill the variable block with in output
	 */
	String write(Object assignedValue);
	
}
