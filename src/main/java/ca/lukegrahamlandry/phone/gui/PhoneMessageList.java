package ca.lukegrahamlandry.phone.gui;

import ca.lukegrahamlandry.phone.data.MessageData;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhoneMessageList extends ExtendedList<PhoneMessageList.Entry> {
    private final PhoneGui screen;

    public PhoneMessageList(PhoneGui p_i45049_1_, Minecraft p_i45049_2_, int x, int p_i45049_3_, int p_i45049_4_, int p_i45049_5_, int p_i45049_6_) {
        super(p_i45049_2_, p_i45049_3_, p_i45049_4_, p_i45049_5_, p_i45049_6_, 20);
        this.screen = p_i45049_1_;
        this.setRenderBackground(false);
        this.setRenderHeader(false, 0);
        this.setRenderTopAndBottom(false);
        this.targetWidth = this.screen.xSize - 50;
        this.x0 = x;
        this.x1 = this.x0 + this.x1;
    }

    public void setMessages(List<MessageData> messages, int currentPhoneID) {
        this.clearEntries();

        for (MessageData msg : messages){
            if (msg.message.isEmpty()) continue;

            List<TextComponent> lines = new ArrayList<>();
            lines.add(new StringTextComponent(""));
            String[] words = msg.message.split(" ");
            StringBuilder textLine = new StringBuilder();
            for (String word : words){
                if (font.width(textLine + " " + word) > targetWidth){
                    lines.add(new StringTextComponent(textLine.toString()));
                    textLine = new StringBuilder();
                }

                textLine.append(" ").append(word);
            }
            lines.add(new StringTextComponent(textLine.toString()));

            int w = font.width(lines.get(1));
            System.out.println(w);
            boolean sentBySelf = new Random().nextBoolean(); // msg.phoneId == currentPhoneID
            lines.forEach(line -> this.addEntry(new Entry(line, sentBySelf, w)));
        }

    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
    }

    protected int getScrollbarPosition() {
        return this.x1;
    }

    public int getRowWidth() {
        return this.width;
    }

    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    @Override
    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        return super.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
    }

    int targetWidth;
    static FontRenderer font = Minecraft.getInstance().font;

    @OnlyIn(Dist.CLIENT)
    public class Entry extends ExtendedList.AbstractListEntry<PhoneMessageList.Entry> {
        private final boolean sentBySelf;
        TextComponent text;
        int msgWidth;
        public Entry(TextComponent message, boolean sentBySelf, int w){
            this.text = message;
            this.msgWidth = w;
            this.sentBySelf = sentBySelf;
        }

        @Override
        public void render(MatrixStack stack, int index, int y, int x, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
            if (this.text.getContents().isEmpty()){

            } else {
                int xPos = PhoneMessageList.this.x0 + 5;
                if (sentBySelf){
                    xPos = PhoneMessageList.this.x0 + PhoneMessageList.this.width - this.msgWidth - 10 - 5;
                }

                PhoneMessageList.this.minecraft.getTextureManager().bind(PhoneMessageList.this.screen.TEXTURE);
                PhoneMessageList.this.screen.blit(stack, xPos, y-5, 0, 290, 0, msgWidth +10, 20, 512, 512);
                font.draw(stack, this.text, xPos, y, 0x000000);
            }
        }
    }
}