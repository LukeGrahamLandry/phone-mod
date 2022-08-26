package ca.lukegrahamlandry.phone.network.clientbound;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.gui.PhoneGui;
import ca.lukegrahamlandry.phone.objects.PhoneItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
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
            else {
                if (!MessageData.clientMessages.containsKey(packet.channel)) MessageData.clientMessages.put(packet.channel, new ArrayList<>());

                MessageData.clientMessages.get(packet.channel).addAll(packet.messages);
                showNotif(packet.channel);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void showNotif(String channel) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || (Minecraft.getInstance().screen instanceof PhoneGui && ((PhoneGui) Minecraft.getInstance().screen).channel.equals(channel))) return;
        for (ItemStack stack : player.inventory.items){
            if (stack.getItem() instanceof PhoneItem){
                if (((PhoneItem) stack.getItem()).channel.equals(channel)){
                    player.displayClientMessage(new StringTextComponent("Phone Message Received (" + channel + ")"), false);
                    break;
                }
            }
        }
    }
}
