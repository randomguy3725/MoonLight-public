package wtf.moonlight.gui.click.dropdown2.component;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.Value;
import wtf.moonlight.features.values.impl.*;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.click.IComponent;
import wtf.moonlight.gui.click.dropdown2.component.impl.BooleanComponent;
import wtf.moonlight.gui.click.dropdown2.component.impl.SliderComponent;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class ModuleComponent implements IComponent {
    private float x, y, width, height = 22;
    private final Module module;
    private final CopyOnWriteArrayList<Component> settings = new CopyOnWriteArrayList<>();
    private boolean opened;
    private double arrowLength = 3;
    private final DecelerateAnimation openAnimation = new DecelerateAnimation(250, 1);
    private final DecelerateAnimation toggleAnimation = new DecelerateAnimation(300, 1);
    private final DecelerateAnimation hoverAnimation = new DecelerateAnimation(200, 1);

    public ModuleComponent(Module module) {
        this.module = module;
        openAnimation.setDirection(Direction.BACKWARDS);
        toggleAnimation.setDirection(Direction.BACKWARDS);
        for (Value value : module.getValues()) {
            if (value instanceof BoolValue boolValue) {
                settings.add(new BooleanComponent(boolValue));
            }
            if (value instanceof SliderValue sliderValue) {
                settings.add(new SliderComponent(sliderValue));
            }
            /*if (value instanceof ModeValue modeValue) {
                settings.add(new ModeComponent(modeValue));
            }
            if (value instanceof ColorValue colorValue) {
                settings.add(new ColorPickerComponent(colorValue));
            }
            if (value instanceof MultiBoolValue multiBoolValue) {
                settings.add(new MultiBooleanComponent(multiBoolValue));
            }*/
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float yOffset = 22;
        openAnimation.setDirection(opened ? Direction.FORWARDS : Direction.BACKWARDS);
        toggleAnimation.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);

        RenderUtils.drawRect(x, y, width, yOffset, ColorUtils.interpolateColor2(new Color(20, 20, 20, 120), INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get(), (float) toggleAnimation.getOutput()));

        Fonts.psRegular.get(15).drawString(module.getName(), x + 5, y + (yOffset - 4) / 2, ColorUtils.interpolateColor2(Color.GRAY, Color.WHITE, (float) toggleAnimation.getOutput()));

        if (!settings.isEmpty()) {
            double val = Minecraft.getDebugFPS() / 8.3;
            if (opened && arrowLength > -3) {
                arrowLength -= 3 / val;
            } else if (!opened && arrowLength < 3) {
                arrowLength += 3 / val;
            }
            RenderUtils.drawArrow(x + width - 10, y + (yOffset - arrowLength) / 2, 3, -1, arrowLength);
        }

        for (Component component : settings) {
            if (!component.isVisible()) continue;
            component.setX(x);
            component.setY((float) (y + yOffset * openAnimation.getOutput()));
            component.setWidth(width);
            if (openAnimation.getOutput() > 0.7f) {
                component.drawBackground(new Color(20, 20, 20, 120));
                component.drawScreen(mouseX, mouseY);
            }
            yOffset += (float) (component.getHeight() * openAnimation.getOutput());
            this.height = yOffset;
        }

        IComponent.super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            switch (mouseButton) {
                case 0 -> module.toggle();
                case 1 -> opened = !opened;
            }
        }
        if (opened && !isHovered(mouseX, mouseY)) {
            settings.forEach(setting -> setting.mouseClicked(mouseX, mouseY, mouseButton));
        }
        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (opened && !isHovered(mouseX, mouseY)) {
            settings.forEach(setting -> setting.mouseReleased(mouseX, mouseY, state));
        }
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (opened) {
            settings.forEach(setting -> setting.keyTyped(typedChar, keyCode));
        }
        IComponent.super.keyTyped(typedChar, keyCode);
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return MouseUtils.isHovered2(x + 2, y, width - 2, 17, mouseX, mouseY);
    }
}
