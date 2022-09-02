package ca.lukegrahamlandry.phone.gui;

import ca.lukegrahamlandry.phone.ModMain;
import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.network.NetworkHandler;
import ca.lukegrahamlandry.phone.network.serverbound.SendPhoneMessagePacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PhoneGui extends Screen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(ModMain.MOD_ID, "textures/gui.png");
    public final String channel;
    public final int phoneID;
    TextFieldWidget message;
    public List<MessageData> displayMessages;
    PhoneMessageList messageList;

    protected int xSize = 153;
    private int ySize = 190;
    private int guiLeft;
    private int guiTop;

    public PhoneGui(String channel, int phoneID) {
        super(new StringTextComponent("Phone"));
        this.channel = channel;
        this.phoneID = phoneID;
        this.displayMessages = MessageData.clientMessages.containsKey(channel) ? new ArrayList<>(MessageData.clientMessages.get(channel)) : new ArrayList<>();
    }

    @Override
    public void init() {
        super.init();

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = 0;

        if (this.children.size() == 0){
            this.messageList = new PhoneMessageList(this, Minecraft.getInstance(), this.guiLeft, this.xSize, this.ySize, this.guiTop /*+25*/, this.guiTop + this.ySize);
            this.message = new TextFieldWidget(this.font, this.guiLeft, this.guiTop + this.ySize, this.xSize, 20, this.message, new StringTextComponent("Phone Message"));
            this.addButton(this.message);
            this.addWidget(this.messageList);
            this.message.setMaxLength(999);
        }

        this.setFocused(this.message);
        this.messageList.setMessages(this.displayMessages, this.phoneID);
        this.messageList.setScrollAmount(this.messageList.getMaxScroll());
    }

    private void sendPhoneMessage() {
        NetworkHandler.INSTANCE.sendToServer(new SendPhoneMessagePacket(this.message.getValue(), this.channel, this.phoneID));
        this.displayMessages.add(new MessageData(this.phoneID, this.message.getValue()));
        this.message.setValue("");
        init();
    }

    @Override
    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        return this.messageList.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
    }

    @Override
    public boolean keyPressed(int keyCode, int p_231046_2_, int p_231046_3_) {
        if (keyCode == GLFW.GLFW_KEY_ENTER){ // && !hasShiftDown()){
            sendPhoneMessage();
            return true;
        }

        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }


        this.message.setFocus(true);
        return this.message.keyPressed(keyCode, p_231046_2_, p_231046_3_);
    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.minecraft.getTextureManager().bind(TEXTURE);
        this.blit(p_230430_1_, this.guiLeft, this.guiTop, 0, "encrypted".equals(this.channel) ? 261 : 0, 66, this.xSize, this.ySize, 512, 512);

        this.messageList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);

    }
}
