package com.mieyde.tx.config.file;

import com.mieyde.tx.common.loader.LoadLevel;
import com.mieyde.tx.common.loader.Scope;
import com.mieyde.tx.config.FileConfigFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

/**
 * @author 我吃稀饭面
 * @date 2023/6/27 21:54
 */
@LoadLevel(name = FileConfigFactory.DEFAULT_TYPE,scope = Scope.PROTOTYPE)
public class SimpleFileConfig implements FileConfig{

    private Config config;

    public SimpleFileConfig(File file) {
        config = ConfigFactory.parseFile(file);
    }

    @Override
    public String getContent(String path) {
        return config.getString(path);
    }
}
