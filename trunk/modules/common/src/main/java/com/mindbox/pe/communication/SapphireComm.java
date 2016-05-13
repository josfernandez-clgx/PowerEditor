package com.mindbox.pe.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class SapphireComm<T extends SapphireComm<?>> implements Serializable {

	private static final long serialVersionUID = 2003051917438000L;

	private static byte[] readBytes(InputStream inputstream) {
		try {
			BufferedInputStream in = new BufferedInputStream(inputstream);
			List<Integer> list = new java.util.ArrayList<Integer>();

			int i;

			while ((i = in.read()) != -1)
				list.add(new Integer(i));

			byte abyte0[] = new byte[list.size()];
			for (int j = 0; j < abyte0.length; j++)
				abyte0[j] = list.get(j).byteValue();

			in.close();
			return abyte0;
		}
		catch (Exception _ex) {
			return null;
		}
	}

	private static SapphireComm<?> serializeInUnchecked(byte abyte0[]) {
		SapphireComm<?> obj = null;
		try {
			ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
			GZIPInputStream gzipinputstream = new GZIPInputStream(bytearrayinputstream);
			ObjectInputStream objectinputstream = new ObjectInputStream(gzipinputstream);

			obj = (SapphireComm<?>) objectinputstream.readObject();
			objectinputstream.close();
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
		return obj;
	}


	/**
	 * Deserialize the servletCom serialized in the specified ObjectInputStream.
	 */
	public static SapphireComm<?> serializeInUnchecked(InputStream pobjin) {
		byte[] ba = readBytes(pobjin);
		return serializeInUnchecked(ba);
	}

	protected SapphireComm() {
	}

	public ResponseComm serializeIn(byte abyte0[]) {
		ResponseComm obj = null;
		try {
			obj = ResponseComm.class.cast(serializeInUnchecked(abyte0));
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
		return obj;
	}

	/**
	 * Deserialize the servletCom serialized in the specified ObjectInputStream.
	 */
	public ResponseComm serializeIn(InputStream pobjin) {
		byte[] ba = readBytes(pobjin);
		return serializeIn(ba);
	}

	public byte[] serializeOut() {
		byte abyte0[] = null;

		try {
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			GZIPOutputStream gzipoutputstream = new GZIPOutputStream(bytearrayoutputstream);
			ObjectOutputStream objectoutputstream = new ObjectOutputStream(gzipoutputstream);

			objectoutputstream.writeObject(this);
			objectoutputstream.flush();
			objectoutputstream.close();
			abyte0 = bytearrayoutputstream.toByteArray();
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
		return abyte0;
	}

	public void serializeOut(BufferedOutputStream bufferedoutputstream) {
		byte abyte0[] = serializeOut();

		try {
			bufferedoutputstream.write(abyte0, 0, abyte0.length);
			bufferedoutputstream.flush();
			bufferedoutputstream.close();
		}
		catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
	}

	public String toString() {
		return super.toString();
	}
}
