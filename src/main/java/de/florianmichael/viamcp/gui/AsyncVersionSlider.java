/*
 * This file is part of ViaMCP - https://github.com/FlorianMichael/ViaMCP
 * Copyright (C) 2020-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viamcp.gui;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

import java.util.Collections;
import java.util.List;

public class AsyncVersionSlider extends GuiButton {
    private float dragValue = (float) (ViaLoadingBase.getProtocols().size() - ViaLoadingBase.getInstance().getTargetVersion().getIndex()) / ViaLoadingBase.getProtocols().size();

    private final List<ProtocolVersion> values;
    private float sliderValue;
    public boolean dragging;

    public AsyncVersionSlider(int buttonId, int x, int y , int widthIn, int heightIn)
    {
        super(buttonId, x, y, Math.max(widthIn, 110), heightIn, "");
        this.values = ViaLoadingBase.getProtocols();
        Collections.reverse(values);
        this.sliderValue = dragValue;
        this.displayString = values.get((int) (this.sliderValue * (values.size() - 1))).getName();
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        super.drawButton(mc, mouseX, mouseY);
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            if (this.dragging)
            {
                this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
                this.dragValue = sliderValue;
                this.displayString = values.get((int) (this.sliderValue * (values.size() - 1))).getName();
                ViaLoadingBase.getInstance().reload(values.get((int) (this.sliderValue * (values.size() - 1))));
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
        {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            this.dragValue = sliderValue;
            this.displayString = values.get((int) (this.sliderValue * (values.size() - 1))).getName();
            ViaLoadingBase.getInstance().reload(values.get((int) (this.sliderValue * (values.size() - 1))));
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY)
    {
        this.dragging = false;
    }

    public void setVersion(int protocol)
    {
        this.dragValue = (float) (ViaLoadingBase.getProtocols().size() - ViaLoadingBase.fromProtocolId(protocol).getIndex()) / ViaLoadingBase.getProtocols().size();
        this.sliderValue = this.dragValue;
        this.displayString = values.get((int) (this.sliderValue * (values.size() - 1))).getName();
    }
}
