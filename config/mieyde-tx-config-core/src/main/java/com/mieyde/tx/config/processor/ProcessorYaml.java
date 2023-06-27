package com.mieyde.tx.config.processor;

import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.common.util.MapUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 23:07
 */
@LoadLevel(name = "yaml")
public class ProcessorYaml implements Processor{

    @Override
    public Properties processor(String config) throws IOException {
        Properties properties = new Properties();
        Map<String, Object> map = MapUtil.asMap(new Yaml().load(config));
        properties.putAll(MapUtil.getFlattenedMap(map));
        return properties;
    }
}
