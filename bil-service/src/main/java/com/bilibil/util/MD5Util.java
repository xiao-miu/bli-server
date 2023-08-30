package com.bilibil.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * MD5加密
 * 单向加密算法
 * 特点：加密速度快，不需要秘钥，但是安全性不高，需要搭配随机盐值使用
 *
 */
public class MD5Util {
	// 加密
	public static String sign(String content, String salt, String charset) {
		content = content + salt;
		return DigestUtils.md5Hex(getContentBytes(content, charset));
	}
	// 和加密字符串进行比对，比对成功就是一致的，
	public static boolean verify(String content, String sign, String salt, String charset) {
		content = content + salt;
		String mysign = DigestUtils.md5Hex(getContentBytes(content, charset));
		return mysign.equals(sign);
	}

	private static byte[] getContentBytes(String content, String charset) {
		if (!"".equals(charset)) {
			try {
				return content.getBytes(charset);
			} catch (UnsupportedEncodingException var3) {
				throw new RuntimeException("MD5签名过程中出现错误,指定的编码集错误");
			}
		} else {
			return content.getBytes();
		}
	}

	//获取文件md5加密后的字符串
	public static String getFileMD5(MultipartFile file) throws Exception {
		// 文件的输入流
		InputStream fis = file.getInputStream();
		// 把字节数组拿出来
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int byteRead;
		while((byteRead = fis.read(buffer)) > 0){
			baos.write(buffer, 0, byteRead);
		}
		fis.close();
		// 把字节数组内容进行加密
		return DigestUtils.md5Hex(baos.toByteArray());
	}
}