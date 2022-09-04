package ca.lukegrahamlandry.phone.data;

import ca.lukegrahamlandry.phone.ModMain;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneDataStorage extends WorldSavedData {
    public PhoneDataStorage() {
        super(ModMain.MOD_ID);

        // initialize the channels we want to exist
        // case: clients play -> log out -> reset world data -> rejoin (without restarting client)
        // result: no channels exist so the fact that they should be cleared never gets synced to the client so MessageData#clientMessages keeps history that is no longer on server
        getMessages("public");
        getMessages("encrypted");
    }

    @Override
    public void load(CompoundNBT tag) {
        for (String channel : tag.getAllKeys()){
            List<MessageData> channelMessages = getMessages(channel);
            CompoundNBT nbt = tag.getCompound(channel);

            int i = 0;
            while (nbt.contains(String.valueOf(i))){
                CompoundNBT savedMessage = nbt.getCompound(String.valueOf(i));
                channelMessages.add(new MessageData(savedMessage.getInt("id"), savedMessage.getString("msg")));
                i++;
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        for (Map.Entry<String, List<MessageData>> data : messages.entrySet()){
            CompoundNBT nbt = new CompoundNBT();

            int i = 0;
            for (MessageData msg : data.getValue()){
                CompoundNBT savedMessage = new CompoundNBT();

                savedMessage.putInt("id", msg.phoneId);
                savedMessage.putString("msg", msg.message);

                nbt.put(String.valueOf(i), savedMessage);
                i++;
            }

            tag.put(data.getKey(), nbt);
        }

        return tag;
    }

    public static PhoneDataStorage get(ServerWorld world){
        return world.getServer().overworld().getDataStorage().computeIfAbsent(PhoneDataStorage::new, ModMain.MOD_ID);
    }

    public Map<String, List<MessageData>> messages = new HashMap<>();

    public List<MessageData> getMessages(String channel) {
        if (!messages.containsKey(channel)) messages.put(channel, new ArrayList<>());
        return messages.get(channel);
    }

    public void addMessage(String channel, MessageData msg) {
        this.getMessages(channel).add(msg);
        this.setDirty();
    }
}
