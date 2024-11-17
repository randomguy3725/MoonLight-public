package wtf.moonlight.gui.click.menu.panel;

import lombok.Getter;
import lombok.Setter;
import org.lwjglx.input.Mouse;
import org.lwjgl.opengl.GL11;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.modules.ModuleCategory;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.click.IComponent;
import wtf.moonlight.gui.click.menu.component.ModuleComponent;
import wtf.moonlight.utils.InstanceAccess;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.animations.impl.SmoothStepAnimation;
import wtf.moonlight.utils.math.MathUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;

import java.util.ArrayList;

@Getter
public class Panel implements IComponent, InstanceAccess {
    private int posX, posY;
    private float maxScroll = Float.MAX_VALUE, rawScroll, scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);
    private final ModuleCategory category;
    @Setter
    private boolean selected;
    private final ArrayList<ModuleComponent> moduleComponents = new ArrayList<>();
    private final Animation animation = new DecelerateAnimation(250, 1);

    public Panel(ModuleCategory category) {
        this.category = category;
        for (Module module : INSTANCE.getModuleManager().getModules()) {
            if (module.getCategory().equals(this.category)) {
                moduleComponents.add(new ModuleComponent(module));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        //update coordinate
        posX = INSTANCE.getMenuGUI().getPosX();
        posY = INSTANCE.getMenuGUI().getPosY();
        //select anim
        animation.setDirection(selected ? Direction.FORWARDS : Direction.BACKWARDS);
        //render module components
        if (isSelected()) {
            //GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtils.scissor(getPosX(), getPosY() + 65, INSTANCE.getMenuGUI().getWidth(), INSTANCE.getMenuGUI().getHeight() - 65);

            float left = 0, right = 0;
            for (int i = 0; i < moduleComponents.size(); i++) {
                ModuleComponent module = moduleComponents.get(i);
                float componentOffset = getComponentOffset(i, left, right);

                module.drawScreen(mouseX, mouseY);

                double scroll = getScroll();
                module.setScroll((int) MathUtils.roundToHalf(scroll));
                onScroll(30, mouseX, mouseY);

                maxScroll = Math.max(0, moduleComponents.isEmpty() ? 0 : moduleComponents.get(moduleComponents.size() - 1).getMaxScroll());
                if ((i + 1) % 2 == 0) {
                    left += 25 + componentOffset;
                } else {
                    right += 25 + componentOffset;
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            //GL11.glPopAttrib();

        }
        IComponent.super.drawScreen(mouseX, mouseY);
    }

    private float getComponentOffset(int i, float left, float right) {
        ModuleComponent component = moduleComponents.get(i);
        component.setLeft((i + 1) % 2 != 0);
        component.setX(component.isLeft() ? posX + 25 : posX + 12.5f + (float) INSTANCE.getMenuGUI().getWidth() / 2);
        component.setHeight(20);
        component.setY(posY + 32 + component.getHeight() + ((i + 1) % 2 == 0 ? left : right));
        float componentOffset = 0;
        for (Component component2 : component.getComponents()) {
            if (component2.isVisible())
                componentOffset += component2.getHeight();
        }
        component.setHeight(component.getHeight() + componentOffset);
        return componentOffset;
    }

    public void onScroll(int ms, int mx, int my) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        if (MouseUtils.isHovered2(getPosX(), getPosY() + 65, INSTANCE.getMenuGUI().getWidth(), INSTANCE.getMenuGUI().getHeight() - 65, mx, my) && moduleComponents.stream().noneMatch(moduleComponent -> moduleComponent.getComponents().stream().anyMatch(component -> component.isHovered(mx, my)))) {
            rawScroll += (float) Mouse.getDWheel() * 30;
        }
        rawScroll = Math.max(Math.min(0, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.mouseClicked(mouseX, mouseY, mouseButton));
        }
        IComponent.super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.mouseReleased(mouseX, mouseY, state));
        }
        IComponent.super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (isSelected()) {
            moduleComponents.forEach(moduleComponent -> moduleComponent.keyTyped(typedChar, keyCode));
        }
        IComponent.super.keyTyped(typedChar, keyCode);
    }
}
