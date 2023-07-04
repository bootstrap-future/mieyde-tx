package com.mieyde.tx.config;

import com.mieyde.tx.common.thread.NamedThreadFactory;
import com.mieyde.tx.common.util.FileUtils;
import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtls;
import com.mieyde.tx.config.file.FileConfig;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 我吃稀饭面
 * @date 2023/6/28 22:18
 */
public class FileConfiguration extends AbstractConfiguration{

    private ExecutorService executorService;
    private FileConfig fileConfig;
    private static final int CORE_CONFIG_OPERATE_THREAD = 1;

    private static final int MAX_CONFIG_OPERATE_THREAD = 2;

    private static final String REGISTRY_TYPE = "file";

    public FileConfiguration(String name) {
        File file = null;
        try {
            file = FileUtils.load(name);
        }catch (Exception e){

        }
        if (ObjectUtils.isNull(file)){
            this.fileConfig = FileConfigFactory.load();
        }else {
            this.fileConfig = FileConfigFactory.load(file);
        }

        this.executorService = new ThreadPoolExecutor(CORE_CONFIG_OPERATE_THREAD,
                MAX_CONFIG_OPERATE_THREAD,
                Integer.MAX_VALUE,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("configOperate",MAX_CONFIG_OPERATE_THREAD));
    }

    @Override
    public String getTypeName() {
        return REGISTRY_TYPE;
    }

    @Override
    public String getLatestConfig(String dataId, String defaultValue) {
        String value = getConfigBySystem(dataId);
        if (StringUtls.isNotBlank(value)){
            return value;
        }

        ConfigFuture configFuture = new ConfigFuture(dataId, defaultValue, ConfigFuture.ConfigOperation.GET);
        executorService.submit(new ConfigOperateRunnable(configFuture));
        Object result = configFuture.get();
        return ObjectUtils.isNull(result) ? null : String.valueOf(result);
    }

    @Override
    public boolean putConfig(String dataId, String content) {
        ConfigFuture configFuture = new ConfigFuture(dataId, content, ConfigFuture.ConfigOperation.PUT);
        executorService.submit(new ConfigOperateRunnable(configFuture));
        return (Boolean) configFuture.get();
    }

    @Override
    public boolean putConfigIfAbsent(String dataId, String content) {
        ConfigFuture configFuture = new ConfigFuture(dataId, content, ConfigFuture.ConfigOperation.PUTIFABSENT);
        executorService.submit(new ConfigOperateRunnable(configFuture));
        return (Boolean) configFuture.get();
    }

    @Override
    public boolean removeConfig(String dataId) {
        ConfigFuture configFuture = new ConfigFuture(dataId, null, ConfigFuture.ConfigOperation.REMOVE);
        executorService.submit(new ConfigOperateRunnable(configFuture));
        return (Boolean) configFuture.get();
    }

    class ConfigOperateRunnable implements Runnable{

        private ConfigFuture configFuture;

        public ConfigOperateRunnable(ConfigFuture configFuture) {
            this.configFuture = configFuture;
        }

        @Override
        public void run() {
            if (ObjectUtils.isNull(configFuture) || configFuture.isTimeout()){
                return;
            }

            try {
                if (ObjectUtils.equals(configFuture.getOperation(),ConfigFuture.ConfigOperation.GET)) {
                    String result = fileConfig.getContent(configFuture.getDataId());
                    configFuture.setResult(result);
                }else if (ObjectUtils.equals(configFuture.getOperation(),ConfigFuture.ConfigOperation.PUT)) {
                    configFuture.setResult(Boolean.TRUE);
                }else if (ObjectUtils.equals(configFuture.getOperation(),ConfigFuture.ConfigOperation.PUTIFABSENT)) {
                    configFuture.setResult(Boolean.TRUE);
                }else if (ObjectUtils.equals(configFuture.getOperation(),ConfigFuture.ConfigOperation.REMOVE)) {
                    configFuture.setResult(Boolean.TRUE);
                }
            }catch (Exception e){
                setFailResult(configFuture);
            }
        }

        private void setFailResult(ConfigFuture configFuture) {
            if (ObjectUtils.equals(configFuture.getOperation(),ConfigFuture.ConfigOperation.GET)) {
                String result = configFuture.getContent();
                configFuture.setResult(result);
            } else {
                configFuture.setResult(Boolean.FALSE);
            }
        }
    }
}
