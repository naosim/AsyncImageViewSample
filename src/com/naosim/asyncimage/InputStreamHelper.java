package com.naosim.asyncimage;

import java.io.InputStream;

public class InputStreamHelper {
	InputStream in;
	InputStreamLoader loader;

	public InputStreamHelper() {
	}

	public InputStreamHelper setInputStream(InputStream in) {
		this.in = in;
		return this;
	}

	public InputStreamHelper setInputStreamLoader(InputStreamLoader loader) {
		this.loader = loader;
		return this;
	}

	public Object startStream() throws Exception {
		Object result = null;
		try {
			result = loader.stream(in);
		} catch (Exception e1) {
			throw e1;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public static Object startStream(InputStream in, InputStreamLoader loader) throws Exception {
		return new InputStreamHelper().setInputStream(in)
				.setInputStreamLoader(loader).startStream();
	}

	public static interface InputStreamLoader {
		public Object stream(InputStream in) throws Exception;
	}

}
