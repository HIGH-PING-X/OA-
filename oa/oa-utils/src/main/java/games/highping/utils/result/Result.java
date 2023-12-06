package games.highping.utils.result;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;//状态码
    private String message;//返回信息
    private T data;//返回数据

    private Result() {
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        if (body != null) {
            result.setData(body);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> ok() {
        return build(null, ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> ok(T body) {
        return build(body, ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> fail() {
        return build(null, ResultCodeEnum.FAIL);
    }

    public static <T> Result<T> fail(T body) {
        return build(body, ResultCodeEnum.FAIL);
    }

    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }

    public Result<T> message(String message) {
        this.setMessage(message);
        return this;
    }

}
