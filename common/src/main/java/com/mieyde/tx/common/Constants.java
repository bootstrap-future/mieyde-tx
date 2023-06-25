package com.mieyde.tx.common;

import java.nio.charset.Charset;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 16:40
 */
public interface Constants {
    /**
     * default charset name
     */
    String DEFAULT_CHARSET_NAME = "UTF-8";
    /**
     * default charset is utf-8
     */
    Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);
}
