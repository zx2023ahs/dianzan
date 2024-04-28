package cn.rh.flash.sdk.paymentChannel.Mpay.util;


import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class PKCS1ToSubjectPublicKeyInfo {

    private static final int SEQUENCE_TAG = 0x30;
    private static final int BIT_STRING_TAG = 0x03;
    private static final byte[] NO_UNUSED_BITS = new byte[]{0x00};
    private static final byte[] RSA_ALGORITHM_IDENTIFIER_SEQUENCE = {(byte) 0x30, (byte) 0x0d, (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00};


    public static PublicKey decodePKCS1PublicKey(byte[] pkcs1PublicKeyEncoding) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(pkcs1PublicKeyEncoding);

        RSAKeyParameters rsa = (RSAKeyParameters) PublicKeyFactory.createKey(spkInfo);

        RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(rsa.getModulus(), rsa.getExponent());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(rsaSpec);
        return publicKey;

    }

    public static int getModulus(byte[] pkcs1PublicKeyEncoding) throws IOException {
        SubjectPublicKeyInfo spkInfo = SubjectPublicKeyInfo.getInstance(pkcs1PublicKeyEncoding);

        RSAKeyParameters rsa = (RSAKeyParameters) PublicKeyFactory.createKey(spkInfo);
        return rsa.getModulus().bitLength();
    }

    public static byte[] createSubjectPublicKeyInfoEncoding(byte[] pkcs1PublicKeyEncoding) {
        byte[] subjectPublicKeyBitString = createDEREncoding(BIT_STRING_TAG, concat(NO_UNUSED_BITS, pkcs1PublicKeyEncoding));
        byte[] subjectPublicKeyInfoValue = concat(RSA_ALGORITHM_IDENTIFIER_SEQUENCE, subjectPublicKeyBitString);
        byte[] subjectPublicKeyInfoSequence = createDEREncoding(SEQUENCE_TAG, subjectPublicKeyInfoValue);

        return subjectPublicKeyInfoSequence;
    }

    private static byte[] concat(byte[]... bas) {
        int len = 0;
        for (int i = 0; i < bas.length; i++) {
            len += bas[i].length;
        }

        byte[] buf = new byte[len];
        int off = 0;
        for (int i = 0; i < bas.length; i++) {
            System.arraycopy(bas[i], 0, buf, off, bas[i].length);
            off += bas[i].length;
        }

        return buf;
    }

    private static byte[] createDEREncoding(int tag, byte[] value) {
        if (tag < 0 || tag >= 0xFF) {
            throw new IllegalArgumentException("Currently only single byte tags supported");
        }

        byte[] lengthEncoding = createDERLengthEncoding(value.length);

        int size = 1 + lengthEncoding.length + value.length;
        byte[] derEncodingBuf = new byte[size];

        int off = 0;
        derEncodingBuf[off++] = (byte) tag;
        System.arraycopy(lengthEncoding, 0, derEncodingBuf, off, lengthEncoding.length);
        off += lengthEncoding.length;
        System.arraycopy(value, 0, derEncodingBuf, off, value.length);

        return derEncodingBuf;
    }

    private static byte[] createDERLengthEncoding(int size) {
        if (size <= 0x7F) {
            // single byte length encoding
            return new byte[]{(byte) size};
        } else if (size <= 0xFF) {
            // double byte length encoding
            return new byte[]{(byte) 0x81, (byte) size};
        } else if (size <= 0xFFFF) {
            // triple byte length encoding
            return new byte[]{(byte) 0x82, (byte) (size >> Byte.SIZE), (byte) size};
        }

        throw new IllegalArgumentException("size too large, only up to 64KiB length encoding supported: " + size);
    }

    public static PublicKey parse(String pkcs1Str) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String[] lines = pkcs1Str.split("\n");
        boolean isBegin = false;
        StringBuffer base64Buf = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.indexOf("-END") > -1) {
                break;
            } else if (line.indexOf("-BEGIN") > -1) {
                isBegin = true;
            } else if (isBegin) {
                base64Buf.append(line);
            }
        }
        byte[] pkcs1PublicKeyEncoding = Base64.getDecoder().decode(base64Buf.toString());
        PublicKey generatePublic = decodePKCS1PublicKey(pkcs1PublicKeyEncoding);
        return generatePublic;
    }

    public static int parseMod(String pkcs1Str) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String[] lines = pkcs1Str.split("\n");
        boolean isBegin = false;
        StringBuffer base64Buf = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.indexOf("-END") > -1) {
                break;
            } else if (line.indexOf("-BEGIN") > -1) {
                isBegin = true;
            } else if (isBegin) {
                base64Buf.append(line);
            }
        }
        byte[] pkcs1PublicKeyEncoding = Base64.getDecoder().decode(base64Buf.toString());
        int modulus = getModulus(pkcs1PublicKeyEncoding);
        return modulus;
    }
}

