package com.mieyde.tx.config;

import cn.hutool.setting.yaml.YamlUtil;
import com.mieyde.tx.common.util.FileUtils;
import com.mieyde.tx.config.file.SimpleFileConfig;
import com.mieyde.tx.config.processor.ProcessorProperties;
import com.mieyde.tx.config.processor.ProcessorYaml;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 22:25
 */
public class TestDemo {
    public static void main(String[] args) throws IOException {
//        YamlFileConfig yamlFileConfig = new YamlFileConfig(FileUtils.load("test.yaml"));
//        System.out.println(yamlFileConfig.getContent("server"));

//        SimpleFileConfig simpleFileConfig = new SimpleFileConfig(FileUtils.load("test.config"));
//        System.out.println(simpleFileConfig.getContent("address"));

        String config = "spring:\n" +
                "  profiles:\n" +
                "    active: prd\n" +
                "server:\n" +
                "  port: 8080\n" +
                "  address: 127.0.0.1\n" +
                "  ids:\n" +
                "    - a1\n" +
                "    - a2\n" +
                "    - a3";
        String config1 = "spring.profiles.active=prd\n" +
                "server.address=127.0.0.1\n" +
                "server.ids=a1,a2,a3";
        Properties properties = new ProcessorYaml().processor(config);
        System.out.println(properties);
        Properties properties1 = new ProcessorProperties().processor(config1);
        System.out.println(properties1);
    }
}
