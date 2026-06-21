package larina.lessons.send_to_tax.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final String code;
    private final Integer httpCode;

    public ServiceException(final ErrorCode errorCode, final Object... args) {
        super(errorCode.formatDescription(args));
        this.code = errorCode.getCode();
        this.httpCode = errorCode.getHttpCode();
    }
}
