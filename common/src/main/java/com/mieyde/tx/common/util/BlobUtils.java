package com.mieyde.tx.common.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.mieyde.tx.common.Constants;
import com.mieyde.tx.common.exception.ShouldNeverHappenException;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.util.Objects;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 16:35
 */
public class BlobUtils {

    /**
     * String 2 blob blob.
     *
     * @param str the str
     * @return the blob
     */
    public static Blob string2blob(String str) {
        if (StrUtil.isBlank(str)) {
            return null;
        }

        try {
            return new SerialBlob(str.getBytes(Constants.DEFAULT_CHARSET));
        } catch (Exception e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    /**
     * Blob 2 string string.
     *
     * @param blob the blob
     * @return the string
     */
    public static String blob2string(Blob blob) {
        if (Objects.isNull(blob)) {
            return null;
        }

        try {
            return new String(blob.getBytes((long) 1, (int) blob.length()), Constants.DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    /**
     * Byte array to blob
     *
     * @param bytes the byte array
     * @return the blob
     */
    public static Blob bytes2Blob(byte[] bytes) {
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        }

        try {
            return new SerialBlob(bytes);
        } catch (Exception e) {
            throw new ShouldNeverHappenException(e);
        }
    }

    /**
     * Blob to byte array.
     *
     * @param blob the blob
     * @return the byte array
     */
    public static byte[] blob2Bytes(Blob blob) {
        if (Objects.isNull(blob)) {
            return null;
        }

        try {
            return blob.getBytes((long) 1, (int) blob.length());
        } catch (Exception e) {
            throw new ShouldNeverHappenException(e);
        }
    }
}
