package com.mieyde.tx.config.processor;

import java.io.IOException;
import java.util.Properties;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 21:46
 */
public interface Processor {

    Properties processor(String config) throws IOException;
}
