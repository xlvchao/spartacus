package com.xlc.spartacus.auth.common;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * RSA加、解密工具，可逆，主要用于页面传输数据的安全，加密后传到后台，后台再解密
 *
 * @author xlc, since 2021
 */
public class RSAUtils {
	public static final int KEYSIZE = 512;

	/**
	 * 加密
	 * 
	 * @param publicKey
	 *            公钥
	 * @param content
	 *            需要加密的内容
	 * @return
	 * @throws Exception
	 */
	public static String encrypttoStr(Key publicKey, String content)
			throws Exception {
		String endata = parseByte2HexStr(publicEnrypy(publicKey, content));
		return endata;
	}

	/**
	 * 解密
	 * 
	 * @param privateKey
	 *            私钥
	 * @param endata
	 *            需要解密的内容
	 * @return
	 * @throws Exception
	 */
	public static String decrypttoStr(Key privateKey, String endata)
			throws Exception {
		String data = new String(privateEncode(privateKey, parseHexStr2Byte(endata)));
		return data;
	}

	public static String decrypttoStr_normal(Key privateKey, String endata)
			throws Exception {
		String data = new String(privateEncode(privateKey, endata.getBytes()));
		return data;
	}

	/**
	 * 加密的方法,使用公钥进行加密
	 * 
	 * @param publicKey
	 *            公钥
	 * @param data
	 *            需要加密的数据
	 * @throws Exception
	 */
	public static byte[] publicEnrypy(Key publicKey, String data)
			throws Exception {

		Cipher cipher = Cipher.getInstance("RSA",
				new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// 设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		// 对数据进行加密
		byte[] result = cipher.doFinal(data.getBytes());

		return result;
	}

	/**
	 * 解密的方法，使用私钥进行解密 privateKey 私钥 encoData 需要解密的数据
	 * 
	 * @throws Exception
	 */
	public static byte[] privateEncode(Key privateKey, byte[] encoData)
			throws Exception {
		Cipher cipher = Cipher.getInstance("RSA",
				new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// 设置为解密模式，用私钥解密
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 解密
		byte[] data = cipher.doFinal(encoData);
		return data;
	}

	/**
	 * 自动生成密钥对
	 * 
	 * @throws Exception
	 */
	public static Map<String, Object> createKey() {

		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
					"RSA",
					new org.bouncycastle.jce.provider.BouncyCastleProvider());

			SecureRandom random = new SecureRandom();
			keyPairGenerator.initialize(RSAUtils.KEYSIZE, random);

			// 生成钥匙对
			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			// 得到公钥
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			// 得到私钥
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("publicKey", publicKey);
			map.put("privateKey", privateKey);

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 使用模和指数生成RSA公钥
	 * 
	 * 
	 * @param modulus
	 *            模
	 * @param exponent
	 *            指数
	 * @return
	 */
	public static RSAPublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA",
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用模和指数生成RSA私钥
	 * 
	 * /None/NoPadding】
	 * 
	 * @param modulus
	 *            模
	 * @param exponent
	 *            指数
	 * @return
	 */
	public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA",
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 产生随机串儿
	 * @return
	 */
	public String randKey() {

		Random r = new Random();
		String code = "";

		for (int i = 0; i < 9; ++i) {
			if (i % 2 == 0) // 偶数位生产随机整数
			{
				code = code + r.nextInt(10);
			} else// 奇数产生随机字母包括大小写
			{
				int temp = r.nextInt(52);
				char x = (char) (temp < 26 ? temp + 97 : (temp % 26) + 65);
				code += x;
			}
		}

		return code;
	}

	/**
	 * 将二进制转换成16进制
	 * 
	 * @param buf
	 * @return String
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return byte[]
	 */
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}


}