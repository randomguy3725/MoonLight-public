package wtf.moonlight.gui.click.menu.component;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.impl.visual.ClickGUI;
import wtf.moonlight.features.values.Value;
import wtf.moonlight.features.values.impl.*;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.click.menu.component.impl.*;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;
import java.util.ArrayList;

@Getter
public class ModuleComponent extends Component {
    private final Module module;
    @Setter
    private int scroll = 0;
    @Setter
    private boolean left = true;
    private final ArrayList<Component> components = new ArrayList<>();
    private final Animation enabled = new DecelerateAnimation(250, 1);
    private final Animation hover = new DecelerateAnimation(250, 1);

    public ModuleComponent(Module module) {
        this.module = module;
        for (Value setting : module.getValues()) {
            if (setting instanceof BoolValue bool) {
                components.add(new BooleanComponent(bool));
            }
            if (setting instanceof SliderValue slider) {
                components.add(new SliderComponent(slider));
            }
            if (setting instanceof ModeValue mode) {
                components.add(new ModeComponent(mode));
            }
            if (setting instanceof MultiBoolValue modes) {
                components.add(new MultiBoxComponent(modes));
            }
            /*if (setting instanceof TextValue string) {
                components.add(new StringComponent(string));
            }*/
            if (setting instanceof ColorValue color) {
                components.add(new ColorPickerComponent(color));
            }
        }
        enabled.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float y = getY() + 6 + scroll;
        enabled.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(MouseUtils.isHovered2(getX() + 20, y + 16, 20, 10, mouseX, mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtils.drawRound(getX() - 5, y + 10, (float) INSTANCE.getMenuGUI().getWidth() / 2 - 25 + 5, getHeight(), 4, new Color(25, 25, 25, 255));
        Fonts.interSemiBold.get(18).drawString(module.getName(), getX(), y + 16, -1);
        RoundedUtils.drawRound((getX() - 50) + (float) INSTANCE.getMenuGUI().getWidth() / 2, y + 16, 15, 7.5f, 4, ColorUtils.interpolateColorC(new Color(70, 70, 70, 255), INSTANCE.getModuleManager().getModule(ClickGUI.class).color.get(), (float) enabled.getOutput()));
        RenderUtils.drawCircle((float) ((getX() - 50) + (float) INSTANCE.getMenuGUI().getWidth() / 2 + (15 * enabled.getOutput())), y + 20, 0, 360, 5, 0.1f, true, new Color(255, 255, 255, 255).getRGB());
        float componentY = y + 22;
        ObjectArrayList<Component> filtered = components.stream()
                .filter(Component::isVisible)
                .collect(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::addAll);
        for (Component component : filtered) {
            component.setX(getX());
            component.setY(componentY);
            component.drawScreen(mouseX, mouseY);
            componentY += component.getHeight();
        }
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        float y = getY() + 6 + scroll;
        if (MouseUtils.isHovered2((getX() - 50) + (float) INSTANCE.getMenuGUI().getWidth() / 2, y + 16, 20, 10, mouseX, mouseY) && mouseButton == 0) {
            module.toggle();
        }
        for (Component component : components) {
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (Component component : components) {
            component.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (Component component : components) {
            component.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    public int getMaxScroll() {
        return (int) (((getY() - INSTANCE.getMenuGUI().getPosY()) + getHeight()) * 2);
    }
}
