package cls.cn.base.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class ClassConfig {
    // 缓存文件MD5
    private final Map<String, String> fileMd5Map = new HashMap<>();

    @Value("${classPath:}")
    private String classPath;


    public void checkFileChanges() {
        if (classPath == null || classPath.isEmpty()) {
            return;
        }
        // 遍历目录中的文件
        try (Stream<Path> list = Files.list(Path.of(classPath))) {
            list.filter(this::isFileChanged)
                    .forEach(this::loadJava);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadJava(Path path) {
        try {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFileChanged(Path path) {
        if (!path.toString().endsWith(".java")) {
            return false;
        }
        try (InputStream is = Files.newInputStream(path)) {
            String fileName = path.getFileName().toString();
            String md5 = DigestUtils.md5DigestAsHex(is);
            String oldMd5 = fileMd5Map.get(fileName);
            if (oldMd5 == null) {
                // 文件第一次加载
                fileMd5Map.put(fileName, md5);
                return false;
            }
            if (Objects.equals(oldMd5, md5)) {
                // 文件没有变化
                return false;
            }
            fileMd5Map.put(fileName, md5);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}