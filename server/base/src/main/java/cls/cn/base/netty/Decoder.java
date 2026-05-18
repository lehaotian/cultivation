package cls.cn.base.netty;

import cls.cn.base.reflection.ProtoRegister;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 消息解码器
 * 消息结构 +----------+ | 总长度 | +----------+ | 消息ID | +----------+ | 主体数据 |
 * +----------+ 总长度 = 4(长度标示) + 4(消息ID) + 主体数据
 */
public class Decoder extends LengthFieldBasedFrameDecoder {

    public Decoder() {
        super(1024 * 1024, 0, 4, -4, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        try {
            int msgId = frame.readInt();
            byte[] bodyBytes = new byte[frame.readableBytes()];
            frame.readBytes(bodyBytes);
            return ProtoRegister.parseFromProto(msgId, bodyBytes);
        } finally {
            frame.release();
        }
    }
}