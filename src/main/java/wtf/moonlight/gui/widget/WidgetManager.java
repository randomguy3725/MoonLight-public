package wtf.moonlight.gui.widget;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wtf.moonlight.Moonlight;
import wtf.moonlight.events.annotations.EventTarget;
import wtf.moonlight.events.impl.render.ChatGUIEvent;
import wtf.moonlight.events.impl.render.Render2DEvent;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.features.modules.impl.visual.Interface;
import wtf.moonlight.features.values.impl.BoolValue;
import wtf.moonlight.gui.widget.impl.*;
import wtf.moonlight.utils.InstanceAccess;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WidgetManager implements InstanceAccess {
    public List<Widget> widgetList = new ArrayList<>();

    public WidgetManager() {
        INSTANCE.getEventManager().register(this);
        register(new TargetHUDWidget());
        register(new InventoryWidget());
        register(new PotionHUDWidget());
        register(new SessionInfoWidget());
        register(new PointerWidget());
        register(new KeyBindWidget());
    }

    public boolean loaded;

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        for (Widget widget : widgetList) {
            if (widget.shouldRender()) {
                widget.updatePos();
                widget.render();
            }
        }
    }

    @EventTarget
    public void onShader2D(Shader2DEvent event) {
        for (Widget widget : widgetList) {
            if (widget.shouldRender()) {
                widget.onShader(event);
            }
        }
    }

    @EventTarget
    public void onChatGUI(ChatGUIEvent event) {
        Widget draggingWidget = null;
        for (Widget widget : widgetList) {
            if (widget.shouldRender() && widget.dragging) {
                draggingWidget = widget;
                break;
            }
        }

        for (Widget widget : widgetList) {
            if (widget.shouldRender()) {
                widget.onChatGUI(event.mouseX, event.mouseY, (draggingWidget == null || draggingWidget == widget));
                if (widget.dragging) draggingWidget = widget;
            }
        }
    }

    private void register(Widget widget) {
        this.widgetList.add(widget);
    }

    public Widget get(String name) {
        for (Widget widget : widgetList) {
            if (widget.name.equalsIgnoreCase(name)) {
                return widget;
            }
        }
        return null;
    }

    public <module extends Widget> module get(Class<? extends module> moduleClass) {
        Iterator<Widget> var2 = this.widgetList.iterator();
        Widget module;
        do {
            if (!var2.hasNext()) {
                return null;
            }
            module = var2.next();
        } while (module.getClass() != moduleClass);

        return (module) module;
    }
}