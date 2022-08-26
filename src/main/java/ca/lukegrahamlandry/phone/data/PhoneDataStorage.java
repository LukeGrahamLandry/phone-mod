package ca.lukegrahamlandry.phone.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneDataStorage extends WorldSavedData {
    public PhoneDataStorage(String p_i2141_1_) {
        super(p_i2141_1_);
    }

    @Override
    public void load(CompoundNBT p_76184_1_) {

    }

    @Override
    public CompoundNBT save(CompoundNBT p_189551_1_) {
        return null;
    }

    public static PhoneDataStorage get(){
        return null;
    }

    public Map<String, List<MessageData>> messages = new HashMap<>();

    public List<MessageData> getMessages(String channel) {
        if (!messages.containsKey(channel)) messages.put(channel, new ArrayList<>());
        return messages.get(channel);
    }
}
