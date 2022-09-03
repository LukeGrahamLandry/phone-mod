package ca.lukegrahamlandry.phone.network;

import ca.lukegrahamlandry.phone.ModMain;
import ca.lukegrahamlandry.phone.network.clientbound.OpenPhoneGuiPacket;
import ca.lukegrahamlandry.phone.network.clientbound.SyncPhoneMessagesPacket;
import ca.lukegrahamlandry.phone.network.serverbound.SendPhoneMessagePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static void registerMessages(){
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ModMain.MOD_ID, "packets"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(ID++, SendPhoneMessagePacket.class, SendPhoneMessagePacket::encode, SendPhoneMessagePacket::decode, SendPhoneMessagePacket::handle);
        INSTANCE.registerMessage(ID++, SyncPhoneMessagesPacket.class, SyncPhoneMessagesPacket::encode, SyncPhoneMessagesPacket::decode, SyncPhoneMessagesPacket::handle);
        INSTANCE.registerMessage(ID++, OpenPhoneGuiPacket.class, OpenPhoneGuiPacket::encode, OpenPhoneGuiPacket::decode, OpenPhoneGuiPacket::handle);
    }
}