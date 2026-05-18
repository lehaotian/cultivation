package cls.cn.base.config;

import cls.cn.base.reflection.ProtoRegister;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Order(0)
@AllArgsConstructor
public class BaseConfig {

    @PostConstruct
    public void init() {
        ProtoRegister.register();
    }
}
