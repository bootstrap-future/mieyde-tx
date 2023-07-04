package com.mieyde.tx.config;

import com.mieyde.tx.common.util.ObjectUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author 我吃稀饭面
 * @date 2023/7/4 15:29
 */
public class ConfigFuture {

    private static final long DEFAULT_CONFIG_TIMEOUT = 5 * 1000;
    private long start = System.currentTimeMillis();
    private long timeoutMills;
    private String dataId;
    private String content;
    private ConfigOperation operation;
    private transient CompletableFuture<Object> origin = new CompletableFuture<>();

    public ConfigFuture(String dataId, String content, ConfigOperation operation) {
        this(dataId, content, operation, DEFAULT_CONFIG_TIMEOUT);
    }

    public ConfigFuture(String dataId, String content, ConfigOperation operation,long timeoutMills) {
        this.dataId = dataId;
        this.content = content;
        this.operation = operation;
        this.timeoutMills = timeoutMills;
    }

    public boolean isTimeout(){
        return System.currentTimeMillis() - start > timeoutMills;
    }

    public Object get(){
        return get(this.timeoutMills,TimeUnit.MILLISECONDS);
    }

    public Object get(long timeoutMills, TimeUnit timeUnit){
        this.timeoutMills = timeUnit.toMillis(timeoutMills);
        Object result = null;
        try {
            result = origin.get(timeoutMills,timeUnit);
        } catch (InterruptedException e) {
            return getFailResult();
        } catch (ExecutionException e) {
            return getFailResult();
        } catch (TimeoutException e) {
            return getFailResult();
        }
        if (ObjectUtils.equals(operation,ConfigOperation.GET)){
            return ObjectUtils.isNull(result) ? content : result;
        }else {
            return ObjectUtils.isNull(result) ? Boolean.FALSE : result;
        }
    }

    private Object getFailResult(){
        if (operation == ConfigOperation.GET) {
            return content;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(Object result) {
        origin.complete(result);
    }

    /**
     * Gets data id.
     *
     * @return the data id
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * Sets data id.
     *
     * @param dataId the data id
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets operation.
     *
     * @return the operation
     */
    public ConfigOperation getOperation() {
        return operation;
    }

    /**
     * Sets operation.
     *
     * @param operation the operation
     */
    public void setOperation(ConfigOperation operation) {
        this.operation = operation;
    }

    public enum ConfigOperation {
        /**
         * Get config operation.
         */
        GET,
        /**
         * Put config operation.
         */
        PUT,
        /**
         * Putifabsent config operation.
         */
        PUTIFABSENT,
        /**
         * Remove config operation.
         */
        REMOVE
    }
}
