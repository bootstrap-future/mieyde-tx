package com.mieyde.tx.config.file;

import cn.hutool.setting.yaml.YamlUtil;
import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.common.loader.Scope;
import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.config.FileConfigFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 22:08
 */
@LoadLevel(name = FileConfigFactory.YAML_TYPE,order = 1,scope = Scope.PROTOTYPE)
public class YamlFileConfig implements FileConfig{

    private Map configMap;

    public YamlFileConfig(File file) {
        try (InputStream is = new FileInputStream(file)){
            configMap = YamlUtil.load(is,Map.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("file not found");
        }
    }

    @Override
    public String getContent(String path) {
        try {
            Map config = configMap;
            String[] dataIds = path.split("\\.");
            for (int i = 0; i < dataIds.length - 1; i++) {
                if (config.containsKey(dataIds[i])){
                    config = (Map) config.get(dataIds[i]);
                }else {
                    return null;
                }
            }

            Object value = config.get(dataIds[dataIds.length - 1]);
            return ObjectUtils.isNull(value) ? null : String.valueOf(value);
        }catch (Exception e){
            return null;
        }
    }
}
