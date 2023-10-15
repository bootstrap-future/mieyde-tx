package com.mieyde.tx.config.sh;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mieyde.tx.common.util.CollectionUtils;
import com.mieyde.tx.common.util.FileUtils;
import com.mieyde.tx.config.file.YamlFileConfig;

import java.io.File;
import java.sql.Driver;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 22:25
 */
public class TestDemo {
    public static void main(String[] args) throws Exception {
//        YamlFileConfig yamlFileConfig = new YamlFileConfig(FileUtils.load("registry.yaml"));
//        System.out.println(yamlFileConfig.getContent("server"));

//        SimpleFileConfig simpleFileConfig = new SimpleFileConfig(FileUtils.load("test.config"));
//        System.out.println(simpleFileConfig.getContent("address"));

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
//        String config1 = "spring.profiles.active=prd\n" +
//                "server.address=127.0.0.1\n" +
//                "server.ids=a1,a2,a3\n" +
//                "server.test=123";
//        Properties properties = new ProcessorYaml().processor(config);
//        System.out.println(properties);
//        Properties properties1 = new ProcessorProperties().processor(config1);
//        System.out.println(properties1);


//        Object[] tempArgsYaml = new Object[]{FileUtils.load("registry.yaml")};
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

//        Map<String, String> envMap = System.getenv();
//        for (Map.Entry<String, String> entry : envMap.entrySet()) {
//            System.out.println(entry.getKey() + "--->" + entry.getValue());
//        }
//        System.out.println("================================================");
//        Properties properties = System.getProperties();
//        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
//            System.out.println(entry.getKey() + "--->" + entry.getValue());
//        }

//        Configuration configuration = new FileConfiguration("registry.conf");
//        System.out.println(configuration.getLatestConfig("registry.type", "none"));
//        System.out.println(configuration.getConfig("registry.type", "none"));
//        System.out.println(configuration.getConfig("registry.type"));
//        System.out.println(configuration.getConfig("registry.nacos.application"));

//        mianyangData();

        //Java SPI
//        ServiceLoader<Driver> load = ServiceLoader.load(Driver.class);
//        Iterator<Driver> iterator = load.iterator();
//        while (iterator.hasNext()){
//            Driver next = iterator.next();
//            System.out.println(next);
//        }

        Stream<String> stream = Stream.of("aaa", "bbb", "ddd", "ccc", "fff");
        Optional<String> first = stream.findFirst();
        System.out.println(first.orElseGet(() -> String.valueOf(new Random().nextInt())));

//        Supplier<Double> supplier = Math::random;
//        Supplier<Double> supplier1 = ()->new Random().nextDouble();
//        System.out.println(supplier1.get());
    }

    public static void mianyangData(){
        StringBuffer xmids = new StringBuffer();
        String content = FileUtils.getContent(FileUtils.load("t3.file"));
        String[] split = content.split("\n");
        JSONArray jsonArray = new JSONArray();
        for (String s : split) {
            JSONObject jsonObject = toJson(s);
            JSONArray projectIdList = jsonObject.getJSONArray("projectIdList");
            xmids.append(CollectionUtils.join(projectIdList,",")).append(",");
            jsonArray.add(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("datas",jsonArray);
        System.out.println(jsonObject.toJSONString());
        System.out.println(xmids.toString());
    }

    public static JSONObject toJson(String data){
        JSONObject jsonObject = new JSONObject();
        String[] record = data.split(",");
        for (int i = 0; i < record.length; i++) {

            String name = record[0];
            String province = record[1];
            String provinceCode = record[6].substring(0,2);
            String city = record[2];
            String cityCode = record[6].substring(0,4);
            String area = record[3];
            String areaCode = record[6].substring(0,6);
            String street = record[4];
            String streetCode = record[6].substring(0,9);
            String community = record[5];
            String communityCode = record[6];

            jsonObject.put("name",name);
            jsonObject.put("province",province);
            jsonObject.put("provinceCode",provinceCode);
            jsonObject.put("city",city);
            jsonObject.put("cityCode",cityCode);
            jsonObject.put("area",area);
            jsonObject.put("areaCode",areaCode);
            jsonObject.put("street",street);
            jsonObject.put("streetCode",streetCode);
            jsonObject.put("community",community);
            jsonObject.put("communityCode",communityCode);

            if (record[7].split("-").length <= 0){
                throw new RuntimeException("关联项目不存在");
            }
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList(record[7].split("-")));
            jsonObject.put("projectIdList",jsonArray);
        }
        return jsonObject;
    }
}
