package com.moyu.redarmy.core.result;

public class ResultGenerator {
    public static Result success() {
        Result result = new Result();
        result.setCode(ResultCode.SUCCESS.code);
        result.setStatus(Result.Status.SUCCESS.status);
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(ResultCode.SUCCESS.code);
        result.setStatus(Result.Status.SUCCESS.status);
        result.setData(data);
        return result;
    }

    public static Result fail(String msg) {
        Result result = new Result();
        result.setCode(ResultCode.FAIL.code);
        result.setStatus(Result.Status.FAIL.status);
        result.setMsg(msg);
        return result;
    }
    //可增加统一异常处理类
}
