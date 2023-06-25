package com.mieyde.tx.common;

import cn.hutool.core.util.StrUtil;

import java.util.List;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 13:48
 */
public class XID {
    /**
     * 端口
     */
    private static int port;
    /**
     * ip地址
     */
    private static String address;

    public static void setPort(int port){ XID.port = port;}

    public static int getPort(){return port;}

    public static void setAddress(String address){XID.address = address;}

    public static String getAddress(){return address;}

    /**
     * 生成全局事务id——xid
     */
    public static String generateXID(long transactionId){
        return StrUtil.join(":",address,port,transactionId);
    }

    /**
     * 根据全局事务id提取transactionId
     */
    public static long getTransactionId(String xid){
        if (StrUtil.isBlank(xid)){
            return -1;
        }
        List<String> split = StrUtil.split(xid, ":");
        return Long.parseLong(split.get(split.size() - 1));
    }

    /**
     * 获取地址
     */
    public static String getIpAddressAndPort() {
        return StrUtil.join(":",address,port);
    }

}
