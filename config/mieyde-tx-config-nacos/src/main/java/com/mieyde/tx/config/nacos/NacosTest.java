package com.mieyde.tx.config.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.AbstractSharedListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.mieyde.tx.common.loader.EnhancedServiceLoader;
import com.mieyde.tx.config.processor.Processor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author 我吃稀饭面
 * @date 2023/7/5 9:24
 */
public class NacosTest {
    public static void main(String[] args) throws NacosException, IOException {
//        String config = "spring:\n" +
//                "  profiles:\n" +
//                "    active: prd\n" +
//                "server:\n" +
//                "  port: 8080\n" +
//                "  address: 127.0.0.1\n" +
//                "  ids:\n" +
//                "    - a1\n" +
//                "    - a2\n" +
//                "    - a3";
        Properties properties = new Properties();
        properties.put("serverAddr","192.168.5.16:8848");
        properties.put("namespace","wy-config");
//        properties.put("username","");
//        properties.put("password","");
        ConfigService configService = NacosFactory.createConfigService(properties);

        Properties properties1 = new Properties();
        properties1.setProperty("server.address","localhost");
        properties1.setProperty("server.port","8848");
        properties1.setProperty("server.username","admin");
        properties1.setProperty("server.password","admin");
        properties1.setProperty("nacos.test","test");

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);

        Yaml yaml = new Yaml(options);

        Map<String, Object> map = new LinkedHashMap<>();
        properties1.forEach((key, value) -> map.put((String) key, value));

        String output = yaml.dump(map);
        System.out.println(output);

        boolean isOk1 = configService.publishConfig("registry.yaml", "WY_DEFAULT_GROUP", output,ConfigType.YAML.getType());
        System.out.println("是否成功：" + isOk1);
//        System.out.println(configService.getConfig("registry.yaml", "WY_DEFAULT_GROUP", 5000));

//        Properties processor = EnhancedServiceLoader.load(Processor.class, "properties").processor(configService.getConfig("registry.yaml", "WY_DEFAULT_GROUP", 5000));
//        System.out.println(processor.getProperty("nacos.test"));
//
//        configService.addListener("registry.yaml", "WY_DEFAULT_GROUP", new AbstractSharedListener() {
//            @Override
//            public void innerReceive(String dataId, String group, String configInfo) {
//                try {
//                    Properties processor1 = EnhancedServiceLoader.load(Processor.class).processor(configInfo);
//                    System.out.println(processor1);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//
//        System.in.read();
    }
}
