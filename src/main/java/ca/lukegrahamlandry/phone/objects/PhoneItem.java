package ca.lukegrahamlandry.phone.objects;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Random;

public class PhoneItem extends Item {
    public final String channel;
    public PhoneItem(Properties p_i48487_1_, String channel) {
        super(p_i48487_1_);
        this.channel = channel;
    }

    public PhoneItem(String channel) {
        this(new Item.Properties(), channel);
    }

    private static final String PHONE_ID_KEY = "phone_id";
    public static int getId(ItemStack stack){
        if (!stack.hasTag()) stack.setTag(new CompoundNBT());
        if (!stack.getTag().contains(PHONE_ID_KEY)) stack.getTag().putInt(PHONE_ID_KEY, new Random().nextInt());
        return stack.getTag().getInt(PHONE_ID_KEY);
    }

    private void openGui(ItemStack stack){
        Minecraft.getInstance().setScreen(new PhoneGui(this.channel, getId(stack)));
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world.isClientSide() && hand == Hand.MAIN_HAND) {
            openGui(player.getItemInHand(hand));
            return ActionResult.success(player.getItemInHand(hand));
        }

        return super.use(world, player, hand);
    }

    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        if (ctx.getLevel().isClientSide() && ctx.getHand() == Hand.MAIN_HAND) {
            openGui(ctx.getPlayer().getItemInHand(ctx.getHand()));
            return ActionResultType.SUCCESS;
        }

        return super.useOn(ctx);
    }

    // have to call getId on server side so it saves and syncs to client
    @Override
    public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(p_77663_1_, p_77663_2_, p_77663_3_, p_77663_4_, p_77663_5_);
        getId(p_77663_1_);
    }
}
