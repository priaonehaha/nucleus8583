package rk.commons.ioc.factory.type.converter.spi;

import rk.commons.ioc.factory.type.converter.TypeConverter;

public class StringToLongConverter implements TypeConverter {
	
	public static final Class<?> FROM = String.class;

	public static final Class<?> TO = long.class;

	public Object convert(Object from) {
		return Long.parseLong((String) from);
	}
}