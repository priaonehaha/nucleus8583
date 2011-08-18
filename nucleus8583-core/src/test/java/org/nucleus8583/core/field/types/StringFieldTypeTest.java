package org.nucleus8583.core.field.types;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.nucleus8583.core.charset.CharsetDecoder;
import org.nucleus8583.core.charset.CharsetEncoder;
import org.nucleus8583.core.charset.Charsets;
import org.nucleus8583.core.field.type.FieldType;
import org.nucleus8583.core.field.type.FieldTypes;
import org.nucleus8583.core.xml.FieldAlignments;
import org.nucleus8583.core.xml.FieldDefinition;

public class StringFieldTypeTest {

	private CharsetEncoder encoder;

	private CharsetDecoder decoder;

	private FieldType stringFieldAlignL;

	private FieldType stringFieldAlignR;

	private FieldType stringFieldAlignN;

	@Before
	public void before() throws Exception {
		encoder = Charsets.getProvider("ASCII").getEncoder();
		decoder = Charsets.getProvider("ASCII").getDecoder();

        FieldDefinition def = new FieldDefinition();
        def.setId(39);
        def.setType("a");
        def.setLength(2);

        stringFieldAlignL = FieldTypes.getType(def);

        def = new FieldDefinition();
        def.setId(39);
        def.setType("custom");
        def.setAlign(FieldAlignments.RIGHT);
        def.setPadWith(" ");
        def.setLength(2);

        stringFieldAlignR = FieldTypes.getType(def);

        def = new FieldDefinition();
        def.setId(39);
        def.setType("custom");
        def.setAlign(FieldAlignments.NONE);
        def.setPadWith("");
        def.setLength(2);

		stringFieldAlignN = FieldTypes.getType(def);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void packBinary() throws Exception {
		stringFieldAlignL.write(new ByteArrayOutputStream(), encoder, new byte[0]);
	}

	@Test
	public void packStringTooLong() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		String errorMsg = null;

		try {
			stringFieldAlignL.write(out, encoder, "1124134=2343434");
		} catch (IllegalArgumentException ex) {
			errorMsg = ex.getMessage();
		}

		assertEquals("value of field #39 is too long, expected 2 but actual is 15", errorMsg);
	}

	@Test
	public void packStringNoPad() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		stringFieldAlignL.write(out, encoder, "20");
		assertEquals("20", out.toString());

		out = new ByteArrayOutputStream();
		stringFieldAlignR.write(out, encoder, "20");
		assertEquals("20", out.toString());

		out = new ByteArrayOutputStream();
		stringFieldAlignN.write(out, encoder, "20");
		assertEquals("20", out.toString());
	}

	@Test
	public void packEmptyString() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		stringFieldAlignL.write(out, encoder, "");
		assertEquals("  ", out.toString());

		out = new ByteArrayOutputStream();
		stringFieldAlignR.write(out, encoder, "");
		assertEquals("  ", out.toString());

		out = new ByteArrayOutputStream();
		stringFieldAlignN.write(out, encoder, "");
		assertEquals("  ", out.toString());
	}

	@Test
	public void packStringWithPad() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		stringFieldAlignL.write(out, encoder, "j");
		out.flush();
		assertEquals("j ", out.toString());

		out = new ByteArrayOutputStream();
		stringFieldAlignR.write(out, encoder, "j");
		out.flush();
		assertEquals(" j", out.toString());

		out = new ByteArrayOutputStream();
		stringFieldAlignN.write(out, encoder, "j");
		out.flush();
		assertEquals("j ", out.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void packStringOverflow() throws Exception {
		stringFieldAlignL.write(new ByteArrayOutputStream(), encoder, "300");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void unpackBinary1() throws Exception {
		stringFieldAlignL.read(new ByteArrayInputStream("a".getBytes()), decoder, new byte[0]);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void unpackBinary2() throws Exception {
		stringFieldAlignL.readBinary(new ByteArrayInputStream("a".getBytes()), decoder);
	}

	@Test
	public void unpackStringNoUnpad() throws Exception {
		assertEquals("20", stringFieldAlignL.readString(new ByteArrayInputStream("20".getBytes()), decoder));

		assertEquals("20", stringFieldAlignR.readString(new ByteArrayInputStream("20".getBytes()), decoder));

		assertEquals("20", stringFieldAlignN.readString(new ByteArrayInputStream("20".getBytes()), decoder));
	}

	@Test
	public void unpackEmptyString() throws Exception {
		assertEquals("", stringFieldAlignL.readString(new ByteArrayInputStream("  ".getBytes()), decoder));

		assertEquals("", stringFieldAlignR.readString(new ByteArrayInputStream("  ".getBytes()), decoder));

		assertEquals("  ", stringFieldAlignN.readString(new ByteArrayInputStream("  ".getBytes()), decoder));
	}

	@Test
	public void unpackStringUnpad() throws Exception {
		assertEquals("j", stringFieldAlignL.readString(new ByteArrayInputStream("j ".getBytes()), decoder));

		assertEquals("j", stringFieldAlignR.readString(new ByteArrayInputStream(" j".getBytes()), decoder));

		assertEquals("j ", stringFieldAlignN.readString(new ByteArrayInputStream("j ".getBytes()), decoder));
	}

	@Test
	public void unpackStringUnpadOverflow() throws Exception {
		assertEquals("j", stringFieldAlignL.readString(new ByteArrayInputStream("j kl".getBytes()), decoder));

		assertEquals("j ", stringFieldAlignR.readString(new ByteArrayInputStream("j kl".getBytes()), decoder));

		assertEquals("j ", stringFieldAlignN.readString(new ByteArrayInputStream("j kl".getBytes()), decoder));
	}
}
