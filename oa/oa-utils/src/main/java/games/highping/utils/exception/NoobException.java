package games.highping.utils.exception;

import games.highping.utils.result.ResultCodeEnum;
import lombok.Data;

@Data
public class NoobException extends RuntimeException{

    private Integer code;
    private String massage;

    public NoobException(Integer code, String massage) {
        super(massage);
        this.code = code;
        this.massage = massage;
    }

    public NoobException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.massage = resultCodeEnum.getMessage();
    }

}
