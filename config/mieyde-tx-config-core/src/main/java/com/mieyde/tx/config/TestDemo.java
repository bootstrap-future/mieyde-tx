package com.mieyde.tx.config;

import cn.hutool.setting.yaml.YamlUtil;
import com.mieyde.tx.common.loader.EnhancedServiceLoader;
import com.mieyde.tx.common.util.FileUtils;
import com.mieyde.tx.config.file.FileConfig;
import com.mieyde.tx.config.file.SimpleFileConfig;
import com.mieyde.tx.config.processor.Processor;
import com.mieyde.tx.config.processor.ProcessorProperties;
import com.mieyde.tx.config.processor.ProcessorYaml;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 22:25
 */
public class TestDemo {
    public static void main(String[] args) throws Exception {
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
                "server.ids=a1,a2,a3\n" +
                "server.test=123";
//        Properties properties = new ProcessorYaml().processor(config);
//        System.out.println(properties);
//        Properties properties1 = new ProcessorProperties().processor(config1);
//        System.out.println(properties1);


//        Object[] tempArgsYaml = new Object[]{FileUtils.load("test.yaml")};
//        Object[] tempArgsConfig = new Object[]{FileUtils.load("test.config")};
//        Object[] tempArgsConfig1 = new Object[]{FileUtils.load("C:\\Users\\Administrator\\Desktop\\test1.config")};
//        String content = EnhancedServiceLoader.load(FileConfig.class, FileConfigFactory.DEFAULT_TYPE,tempArgsConfig1).getContent("fullname");
//        System.out.println(content);
//
//        String content1 = EnhancedServiceLoader.load(FileConfig.class, FileConfigFactory.YAML_TYPE).getContent("server.address");
//        System.out.println(content1);

//        Properties yamlProperties = EnhancedServiceLoader.load(Processor.class, "yaml").processor(config);
//        System.out.println(yamlProperties);
//
//        Properties properties = EnhancedServiceLoader.load(Processor.class, "properties").processor(config1);
//        System.out.println(properties);

        Map<String, String> envMap = System.getenv();
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            System.out.println(entry.getKey() + "--->" + entry.getValue());
        }
        System.out.println("================================================");
        Properties properties = System.getProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            System.out.println(entry.getKey() + "--->" + entry.getValue());
        }
    }
}
