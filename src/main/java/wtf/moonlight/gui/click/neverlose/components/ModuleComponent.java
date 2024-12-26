package wtf.moonlight.gui.click.neverlose.components;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import wtf.moonlight.features.modules.Module;
import wtf.moonlight.features.values.Value;
import wtf.moonlight.features.values.impl.*;
import wtf.moonlight.gui.click.Component;
import wtf.moonlight.gui.click.neverlose.components.settings.*;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.utils.animations.Animation;
import wtf.moonlight.utils.animations.Direction;
import wtf.moonlight.utils.animations.impl.DecelerateAnimation;
import wtf.moonlight.utils.render.ColorUtils;
import wtf.moonlight.utils.render.MouseUtils;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

import static wtf.moonlight.gui.click.neverlose.NeverLose.*;

@Getter
public class ModuleComponent extends Component {
    private final Module module;
    @Setter
    private int scroll = 0;
    @Setter
    private boolean left = true;
    private final ObjectArrayList<Component> components = new ObjectArrayList<>();
    private final Animation enabled = new DecelerateAnimation(250,1);
    private final Animation hover = new DecelerateAnimation(250,1);
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
            if (setting instanceof ColorValue color) {
                components.add(new ColorPickerComponent(color));
            }
            if (setting instanceof TextValue string) {
                components.add(new StringComponent(string));
            }
        }
        enabled.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(Direction.BACKWARDS);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY) {
        float y = getY() + 6 + scroll;
        //name
        Fonts.interSemiBold.get(14).drawString(module.getName().replaceAll("(?<=[a-z])(?=[A-Z])", " ").toUpperCase(),getX() + 4,y,moduleTextRGB);
        //rect
        RoundedUtils.drawRoundOutline(getX(),y + 10,getWidth() / 2,getHeight(),4,0.1f,bgColor4,outlineColor);
        //enable
        enabled.setDirection(module.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
        hover.setDirection(MouseUtils.isHovered2(getX() + 154,y + 16,20,10,mouseX,mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        Fonts.interSemiBold.get(18).drawString("Enabled",getX() + 6,y + 18,textRGB);
        RoundedUtils.drawRound(getX() + 154,y + 16,20,10,4,new Color(ColorUtils.interpolateColor2(new Color(ColorUtils.interpolateColor2(boolBgColor,boolBgColor2,(float) enabled.getOutput())),
                new Color(ColorUtils.interpolateColor2(boolBgColor,boolBgColor2,(float) enabled.getOutput())).brighter().brighter(), (float) hover.getOutput())));
        RenderUtils.drawCircle(getX() + 159 + 10 * (float) enabled.getOutput(),y + 21,0,360,5,.1f,true, ColorUtils.interpolateColor2(new Color(ColorUtils.interpolateColor2(boolCircleColor2,boolCircleColor,(float) enabled.getOutput())),
                new Color(ColorUtils.interpolateColor2(boolCircleColor.darker().darker(),boolCircleColor,(float) enabled.getOutput())).brighter().brighter(), (float) hover.getOutput()));
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
        if (MouseUtils.isHovered2(getX() + 154,getY() + scroll + 22,20,10,mouseX,mouseY) && mouseButton == 0){
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
        return (int) (((getY() - INSTANCE.getNeverLose().getPosY()) + getHeight()) * 4);
    }
}
