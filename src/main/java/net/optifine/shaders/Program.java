package net.optifine.shaders;

import java.nio.IntBuffer;
import java.util.Arrays;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.RenderScale;

public class Program
{
    private final int index;
    private final String name;
    private final ProgramStage programStage;
    private final Program programBackup;
    private GlAlphaState alphaState;
    private GlBlendState blendState;
    private RenderScale renderScale;
    private final Boolean[] buffersFlip = new Boolean[8];
    private int id;
    private int ref;
    private String drawBufSettings;
    private IntBuffer drawBuffers;
    private IntBuffer drawBuffersBuffer;
    private int compositeMipmapSetting;
    private int countInstances;
    private final boolean[] toggleColorTextures = new boolean[8];

    public Program(int index, String name, ProgramStage programStage, Program programBackup)
    {
        this.index = index;
        this.name = name;
        this.programStage = programStage;
        this.programBackup = programBackup;
    }

    public Program(int index, String name, ProgramStage programStage, boolean ownBackup)
    {
        this.index = index;
        this.name = name;
        this.programStage = programStage;
        this.programBackup = ownBackup ? this : null;
    }

    public void resetProperties()
    {
        this.alphaState = null;
        this.blendState = null;
        this.renderScale = null;
        Arrays.fill(this.buffersFlip, null);
    }

    public void resetId()
    {
        this.id = 0;
        this.ref = 0;
    }

    public void resetConfiguration()
    {
        this.drawBufSettings = null;
        this.compositeMipmapSetting = 0;
        this.countInstances = 0;

        if (this.drawBuffersBuffer == null)
        {
            this.drawBuffersBuffer = Shaders.nextIntBuffer(8);
        }
    }

    public void copyFrom(Program p)
    {
        this.id = p.getId();
        this.alphaState = p.getAlphaState();
        this.blendState = p.getBlendState();
        this.renderScale = p.getRenderScale();
        System.arraycopy(p.getBuffersFlip(), 0, this.buffersFlip, 0, this.buffersFlip.length);
        this.drawBufSettings = p.getDrawBufSettings();
        this.drawBuffers = p.getDrawBuffers();
        this.compositeMipmapSetting = p.getCompositeMipmapSetting();
        this.countInstances = p.getCountInstances();
        System.arraycopy(p.getToggleColorTextures(), 0, this.toggleColorTextures, 0, this.toggleColorTextures.length);
    }

    public int getIndex()
    {
        return this.index;
    }

    public String getName()
    {
        return this.name;
    }

    public ProgramStage getProgramStage()
    {
        return this.programStage;
    }

    public Program getProgramBackup()
    {
        return this.programBackup;
    }

    public int getId()
    {
        return this.id;
    }

    public int getRef()
    {
        return this.ref;
    }

    public String getDrawBufSettings()
    {
        return this.drawBufSettings;
    }

    public IntBuffer getDrawBuffers()
    {
        return this.drawBuffers;
    }

    public IntBuffer getDrawBuffersBuffer()
    {
        return this.drawBuffersBuffer;
    }

    public int getCompositeMipmapSetting()
    {
        return this.compositeMipmapSetting;
    }

    public int getCountInstances()
    {
        return this.countInstances;
    }

    public GlAlphaState getAlphaState()
    {
        return this.alphaState;
    }

    public GlBlendState getBlendState()
    {
        return this.blendState;
    }

    public RenderScale getRenderScale()
    {
        return this.renderScale;
    }

    public Boolean[] getBuffersFlip()
    {
        return this.buffersFlip;
    }

    public boolean[] getToggleColorTextures()
    {
        return this.toggleColorTextures;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setRef(int ref)
    {
        this.ref = ref;
    }

    public void setDrawBufSettings(String drawBufSettings)
    {
        this.drawBufSettings = drawBufSettings;
    }

    public void setDrawBuffers(IntBuffer drawBuffers)
    {
        this.drawBuffers = drawBuffers;
    }

    public void setCompositeMipmapSetting(int compositeMipmapSetting)
    {
        this.compositeMipmapSetting = compositeMipmapSetting;
    }

    public void setCountInstances(int countInstances)
    {
        this.countInstances = countInstances;
    }

    public void setAlphaState(GlAlphaState alphaState)
    {
        this.alphaState = alphaState;
    }

    public void setBlendState(GlBlendState blendState)
    {
        this.blendState = blendState;
    }

    public void setRenderScale(RenderScale renderScale)
    {
        this.renderScale = renderScale;
    }

    public String getRealProgramName()
    {
        if (this.id == 0)
        {
            return "none";
        }
        else
        {
            Program program;

            for (program = this; program.getRef() != this.id; program = program.getProgramBackup())
            {
                if (program.getProgramBackup() == null || program.getProgramBackup() == program)
                {
                    return "unknown";
                }
            }

            return program.getName();
        }
    }

    public String toString()
    {
        return "name: " + this.name + ", id: " + this.id + ", ref: " + this.ref + ", real: " + this.getRealProgramName();
    }
}
