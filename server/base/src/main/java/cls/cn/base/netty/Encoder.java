package cls.cn.base.netty;

import cls.cn.base.reflection.ProtoRegister;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码
 * 消息结构 +----------+ | 总长度 | +----------+ | 消息ID | +----------+ | 主体数据 |
 * +----------+ 总长度 = 4(长度标示) + 4(消息ID) + 主体数据
 */
public class Encoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        int msgId = ProtoRegister.getProtoMsgId(msg);
        byte[] bytes = msg.toByteArray();
        out.writeInt(8 + bytes.length);
        out.writeInt(msgId);
        out.writeBytes(bytes);
    }
}