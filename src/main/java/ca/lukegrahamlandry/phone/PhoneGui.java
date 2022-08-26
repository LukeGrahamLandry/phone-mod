package ca.lukegrahamlandry.phone;

import ca.lukegrahamlandry.phone.data.MessageData;
import ca.lukegrahamlandry.phone.network.NetworkHandler;
import ca.lukegrahamlandry.phone.network.serverbound.SendPhoneMessagePacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class PhoneGui extends Screen {
    private final String channel;
    private final int phoneID;
    TextFieldWidget message;
    List<MessageData> displayMessages;

    private int xSize = 100;
    private int ySize = 200;
    private int guiLeft;
    private int guiTop;

    public PhoneGui(String channel, int phoneID) {
        super(new StringTextComponent("Phone"));
        this.channel = channel;
        this.phoneID = phoneID;
        this.displayMessages = MessageData.clientMessages.containsKey(channel) ? MessageData.clientMessages.get(channel) : new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.message = new TextFieldWidget(this.font, this.guiLeft + 13, (this.height - this.ySize + 50) / 2, 150, 20, this.message, new StringTextComponent("Phone Message"));
        this.addButton(this.message);
        this.setFocused(this.message);
    }

    private void sendPhoneMessage() {
        NetworkHandler.INSTANCE.sendToServer(new SendPhoneMessagePacket(this.message.getValue(), this.channel, this.phoneID));
        this.displayMessages.add(new MessageData(this.phoneID, this.message.getValue()));
        this.message.setValue("");
        init();
    }

    @Override
    public boolean keyPressed(int keyCode, int p_231046_2_, int p_231046_3_) {
        if (keyCode == GLFW.GLFW_KEY_ENTER && !hasShiftDown()){
            sendPhoneMessage();
            return true;
        }
        return super.keyPressed(keyCode, p_231046_2_, p_231046_3_);
    }
}
