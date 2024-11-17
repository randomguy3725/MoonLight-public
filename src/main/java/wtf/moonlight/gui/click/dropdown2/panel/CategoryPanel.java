package wtf.moonlight.gui.click.dropdown2.panel;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import wtf.moonlight.MoonLight;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.gui.click.IComponent;
import wtf.moonlight.gui.click.dropdown2.component.ModuleComponent;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.animations.impl.SmoothStepAnimation;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class CategoryPanel implements IComponent {
    private float x, y, dragX, dragY;
    private float width = 125, height;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private float verticalAmount = 0;
    private boolean dragging, opened;
    private final DecelerateAnimation openAnimation = new DecelerateAnimation(250, 1);
    private final ModuleCategory category;
    private final CopyOnWriteArrayList<ModuleComponent> moduleComponents = new CopyOnWriteArrayList<>();
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

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

        RenderUtils.scaleStart((float) new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / 2, (float) new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() / 2, (float) INSTANCE.getMoonGUI().getOpeningAnimation().getOutput());

        RenderUtils.drawRect(x, y, width, (float) (22 + ((height - 22) * openAnimation.getOutput())), new Color(0, 0, 0, 100).getRGB());
        RenderUtils.drawRect(x, y, width, 22, new Color(20, 20, 20, 120).getRGB());

        Fonts.interBold.get(21).drawStringWithShadow(category.getName(), x + 5, y + 6, -1);

        float componentOffset = 22;
        for (ModuleComponent component : moduleComponents) {
            component.setX(x);
            component.setY(y + componentOffset);
            component.setWidth(width);
            if (openAnimation.getOutput() > 0.7f) {
                component.drawScreen(mouseX, mouseY);
            }
            componentOffset += (float) (component.getHeight() * openAnimation.getOutput());
        }

        height = componentOffset;

        RenderUtils.scaleEnd();
        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtils.isHovered2(x, y - 2, width, 22, mouseX, mouseY)) {
            switch (mouseButton) {
                case 0 -> {
                    dragging = true;
                    dragX = x - mouseX;
                    dragY = y - mouseY;
                }
                case 1 -> opened = !opened;
            }
        }
        if (opened && !MouseUtils.isHovered2(x, y - 2, width, 22, mouseX, mouseY)) {
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
