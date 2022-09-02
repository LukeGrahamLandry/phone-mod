package ca.lukegrahamlandry.phone.network.clientbound;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.gui.PhoneGui;
import ca.lukegrahamlandry.phone.objects.PhoneItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class ClientHelper {
    public static void handleSyncPacket(SyncPhoneMessagesPacket packet){
        if (packet.replace) {
            MessageData.clientMessages.put(packet.channel, packet.messages);
            if (Minecraft.getInstance().screen instanceof PhoneGui && ((PhoneGui) Minecraft.getInstance().screen).channel.equals(packet.channel)) {
                Minecraft.getInstance().setScreen(null);
            }
        }
        else {
            if (!MessageData.clientMessages.containsKey(packet.channel)) MessageData.clientMessages.put(packet.channel, new ArrayList<>());

            MessageData.clientMessages.get(packet.channel).addAll(packet.messages);
            showNotif(packet.channel, packet.messages);
        }
    }


    static Style PUBLIC_COLOUR = Style.EMPTY.withColor(TextFormatting.GRAY);
    static Style ENCRYPTED_COLOUR = Style.EMPTY.withColor(Color.parseColor("#882123"));

    private static void showNotif(String channel, List<MessageData> msgs) {
        PlayerEntity player = Minecraft.getInstance().player;

        if (player == null) return;


        if (Minecraft.getInstance().screen instanceof PhoneGui) {
            PhoneGui phone = (PhoneGui) Minecraft.getInstance().screen;
            if (phone.channel.equals(channel)){
                List<MessageData> messagesToAdd = new ArrayList<>(msgs);
                messagesToAdd.removeIf((m) -> phone.phoneID == m.phoneId);  // ones sent by you are already added
                phone.displayMessages.addAll(messagesToAdd);
                if (!messagesToAdd.isEmpty()) phone.init();
                return;
            }
        }

        for (ItemStack stack : player.inventory.items){
            if (stack.getItem() instanceof PhoneItem){
                if (((PhoneItem) stack.getItem()).channel.equals(channel)){
                    player.displayClientMessage(new StringTextComponent("Message received.").withStyle(channel.equals("encrypted") ? ENCRYPTED_COLOUR : PUBLIC_COLOUR), false);
                    SoundEvent sound = Registry.SOUND_EVENT.get(new ResourceLocation("proximitybase:phone"));
                    if (sound != null) player.level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 1, 1, false);
                    break;
                }
            }
        }
    }


    public static void openGui(String channel, int id){
        Minecraft.getInstance().setScreen(new PhoneGui(channel, id));
    }
}
