package com.mieyde.tx.common.io;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 14:52
 */
public class FileLoader {

    private static final Logger log = LoggerFactory.getLogger(FileLoader.class);

    public static File load(String name){
        try {
            if (StrUtil.isBlank(name)){
                throw new IllegalArgumentException("name cant't be null");
            }
            String decodePath = URLDecoder.decode(name, StandardCharsets.UTF_8.name());
            return getFileFromSystem(decodePath);
        }catch (Exception e){
            log.error("decode name error: {}",e.getMessage(),e);
        }
        return null;
    }

    private static File getFileFromSystem(String decodePath){
        URL resource = FileLoader.class.getClassLoader().getResource("");
        List<String> paths = new ArrayList<>();
        paths.add(decodePath);
        if (Objects.nonNull(resource)){
            paths.add(resource.getPath() + decodePath);
        }
        for (String path : paths) {
            File targetFile = new File(path);
            if (targetFile.exists()){
                return targetFile;
            }
        }
        return null;
    }
}
