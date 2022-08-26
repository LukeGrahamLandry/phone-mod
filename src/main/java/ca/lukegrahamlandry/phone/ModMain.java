package ca.lukegrahamlandry.phone;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.data.PhoneDataStorage;
import ca.lukegrahamlandry.phone.network.NetworkHandler;
import ca.lukegrahamlandry.phone.network.clientbound.SyncPhoneMessagesPacket;
import ca.lukegrahamlandry.phone.objects.ClearPhoneCommand;
import ca.lukegrahamlandry.phone.objects.PhoneItem;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.item.Item;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

@Mod(ModMain.MOD_ID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ModMain {
    public static final String MOD_ID = "anonphone";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public ModMain() {
        NetworkHandler.registerMessages();
        ArgumentTypes.register(ModMain.MOD_ID + "channel", ClearPhoneCommand.PhoneChannelArgumentType.class, new ArgumentSerializer<>(ClearPhoneCommand.PhoneChannelArgumentType::new));

        ITEMS.register("public_phone", () -> new PhoneItem("public"));
        ITEMS.register("encrypted_phone", () -> new PhoneItem("encrypted"));
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
       if (!event.getPlayer().level.isClientSide()){
           for (Map.Entry<String, List<MessageData>> channel : PhoneDataStorage.get((ServerWorld) event.getPlayer().level).messages.entrySet()){
               NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncPhoneMessagesPacket(channel.getValue(), channel.getKey(), true));
           }
       }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        event.getDispatcher().register(ClearPhoneCommand.register());
    }
}
