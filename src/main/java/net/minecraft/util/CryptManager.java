package net.minecraft.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptManager
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static SecretKey createNewSharedKey()
    {
        try
        {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(128);
            return keygenerator.generateKey();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception)
        {
            throw new Error(nosuchalgorithmexception);
        }
    }

    public static KeyPair generateKeyPair()
    {
        try
        {
            KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
            LOGGER.error("Key pair generation failed!");
            return null;
        }
    }

    public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey)
    {
        try
        {
            return digestOperation("SHA-1", serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        }
        catch (UnsupportedEncodingException unsupportedencodingexception)
        {
            unsupportedencodingexception.printStackTrace();
            return null;
        }
    }

    private static byte[] digestOperation(String algorithm, byte[]... data)
    {
        try
        {
            MessageDigest messagedigest = MessageDigest.getInstance(algorithm);

            for (byte[] abyte : data)
            {
                messagedigest.update(abyte);
            }

            return messagedigest.digest();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
    }

    public static PublicKey decodePublicKey(byte[] encodedKey)
    {
        try
        {
            EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(encodedkeyspec);
        }
        catch (NoSuchAlgorithmException var3)
        {
        }
        catch (InvalidKeySpecException var4)
        {
        }

        LOGGER.error("Public key reconstitute failed!");
        return null;
    }

    public static SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted)
    {
        return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
    }

    public static byte[] encryptData(Key key, byte[] data)
    {
        return cipherOperation(1, key, data);
    }

    public static byte[] decryptData(Key key, byte[] data)
    {
        return cipherOperation(2, key, data);
    }

    private static byte[] cipherOperation(int opMode, Key key, byte[] data)
    {
        try
        {
            return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
        }
        catch (IllegalBlockSizeException illegalblocksizeexception)
        {
            illegalblocksizeexception.printStackTrace();
        }
        catch (BadPaddingException badpaddingexception)
        {
            badpaddingexception.printStackTrace();
        }

        LOGGER.error("Cipher data failed!");
        return null;
    }

    private static Cipher createTheCipherInstance(int opMode, String transformation, Key key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(opMode, key);
            return cipher;
        }
        catch (InvalidKeyException invalidkeyexception)
        {
            invalidkeyexception.printStackTrace();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception)
        {
            nosuchalgorithmexception.printStackTrace();
        }
        catch (NoSuchPaddingException nosuchpaddingexception)
        {
            nosuchpaddingexception.printStackTrace();
        }

        LOGGER.error("Cipher creation failed!");
        return null;
    }

    public static Cipher createNetCipherInstance(int opMode, Key key)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        }
        catch (GeneralSecurityException generalsecurityexception)
        {
            throw new RuntimeException(generalsecurityexception);
        }
    }
}
