package wtf.moonlight.gui.click.dropdown.panel;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import wtf.moonlight.MoonLight;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.gui.click.IComponent;
import wtf.moonlight.gui.click.dropdown.component.ModuleComponent;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.EaseInOutQuad;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class CategoryPanel implements IComponent {
    private float x, y, dragX, dragY;
    private float width = 115, height;
    private boolean dragging, opened;
    private final EaseInOutQuad openAnimation = new EaseInOutQuad(250, 1);
    private final ModuleCategory category;
    private final CopyOnWriteArrayList<ModuleComponent> moduleComponents = new CopyOnWriteArrayList<>();

    public CategoryPanel(ModuleCategory category) {
        this.category = category;
        this.openAnimation.setDirection(Direction.BACKWARDS);

        for (Module module : MoonLight.INSTANCE.getModuleManager().getModulesByCategory(category)) {
            moduleComponents.add(new ModuleComponent(module));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        update(mouseX, mouseY);

        RenderUtils.scaleStart((float) new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / 2, (float) new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() / 2, (float) INSTANCE.getDropdownGUI().getOpeningAnimation().getOutput());

        RoundedUtils.drawRound(x, y - 2, width, (float) (19 + ((height - 19) * openAnimation.getOutput())), 6, new Color(ColorUtils.darker(INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get().getRGB(),0.2f)));
        //RoundedUtils.drawRound(x, y - 2, width, 19, 3, ColorUtils.reAlpha(INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get(), 25));
        //RoundedUtils.drawRound(x, y - 2, width, (float) (19 + ((height - 19) * openAnimation.getOutput())), 3, ColorUtils.reAlpha(INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get(), 50));

        Fonts.interRegular.get(18).drawCenteredString(category.getName(), x + width / 2, y + 4.5, -1);

        float componentOffsetY = 21;
        float distance = 7;

        for (ModuleComponent component : moduleComponents) {
            component.setX(x + distance);
            component.setY(y + componentOffsetY);
            component.setWidth(width - distance * 2);
            if (openAnimation.getOutput() > 0.7f) {
                component.drawScreen(mouseX, mouseY);
            }
            componentOffsetY += (float) (component.getHeight() * openAnimation.getOutput());
        }
        height = componentOffsetY + 4;

        RenderUtils.scaleEnd();
        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(x, y - 2, width, 19, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0 -> {
                    dragging = true;
                    dragX = x - mouseX;
                    dragY = y - mouseY;
                }
                case 1 -> opened = !opened;
            }
        }
        if (opened && !MouseUtils.isHovered2(x, y - 2, width, 19, mouseX, mouseY)) {
            moduleComponents.forEach(component -> component.mouseClicked(mouseX, mouseY, mouseButton));
        }
        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        moduleComponents.forEach(component -> component.keyTyped(typedChar, keyCode));
        IComponent.super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) dragging = false;
        moduleComponents.forEach(component -> component.mouseReleased(mouseX, mouseY, state));
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    public void update(int mouseX, int mouseY) {
        this.openAnimation.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);

        if (dragging) {
            x = (mouseX + dragX);
            y = (mouseY + dragY);
        }
    }
}
