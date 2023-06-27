package com.mieyde.tx.config.processor;

import com.mieyde.tx.common.loader.LoadLevel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 23:28
 */
@LoadLevel(name = "properties")
public class ProcessorProperties implements Processor{
    @Override
    public Properties processor(String config) throws IOException {
        Properties properties = new Properties();
        try(Reader reader = new InputStreamReader(new ByteArrayInputStream(config.getBytes()), StandardCharsets.UTF_8)){
            properties.load(reader);
        }
        return properties;
    }
}
