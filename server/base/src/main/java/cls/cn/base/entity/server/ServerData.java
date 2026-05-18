package cls.cn.base.entity.server;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 服务器数据
 */
@Data
@Document(collection = "servers")
public class ServerData {
    /**
     * 服务器id
     */
    @Id
    private String id;
    /**
     * 服务器名称
     */
    private String name;
    /**
     * 服务器类型
     */
    private int type;
    /**
     * 服务器状态
     */
    private int status;
    /**
     * 服务器地址
     */
    private String address;
}
