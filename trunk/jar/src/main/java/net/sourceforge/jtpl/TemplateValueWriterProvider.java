package net.sourceforge.jtpl;

/**
 * Selects writer based on "modifier" name in template varialble.
 */
public interface TemplateValueWriterProvider {

	/**
	 * @param variableModifierName ID of a value modifier as added to template variable prefixed by pipe char
	 * @return The instance to convert assigned value to output, null if not available
	 */
	TemplateValueWriter getWriter(String variableModifierName);
	
}
