package com.mieyde.tx.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 14:12
 */
@AllArgsConstructor
@Getter
public enum MieydeErrorCode {

    UnknownError("10000","未知错误","请联系管理员")
    ;
    private String errCode;
    private String errMsg;
    private String errDispose;

    @Override
    public String toString() {
        return String.format("[%s] [%s] [%s]",errCode,errMsg,errDispose);
    }
}
