package com.mieyde.tx.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/25 21:15
 */
public class CompressUtils {

    /**
     * 压缩
     */
    public static byte[] compress(final byte[] src) {
        byte[] result;
        ByteArrayOutputStream bos = null;
        GZIPOutputStream gos = null;
        try {
            bos = new ByteArrayOutputStream(src.length);
            gos = new GZIPOutputStream(bos);
            gos.write(src);
            gos.finish();
            result = bos.toByteArray();
        } catch (IOException e){
            throw new IllegalArgumentException(e);
        }finally {
            IOUtils.close(bos,gos);
        }
        return result;
    }

    /**
     * 解压
     */
    public static byte[] uncompress(final byte[] src){
        byte[] result;
        byte[] uncompressData = new byte[src.length];
        ByteArrayInputStream bis = null;
        GZIPInputStream iis = null;
        ByteArrayOutputStream bos = null;
        try {
            bis = new ByteArrayInputStream(src);
            iis = new GZIPInputStream(bis);
            bos = new ByteArrayOutputStream(src.length);

            while (true){
                int len = iis.read(uncompressData, 0, uncompressData.length);
                if (len <= 0){
                    break;
                }
                bos.write(uncompressData,0,len);
            }
            bos.flush();
            result = bos.toByteArray();
        }catch (IOException e){
            throw new IllegalArgumentException(e);
        }finally {
            IOUtils.close(bis,iis,bos);
        }
        return result;
    }

    /**
     * 判断是否为压缩数据
     */
    public static boolean isCompressData(byte[] bytes) {
        if (bytes != null && bytes.length > 2) {
            int header = ((bytes[0] & 0xff)) | (bytes[1] & 0xff) << 8;
            return GZIPInputStream.GZIP_MAGIC == header;
        }
        return false;
    }

}
