package com.mieyde.tx.config;

import com.mieyde.tx.common.loader.EnhancedServiceLoader;
import com.mieyde.tx.config.file.FileConfig;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 21:55
 */
public class FileConfigFactory {

    public static final String DEFAULT_TYPE = "CONF";

    public static final String YAML_TYPE = "YAML";

    private static final LinkedHashMap<String,String> SUFFIX_MAP = new LinkedHashMap<>(4){
        {
            put("conf",DEFAULT_TYPE);
            put("properties",DEFAULT_TYPE);
            put("yml",YAML_TYPE);
            put("yaml",YAML_TYPE);
        }
    };

    public static FileConfig load(){
        return loadService(DEFAULT_TYPE, null, null);
    }

    public static FileConfig load(File file){
        return loadService(getConfigType(file.getName()), new Class[]{File.class}, new Object[]{file});
    }

    private static FileConfig loadService(String name,Class[] argsType,Object[] args){
        return EnhancedServiceLoader.load(FileConfig.class, name, argsType, args);
    }

    private static String getConfigType(String fileName) {
        String configType = DEFAULT_TYPE;
        int suffixIndex = fileName.lastIndexOf(".");
        if (suffixIndex > 0){
            configType = SUFFIX_MAP.getOrDefault(fileName.substring(suffixIndex + 1),DEFAULT_TYPE);
        }
        return configType;
    }
}
