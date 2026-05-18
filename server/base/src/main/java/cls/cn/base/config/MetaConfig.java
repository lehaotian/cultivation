package cls.cn.base.config;

import cls.cn.base.meta.Tables;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import java.io.IOException;
import java.nio.file.*;

@Configuration
@Slf4j
@Order(1)
public class MetaConfig {
    @Value("${dataDir}")
    private String pathStr;
    @Getter
    private Tables tables;

    public void load() {
        Tables newTables;
        try {
            newTables = new Tables(file -> JsonParser.parseString(Files.readString(Path.of(pathStr + file + ".json"))));
            log.info("数值表加载成功！");
        } catch (Exception e) {
            log.error("数值表加载失败！", e);
            return;
        }
        tables = newTables;
    }

    @PostConstruct
    public void init() {
        // 初始加载配置
        load();
        // 启动异步监听
        watchDirectory();
    }

    /**
     * 每5秒定时检查文件变化（作为 WatchService 的补充）
     */
    @Scheduled(fixedDelay = 5000)
    @Async
    public void checkMetaChanges() {
        Path path = Path.of(pathStr);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return;
        }
        
        try {
            boolean change = false;
            try (var paths = Files.list(path)) {
                for (Path jsonPath : paths.toList()) {
                    if (jsonPath.toString().endsWith(".json")) {
                        // 检测到 .json 文件，标记为变化（简化处理，直接重新加载）
                        change = true;
                        break;
                    }
                }
            }
            
            if (change) {
                load();
                log.debug("定时任务检测到文件变化，已重新加载配置");
            }
        } catch (Exception e) {
            log.error("定时检查文件变化失败", e);
        }
    }
    
    /**
     * 使用 WatchService 异步监听目录变化
     */
    @Async
    public void watchDirectory() {
        Path path = Path.of(pathStr);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            log.error("文件目录{}不存在", pathStr);
            return;
        }
        
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            // 注册监听目录的修改事件
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            
            log.info("MetaConfig WatchService 已启动，监听目录: {}", pathStr);
            
            while (true) {
                WatchKey key = watchService.take();
                
                boolean change = false;
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    
                    // 忽略溢出事件
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    
                    // 只处理 .json 文件
                    if (!fileName.toString().endsWith(".json")) {
                        continue;
                    }
                    
                    // 检测到文件变化
                    change = true;
                    log.info("检测到文件变化: {}", fileName);
                }
                
                // 如果检测到变化，重新加载配置
                if (change) {
                    load();
                }
                
                // 重置 WatchKey，继续监听
                boolean valid = key.reset();
                if (!valid) {
                    log.warn("WatchKey 无效，停止监听");
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("WatchService 线程被中断");
        } catch (ClosedWatchServiceException e) {
            // WatchService 已关闭，正常退出
            log.info("WatchService 已关闭，停止监听");
        } catch (IOException e) {
            log.error("初始化 WatchService 失败", e);
        } catch (Exception e) {
            log.error("监听目录变化时发生错误", e);
        }
    }
    

}