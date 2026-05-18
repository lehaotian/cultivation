package cls.cn.logic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "cls.cn")
@EnableMongoRepositories(basePackages = "cls.cn.base.repository")
@EntityScan(basePackages = "cls.cn.base.entity")
@EnableAsync
@EnableScheduling
public class LogicApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogicApplication.class, args);
    }

}
