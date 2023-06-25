package com.mieyde.tx.common.util;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 21:19
 */
public class IOUtils {

    public static void close(AutoCloseable... autoCloseables){
        if (CollectionUtils.isNotEmpty(autoCloseables)){
            for (AutoCloseable autoCloseable : autoCloseables) {
                close(autoCloseable);
            }
        }
    }

    public static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
            }
        }
    }
}
