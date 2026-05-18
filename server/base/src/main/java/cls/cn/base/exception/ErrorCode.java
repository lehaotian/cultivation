package cls.cn.base.exception;

public enum ErrorCode {
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(1),
    /**
     * 参数错误
     */
    PARAM_ERROR(2),
    /**
     * 服务器错误
     */
    SERVER_ERROR(3),
    /**
     * 道具不足
     */
    ITEM_NOT_ENOUGH(4),
    /**
     * 无效的请求
     */
    INVALID_REQUEST(6),
    /**
     * 无效的签名
     */
    INVALID_SIGN(7),
    /**
     * 无效的时间戳
     */
    INVALID_TIMESTAMP(8),

    /**
     * 无效的货币
     */
    INVALID_CURRENCY(14),

    /**
     * 无效的昵称
     */
    INVALID_NICKNAME(21),
    ;
    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
