package org.xu.binary;

import org.xu.compiler.XuCompiledCode;
import org.xu.exception.XuException;
import org.xu.pass.XuCompilationPass;
import org.xu.type.XuValue;
import org.xu.type.XuValueType;
import org.xu.util.HashUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XuBytecodeEmissionPass extends XuCompilationPass<XuCompiledCode, XuBytecode> {

    private final DataOutputStream writer;
    private final String filePath = "project/build/test.abc";
    private BytecodeHeader header;
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "xu_first_ver";

    public XuBytecodeEmissionPass() {
        try {
            writer = new DataOutputStream(new FileOutputStream(filePath));
        } catch (IOException e) {
            throw new XuException("Cannot open .abc file.");
        }
    }

    @Override
    public Class<XuCompiledCode> getInputType() {
        return XuCompiledCode.class;
    }

    @Override
    public Class<XuBytecode> getOutputType() {
        return XuBytecode.class;
    }

    @Override
    public String getDebugName() {
        return "Bytecode Emission Pass";
    }

    @Override
    protected XuBytecode pass(XuCompiledCode input) {
        List<Byte> rawCode = new ArrayList<>();

        try {
            writer.writeByte(XuBinaryCodes.HEADER);
            byte[] hashBytes = new byte[input.code.size()];

            for (int i = 0; i < hashBytes.length; i++) {
                if (input.code.get(i) != null) {
                    hashBytes[i] = ((Number) input.code.toArray()[i]).byteValue();
                }
            }

            String hash = HashUtil.generateHash(hashBytes);
            byte[] encryptedBytes = encrypt(hash);

            for (Byte aByte : encryptedBytes) {
                writer.writeByte(aByte);
            }

            writer.writeByte(XuBinaryCodes.MAIN);

            for (Byte byteCode : input.code) {
                writer.writeByte(byteCode);
                rawCode.add(byteCode);
            }

            writer.writeByte(XuBinaryCodes.STRING_POOL);

            for (Map.Entry<Byte, String> string : input.stringTable.entrySet()) {
                writer.writeByte(string.getValue().length());

                for (char c : string.getValue().toCharArray()) {
                    writer.writeByte((byte) c);
                }
            }

            writer.writeByte(XuBinaryCodes.TABLE);

            for (Map.Entry<Byte, XuValue> entry : input.constantTable.entrySet()) {
                writer.writeByte(entry.getValue().type);

                switch (entry.getValue().type) {
                    case XuValueType.STRING:
                        byte[] bytes = ((String) entry.getValue().value).getBytes();

                        writer.writeByte(bytes.length);

                        for (byte aByte : bytes) {
                            writer.writeByte(aByte);
                        }
                        break;

                    case XuValueType.INT:
                        writer.writeInt((Integer) entry.getValue().value);
                        break;

                    case XuValueType.FLOAT:
                        writer.writeFloat((Float) entry.getValue().value);
                        break;

                    case XuValueType.BOOL:
                        writer.writeByte((Boolean) entry.getValue().value ? 1 : 0);
                        break;

                    case XuValueType.CHAR:
                        writer.writeChar((Character) entry.getValue().value);
                        break;

                    default:
                        writer.writeBytes(entry.getKey().toString());
                        break;
                }
                writer.writeByte(entry.getKey());
            }

            writer.writeByte(XuBinaryCodes.END_OF_FILE);

            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new XuException("Error writing bytecode to file.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new XuBytecode(filePath, rawCode, input.constantTable, input.stringTable);
    }

    private byte[] encrypt(String hash) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(hash.getBytes());
    }
}
