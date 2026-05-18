package cls.cn.base.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintStream;
import java.util.Arrays;

@Getter
@Slf4j
public class GameException extends RuntimeException {
    private final ErrorCode code;

    public GameException(ErrorCode code, String format, Object... arguments) {
        super(replacePlaceholders(format, arguments));
        this.code = code;
    }

    public GameException(ErrorCode code, Throwable cause, String format, Object... arguments) {
        super(replacePlaceholders(format, arguments), cause);
        this.code = code;
    }

    // 自定义方法，替换 {} 占位符
    private static String replacePlaceholders(String format, Object... arguments) {
        StringBuilder result = new StringBuilder(format);
        int index = 0;
        for (Object arg : arguments) {
            int placeholderIndex = result.indexOf("{}");
            if (placeholderIndex != -1) {
                result.replace(placeholderIndex, placeholderIndex + 2, arg.toString());
            } else {
                break;
            }
            index++;
        }
        return result.toString();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        StackTraceElement[] trace = getStackTrace();
        s.println(this);
        Arrays.stream(trace)
                .filter(element -> element.getClassName().startsWith("cls.cn"))
                .forEach(element -> s.println("\tat " + element));
        if (getCause() != null) {
            s.print("Caused by: ");
            getCause().printStackTrace(s);
        }
    }

}
