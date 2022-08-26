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
    private final int phoneID;
    TextFieldWidget message;
    List<MessageData> displayMessages;
    PhoneMessageList messageList;

    protected int xSize = 250;
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
    protected void init() {
        super.init();

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        if (this.children.size() == 0){
            this.messageList = new PhoneMessageList(this, Minecraft.getInstance(), this.guiLeft, this.xSize, this.ySize, this.guiTop, this.guiTop + this.ySize - 30);
            this.message = new TextFieldWidget(this.font, this.guiLeft, this.guiTop + this.ySize - 15, this.xSize, 20, this.message, new StringTextComponent("Phone Message"));
            this.addButton(this.message);
            this.addWidget(this.messageList);
        }

        this.setFocused(this.message);
        this.message.setValue("");
        this.message.setMaxLength(999);
        this.messageList.setMessages(this.displayMessages, this.phoneID);
        this.messageList.setScrollAmount(this.messageList.getMaxScroll());
    }

    private void sendPhoneMessage() {
        NetworkHandler.INSTANCE.sendToServer(new SendPhoneMessagePacket(this.message.getValue(), this.channel, this.phoneID));
        this.displayMessages.add(new MessageData(this.phoneID, this.message.getValue()));
        init();
    }

    @Override
    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        return this.messageList.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
    }

    @Override
    public boolean keyPressed(int keyCode, int p_231046_2_, int p_231046_3_) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && !hasShiftDown()){
            System.out.println(this.message.getValue());
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
        this.blit(p_230430_1_, this.guiLeft, 0, 0, 0, 0, this.xSize, this.ySize + 10, 512, 512);

        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.messageList.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    }
}
