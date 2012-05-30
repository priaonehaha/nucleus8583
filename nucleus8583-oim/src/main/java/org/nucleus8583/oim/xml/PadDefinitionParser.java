package org.nucleus8583.oim.xml;

import org.nucleus8583.oim.field.spi.BinaryPad;
import org.nucleus8583.oim.field.spi.TextPad;
import org.w3c.dom.Element;

import rk.commons.inject.factory.support.ObjectDefinitionBuilder;
import rk.commons.inject.factory.xml.ObjectDefinitionParserDelegate;
import rk.commons.inject.factory.xml.SingleObjectDefinitionParser;
import rk.commons.util.StringHelper;

public class PadDefinitionParser extends SingleObjectDefinitionParser {

	public static final String ELEMENT_LOCAL_NAME = "pad";
	
	@Override
	protected Class<?> getObjectClass(Element element) {
		String mode = element.getAttribute("mode");
		
		if ("text".equals(mode)) {
			return TextPad.class;
		} else if ("binary".equals(mode)) {
			return BinaryPad.class;
		} else {
			throw new UnsupportedOperationException("unsupported mode " + mode);
		}
	}
	
	protected void doParse(Element element, ObjectDefinitionParserDelegate delegate, ObjectDefinitionBuilder builder) {
		String stmp = element.getAttribute("no");
		if (StringHelper.hasText(stmp)) {
			builder.addPropertyValue("no", Integer.parseInt(stmp));
		}
		
		stmp = element.getAttribute("padWith");
		if (StringHelper.hasText(stmp, false)) {
			builder.addPropertyValue("padWith", StringHelper.escapeJava(stmp).toCharArray());
		}
	}
}
