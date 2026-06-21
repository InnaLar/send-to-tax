package larina.lessons.send_to_tax.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    ERR_CODE_001("ERR.CODE.001", "Receipt with id %s not found", 404),
    ERR_CODE_002("ERR.CODE.002", "Refund for receipt %s not created", 404),
    ERR_CODE_003("ERR.CODE.003", "Receipt with id %s already refunded", 405),
    ERR_CODE_004("ERR.CODE.004", "Outfox for receipt with id %s not created", 404),
    ERR_CODE_005("ERR.CODE.005", "Call TfkClient for receipt with id %s failed", 500);

    private final String code;
    private final String description;
    private final Integer httpCode;

    public String formatDescription(final Object... args) {
        return String.format(description, args);
    }
}
