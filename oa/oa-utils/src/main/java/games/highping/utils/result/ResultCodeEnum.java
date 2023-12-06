package games.highping.utils.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    server_error(500, "服务器异常"),
    data_error(201, "数据异常"),
    login_error(208, "登录失败"),
    permission_error(209, "权限不足");

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
