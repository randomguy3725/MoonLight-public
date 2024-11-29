package wtf.moonlight.gui.widget.impl;

import org.lwjgl.opengl.GL11;
import wtf.moonlight.events.impl.render.Shader2DEvent;
import wtf.moonlight.gui.font.Fonts;
import wtf.moonlight.gui.widget.Widget;
import wtf.moonlight.utils.render.RenderUtils;
import wtf.moonlight.utils.render.RoundedUtils;

import java.awt.*;

public class SessionInfoWidget extends Widget {
    public SessionInfoWidget() {
        super("Session Info");
        this.x = 0.1f;
        this.y = 0.2f;
    }

    @Override
    public void onShader(Shader2DEvent event) {
        switch (setting.sessionInfoMode.get()){
            case "Rise":
                if(event.getShaderType() == Shader2DEvent.ShaderType.GLOW) {
                    RoundedUtils.drawRound(renderX, renderY, this.width, this.height, 11, new Color(setting.color(0)));
                }
                break;
        }
    }

    @Override
    public void render() {

        switch (setting.sessionInfoMode.get()){
            case "Default":
                this.width = 150;
                this.height = 100;
                RoundedUtils.drawRound(renderX,renderY,width,height,10,new Color(setting.bgColor()));


                Fonts.interRegular.get(14).drawStringWithShadow("Session Time: " + RenderUtils.sessionTime(),renderX + 7.5f,renderY + 10,-1);
                Fonts.interRegular.get(14).drawStringWithShadow("User: " + mc.thePlayer.getName(),renderX + 7.5f,renderY + 10 + Fonts.interRegular.get(13).getHeight(),-1);

                float xOffset = 10;

                for (int i = 0; i < setting.match; i++) {
                    if (setting.matchKilled > 0 || setting.prevMatchKilled > 0) {
                        int prevKilled = setting.prevMatchKilled;
                        int barHeight = i == 5 ? setting.matchKilled : prevKilled;

                        GL11.glPushMatrix();
                        RenderUtils.scissor(renderX + xOffset, (renderY + height) - (2 * 10), 5, (2 * 10));
                        GL11.glEnable(GL11.GL_SCISSOR_TEST);
                        Color color = i == 5 ? new Color(setting.color()) : new Color(setting.color()).darker().darker();
                        RoundedUtils.drawRound(renderX + xOffset, (renderY + height) - (barHeight * 10) + 2, 5, (barHeight * 10) + 2, 4, color);
                        GL11.glDisable(GL11.GL_SCISSOR_TEST);
                        GL11.glPopMatrix();
                    } else {
                        Fonts.nursultan.get(15).drawString("U",renderX + xOffset,renderY + height - Fonts.nursultan.get(15).getHeight(),-1);
                    }
                    xOffset += 10;
                }

                Fonts.interRegular.get(15).drawStringWithShadow("Current game: ",renderX + xOffset, (renderY + height) - (setting.matchKilled * 10) - Fonts.interRegular.get(15).getHeight() * 2 + (float) Fonts.interRegular.get(13).getHeight() / 2,-1);
                Fonts.interRegular.get(13).drawStringWithShadow(setting.matchKilled + " kills",renderX + xOffset, (renderY + height) - (setting.matchKilled * 10) - Fonts.interRegular.get(15).getHeight() + (float) Fonts.interRegular.get(13).getHeight() / 2 ,-1);
                break;

            case "Rise":
                this.width = 140;
                this.height = 55;
                double padding = 8;
                RoundedUtils.drawRoundOutline(renderX, renderY, this.width, this.height, 11,0.5f, new Color(0,0,0,100), new Color(setting.color(0)));

                Fonts.interRegular.get(24).drawCenteredString("Session Stats", renderX + this.width / 2f, renderY + padding, setting.color(0));
                Fonts.interRegular.get(18).drawCenteredString(RenderUtils.sessionTime(), renderX + this.width / 2f, renderY + padding + 19, new Color(255, 255, 255, 200).getRGB());
                Fonts.interRegular.get(18).drawCenteredString("kills: " + setting.killed, renderX + 35, renderY + padding + 32, new Color(255, 255, 255, 200).getRGB());
                Fonts.interRegular.get(18).drawCenteredString("wins: " + setting.won, renderX + 105, renderY + padding + 32, new Color(255, 255, 255, 200).getRGB());
                break;
        }
    }

    @Override
    public boolean shouldRender() {
        return setting.isEnabled() && setting.elements.isEnabled("Session Info") && !setting.sessionInfoMode.is("Exhi");
    }
}
