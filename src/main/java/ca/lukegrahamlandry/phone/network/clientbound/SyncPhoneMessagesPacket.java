package ca.lukegrahamlandry.phone.network.clientbound;

import ca.lukegrahamlandry.phone.data.MessageData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncPhoneMessagesPacket {
    private final List<MessageData> messages;
    private final String channel;
    private boolean replace;

    public SyncPhoneMessagesPacket(List<MessageData> messages, String channel, boolean replace){
        this.messages = messages;
        this.channel = channel;
        this.replace = replace;
    }

    public static SyncPhoneMessagesPacket decode(PacketBuffer buf) {
        String channel = buf.readUtf(32767);
        boolean replace = buf.readBoolean();
        int size = buf.readInt();
        List<MessageData> messages = new ArrayList<>();
        for (int i=0;i<size;i++){
            messages.add(new MessageData(buf.readInt(), buf.readUtf(32767)));
        }

        return new SyncPhoneMessagesPacket(messages, channel, replace);
    }

    public static void encode(SyncPhoneMessagesPacket packet, PacketBuffer buf) {
        buf.writeUtf(packet.channel);
        buf.writeBoolean(packet.replace);
        buf.writeInt(packet.messages.size());
        for (MessageData msg : packet.messages){
            buf.writeInt(msg.phoneId);
            buf.writeUtf(msg.message);
        }
    }

    public static void handle(SyncPhoneMessagesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (packet.replace) MessageData.clientMessages.put(packet.channel, packet.messages);
            else MessageData.clientMessages.get(packet.channel).addAll(packet.messages);
        });
        ctx.get().setPacketHandled(true);
    }
}
