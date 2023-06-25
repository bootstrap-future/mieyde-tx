package com.mieyde.tx.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 我吃稀饭面
 * @date 2023/6/25 14:21
 */
public class MieydeException extends RuntimeException{

    private static final Logger log = LoggerFactory.getLogger(MieydeException.class);

    private static final long serialVersionUID = 5531071561516852688L;

    private final MieydeErrorCode errCode;

    public MieydeException() {
        this(MieydeErrorCode.UnknownError);
    }

    public MieydeException(MieydeErrorCode errCode) {
        this(errCode.getErrMsg(),errCode);
    }

    public MieydeException(String errMsg) {
        this(errMsg,MieydeErrorCode.UnknownError);
    }

    public MieydeException(String errMsg,MieydeErrorCode errCode) {
        this(null,errMsg,errCode);
    }

    public MieydeException(Throwable cause, String msg, MieydeErrorCode errCode) {
        super(msg, cause);
        this.errCode = errCode;
    }

    public MieydeException(Throwable throwable) {
        this(throwable,throwable.getMessage());
    }

    public MieydeException(Throwable throwable,String errMsg) {
        this(throwable,errMsg,MieydeErrorCode.UnknownError);
    }

    public MieydeErrorCode getErrCode() {
        return errCode;
    }

    public static MieydeException of(Throwable e){
        return of("",e);
    }

    public static MieydeException of(String errMsg,Throwable e){
        log.error(errMsg,e.getMessage(),e);
        if (e instanceof MieydeException){
            return ((MieydeException) e);
        }
        return new MieydeException(e,errMsg);
    }
}
