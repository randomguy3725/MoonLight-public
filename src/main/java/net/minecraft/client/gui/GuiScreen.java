package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import tv.twitch.chat.ChatUserInfo;
import wtf.moonlight.MoonLight;
import wtf.moonlight.events.impl.render.RenderGuiEvent;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');
    protected Minecraft mc;
    protected RenderItem itemRender;
    public int width;
    public int height;
    protected List<GuiButton> buttonList = Lists.newArrayList();
    protected List<GuiLabel> labelList = Lists.newArrayList();
    public boolean allowUserInput;
    protected FontRenderer fontRendererObj;
    public GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int touchValue;
    private URI clickedLinkURI;

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        MoonLight.INSTANCE.getEventManager().call(new RenderGuiEvent());
        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY);
        }

        for (int j = 0; j < this.labelList.size(); ++j)
        {
            this.labelList.get(j).drawLabel(this.mc, mouseX, mouseY);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
    }

    public static String getClipboardString()
    {
        try
        {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception var1)
        {
        }

        return "";
    }

    public static void setClipboardString(String copyText)
    {
        if (!StringUtils.isEmpty(copyText))
        {
            try
            {
                StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
            }
            catch (Exception var2)
            {
            }
        }
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            }
            else
            {
                list.set(i, EnumChatFormatting.GRAY + list.get(i));
            }
        }

        this.drawHoveringText(list, x, y);
    }

    protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY)
    {
        this.drawHoveringText(Collections.singletonList(tabName), mouseX, mouseY);
    }

    protected void drawHoveringText(List<String> textLines, int x, int y)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines)
            {
                int j = this.fontRendererObj.getStringWidth(s);

                if (j > i)
                {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1)
            {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width)
            {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height)
            {
                i2 = this.height - k - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            for (int k1 = 0; k1 < textLines.size(); ++k1)
            {
                String s1 = textLines.get(k1);
                this.fontRendererObj.drawStringWithShadow(s1, (float)l1, (float)i2, -1);

                if (k1 == 0)
                {
                    i2 += 2;
                }

                i2 += 10;
            }

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    protected void handleComponentHover(IChatComponent component, int x, int y)
    {
        if (component != null && component.getChatStyle().getChatHoverEvent() != null)
        {
            HoverEvent hoverevent = component.getChatStyle().getChatHoverEvent();

            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM)
            {
                ItemStack itemstack = null;

                try
                {
                    NBTBase nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                    if (nbtbase instanceof NBTTagCompound)
                    {
                        itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbtbase);
                    }
                }
                catch (NBTException var11)
                {
                }

                if (itemstack != null)
                {
                    this.renderToolTip(itemstack, x, y);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY)
            {
                if (this.mc.gameSettings.advancedItemTooltips)
                {
                    try
                    {
                        NBTBase nbtbase1 = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                        if (nbtbase1 instanceof NBTTagCompound nbttagcompound)
                        {
                            List<String> list1 = Lists.newArrayList();
                            list1.add(nbttagcompound.getString("name"));

                            if (nbttagcompound.hasKey("type", 8))
                            {
                                String s = nbttagcompound.getString("type");
                                list1.add("Type: " + s + " (" + EntityList.getIDFromString(s) + ")");
                            }

                            list1.add(nbttagcompound.getString("id"));
                            this.drawHoveringText(list1, x, y);
                        }
                        else
                        {
                            this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                        }
                    }
                    catch (NBTException var10)
                    {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                    }
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT)
            {
                this.drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.getValue().getFormattedText()), x, y);
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT)
            {
                StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());

                if (statbase != null)
                {
                    IChatComponent ichatcomponent = statbase.getStatName();
                    IChatComponent ichatcomponent1 = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"));
                    ichatcomponent1.getChatStyle().setItalic(Boolean.valueOf(true));
                    String s1 = statbase instanceof Achievement ? ((Achievement)statbase).getDescription() : null;
                    List<String> list = Lists.newArrayList(ichatcomponent.getFormattedText(), ichatcomponent1.getFormattedText());

                    if (s1 != null)
                    {
                        list.addAll(this.fontRendererObj.listFormattedStringToWidth(s1, 150));
                    }

                    this.drawHoveringText(list, x, y);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
                }
            }

            GlStateManager.disableLighting();
        }
    }

    protected void setText(String newChatText, boolean shouldOverwrite)
    {
    }

    protected boolean handleComponentClick(IChatComponent component)
    {
        if (component == null)
        {
            return false;
        }
        else
        {
            ClickEvent clickevent = component.getChatStyle().getChatClickEvent();

            if (isShiftKeyDown())
            {
                if (component.getChatStyle().getInsertion() != null)
                {
                    this.setText(component.getChatStyle().getInsertion(), false);
                }
            }
            else if (clickevent != null)
            {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL)
                {
                    if (!this.mc.gameSettings.chatLinks)
                    {
                        return false;
                    }

                    try
                    {
                        URI uri = new URI(clickevent.getValue());
                        String s = uri.getScheme();

                        if (s == null)
                        {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }

                        if (!PROTOCOLS.contains(s.toLowerCase()))
                        {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase());
                        }

                        if (this.mc.gameSettings.chatLinksPrompt)
                        {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickevent.getValue(), 31102009, false));
                        }
                        else
                        {
                            this.openWebLink(uri);
                        }
                    }
                    catch (URISyntaxException urisyntaxexception)
                    {
                        LOGGER.error("Can't open url for " + clickevent, urisyntaxexception);
                    }
                }
                else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE)
                {
                    URI uri1 = (new File(clickevent.getValue())).toURI();
                    this.openWebLink(uri1);
                }
                else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                {
                    this.setText(clickevent.getValue(), true);
                }
                else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
                {
                    this.sendChatMessage(clickevent.getValue(), false);
                }
                else if (clickevent.getAction() == ClickEvent.Action.TWITCH_USER_INFO)
                {
                    ChatUserInfo chatuserinfo = this.mc.getTwitchStream().func_152926_a(clickevent.getValue());

                    if (chatuserinfo != null)
                    {
                        this.mc.displayGuiScreen(new GuiTwitchUserMode(this.mc.getTwitchStream(), chatuserinfo));
                    }
                    else
                    {
                        LOGGER.error("Tried to handle twitch user but couldn't find them!");
                    }
                }
                else
                {
                    LOGGER.error("Don't know how to handle " + clickevent);
                }

                return true;
            }

            return false;
        }
    }

    public void sendChatMessage(String msg)
    {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat)
    {
        if (addToChat)
        {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }

        this.mc.thePlayer.sendChatMessage(msg);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            for (int i = 0; i < this.buttonList.size(); ++i)
            {
                GuiButton guibutton = this.buttonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                }
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRendererObj = mc.fontRendererObj;
        this.width = width;
        this.height = height;
        this.buttonList.clear();
        this.initGui();
    }

    public void setGuiSize(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    public void initGui()
    {
    }

    public void handleInput() throws IOException
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.handleKeyboardInput();
            }
        }
    }

    public void handleMouseInput() throws IOException
    {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0)
            {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        }
        else if (k != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    public void handleKeyboardInput() throws IOException
    {
        if (Keyboard.getEventKeyState())
        {
            this.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }

        this.mc.dispatchKeypresses();
    }

    public void updateScreen()
    {
    }

    public void onGuiClosed()
    {
    }

    public void drawDefaultBackground()
    {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int tint)
    {
        if (this.mc.theWorld != null)
        {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else
        {
            this.drawBackground(tint);
        }
    }

    public void drawBackground(int tint)
    {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, this.height, 0.0D).tex(0.0D, (float)this.height / 32.0F + (float)tint).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos(this.width, this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)tint).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos(this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, tint).color(64, 64, 64, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, tint).color(64, 64, 64, 255).endVertex();
        tessellator.draw();
    }

    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void confirmClicked(boolean result, int id)
    {
        if (id == 31102009)
        {
            if (result)
            {
                this.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(URI url)
    {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, url);
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    public static boolean isCtrlKeyDown()
    {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public static boolean isAltKeyDown()
    {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyComboCtrlX(int keyID)
    {
        return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID)
    {
        return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID)
    {
        return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID)
    {
        return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public void onResize(Minecraft mcIn, int w, int h)
    {
        this.setWorldAndResolution(mcIn, w, h);
    }
}
