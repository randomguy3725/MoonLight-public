package wtf.moonlight.gui.click.neverlose.components.espelements;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.features.modules.impl.visual.ESP;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.List;

public class NameElement extends Component {
    private int x;
    private int y;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
            x = INSTANCE.getNeverLose().espPreviewComponent.getPosX() + INSTANCE.getNeverLose().getWidth() + 120;
            y = (int) (INSTANCE.getNeverLose().espPreviewComponent.getPosY() + 35 + 75 * (1 - INSTANCE.getNeverLose().espPreviewComponent.getElementsManage().open.getOutput()));

        if (INSTANCE.getModuleManager().getModule(ESP.class).tags.get()) {
            final FontRenderer fontRenderer = mc.fontRendererObj;
            final String name = mc.thePlayer.getDisplayName().getFormattedText() + " " + (MathUtils.roundToHalf(mc.thePlayer.getHealth())) + EnumChatFormatting.RED + "❤";

            if (INSTANCE.getModuleManager().getModule(ESP.class).tagsBackground.get()) {

                RenderUtils.drawRect(
                        (x - fontRenderer.getStringWidth(name) / 2f * INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get()),
                        (y - 2),
                        fontRenderer.getStringWidth(name) * INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get(),
                        fontRenderer.FONT_HEIGHT * INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get() + 1,
                        0x96000000);
            }
            fontRenderer.drawScaledString(name, (x - fontRenderer.getStringWidth(name) / 2f * INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get()), y, INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get(), -1);


            if (INSTANCE.getModuleManager().getModule(ESP.class).item.get()) {
                List<ItemStack> items = new ArrayList<>();
                if (mc.thePlayer.getHeldItem() != null) {
                    items.add(mc.thePlayer.getHeldItem());
                }
                for (int index = 3; index >= 0; index--) {
                    ItemStack stack = mc.thePlayer.inventory.armorInventory[index];
                    if (stack != null) {
                        items.add(stack);
                    }
                }
                float armorX = x - fontRenderer.getStringWidth(name) / 2f - ((float) (items.size() * 18) / 2) * INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get();

                for (ItemStack stack : items) {
                    RenderUtils.renderItemStack(stack, armorX, y - 25 * INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get(), INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get() + INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get() / 2, true);
                    armorX += 18 * INSTANCE.getModuleManager().getModule(ESP.class).tagsSize.get();
                }
            }
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }
}