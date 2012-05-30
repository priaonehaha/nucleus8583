package org.nucleus8583.core.field.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nucleus8583.core.field.Type;
import org.nucleus8583.core.util.BcdPrefixer;
import org.nucleus8583.core.util.EbcdicPadder;

import rk.commons.util.StringHelper;

public class BcdPrefixedEbcdicText implements Type<String> {

    private static final long serialVersionUID = -5615324004502124085L;

    protected final BcdPrefixer prefixer;

    protected final EbcdicPadder padder;

    protected int maxLength;

    protected String emptyValue;

    public BcdPrefixedEbcdicText() {
        prefixer = new BcdPrefixer();
        padder = new EbcdicPadder();
        
        emptyValue = "";
    }
    
    public BcdPrefixedEbcdicText(BcdPrefixedEbcdicText o) {
    	prefixer = new BcdPrefixer(o.prefixer);
    	padder = new EbcdicPadder(o.padder);
    	
    	maxLength = o.maxLength;
    	emptyValue = o.emptyValue;
    }
	
	public void setPrefixLength(int prefixLength) {
		prefixer.setPrefixLength(prefixLength);
	}
	
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setEmptyValue(String emptyValue) {
		if (emptyValue == null) {
			this.emptyValue = "";
		} else {
			this.emptyValue = StringHelper.escapeJava(emptyValue);
		}
	}

    public String read(InputStream in) throws IOException {
    	// read body length
        int vlen = prefixer.readUint(in);
        if (vlen == 0) {
            return emptyValue;
        }

        // read body
        char[] cbuf = new char[vlen];
        padder.read(in, cbuf, 0, vlen);

        return new String(cbuf);
    }

    public void write(OutputStream out, String value) throws IOException {
        int vlen = value.length();
        if (vlen > maxLength) {
            throw new IllegalArgumentException("value too long, expected 0-" + maxLength + " but actual is " + vlen);
        }

        // write body length
        prefixer.writeUint(out, vlen);
        
        // write body
        padder.write(out, value, 0, vlen);
    }

	public void readBitmap(InputStream in, byte[] bitmap, int off, int len)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	public void writeBitmap(OutputStream out, byte[] bitmap, int off, int len)
			throws IOException {
		throw new UnsupportedOperationException();
	}
	
	public Type<String> clone() {
		return new BcdPrefixedEbcdicText(this);
	}
}
