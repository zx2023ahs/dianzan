package cn.rh.flash.sdk.paymentChannel.Mpay.util;



import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.Arrays;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class RSAUtils {
    public static final int SOU_BLOCK_LENGTH = 446;
    public static final int SEC_BLOCK_LENGTH = 512;


    private static KeyPair getKeyPair(String pck1String) throws IOException {
        PEMParser pemParser = new PEMParser(new StringReader(pck1String.trim()));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());
        Object object = pemParser.readObject();
        return converter.getKeyPair((PEMKeyPair) object);
    }

    public static PrivateKey getPrivateKey(String pck1String) throws Exception {
        KeyPair kp = RSAUtils.getKeyPair(pck1String);
        return kp.getPrivate();
    }

    public static PublicKey getPublicKey(String pck1String) throws Exception {

        return PKCS1ToSubjectPublicKeyInfo.parse(pck1String);
    }

    public static int getLength(String pck1String) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return PKCS1ToSubjectPublicKeyInfo.parseMod(pck1String);
    }


    private static byte[][] split(byte[] data, int length) {
        int count = (int) Math.ceil(1.0 * data.length / length);
        byte[][] result = new byte[count][];

        for (int i = 0; i < count; i++) {
            if (i * length + length < data.length) {
                result[i] = Arrays.copyOfRange(data, i * length, i * length + length);
            } else {
                result[i] = Arrays.copyOfRange(data, i * length, data.length - i * length);
            }

        }
        return result;
    }

    public static byte[] encrypt(byte[] data, Key publicKey) throws Exception {
        Cipher cipher = RSAUtils.makeCipher(publicKey, Cipher.ENCRYPT_MODE);
        int count = (int) Math.ceil(1.0 * data.length / SOU_BLOCK_LENGTH);
        ByteBuffer buffer = ByteBuffer.allocate(count * SEC_BLOCK_LENGTH);
        for (int i = 0; i < count; i++) {
            byte[] dd;
            if (i * SOU_BLOCK_LENGTH + SOU_BLOCK_LENGTH > data.length) {
                dd = cipher.doFinal(data, i * SOU_BLOCK_LENGTH, data.length - i * SOU_BLOCK_LENGTH);
            } else {
                dd = cipher.doFinal(data, i * SOU_BLOCK_LENGTH, SOU_BLOCK_LENGTH);
            }
            buffer.put(dd);
        }
//        System.out.println(((RSAPublicKey)publicKey).getPublicExponent().toString());
//        System.out.println(((RSAPublicKey)publicKey).getModulus().toString());
        return buffer.array();
    }


    private static Cipher makeCipher(Key key, int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        cipher.init(mode, key,oaepParams);
        return cipher;
    }

    public static byte[] decrypt(byte[] data, Key privateKey) throws Exception {
        Cipher cipher = RSAUtils.makeCipher(privateKey, Cipher.DECRYPT_MODE);
        int count = (int) Math.ceil(1.0 * data.length / SEC_BLOCK_LENGTH);
        ByteBuffer buffer = ByteBuffer.allocate(count * SOU_BLOCK_LENGTH);
        for (int i = 0; i < count; i++) {
            byte[] dd;
            if (i * SEC_BLOCK_LENGTH + SEC_BLOCK_LENGTH > data.length) {
                dd = cipher.doFinal(data, i * SEC_BLOCK_LENGTH, data.length - i * SEC_BLOCK_LENGTH + SEC_BLOCK_LENGTH);
            } else {
                dd = cipher.doFinal(data, i * SEC_BLOCK_LENGTH, SEC_BLOCK_LENGTH);
            }
            buffer.put(dd);
        }
        return buffer.array();
    }


}
