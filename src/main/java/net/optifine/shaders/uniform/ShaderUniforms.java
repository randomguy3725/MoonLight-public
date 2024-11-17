package net.optifine.shaders.uniform;

import java.util.ArrayList;
import java.util.List;

public class ShaderUniforms
{
    private final List<ShaderUniformBase> listUniforms = new ArrayList();

    public void setProgram(int program)
    {
        for (int i = 0; i < this.listUniforms.size(); ++i)
        {
            ShaderUniformBase shaderuniformbase = this.listUniforms.get(i);
            shaderuniformbase.setProgram(program);
        }
    }

    public void reset()
    {
        for (int i = 0; i < this.listUniforms.size(); ++i)
        {
            ShaderUniformBase shaderuniformbase = this.listUniforms.get(i);
            shaderuniformbase.reset();
        }
    }

    public ShaderUniform1i make1i(String name)
    {
        ShaderUniform1i shaderuniform1i = new ShaderUniform1i(name);
        this.listUniforms.add(shaderuniform1i);
        return shaderuniform1i;
    }

    public ShaderUniform2i make2i(String name)
    {
        ShaderUniform2i shaderuniform2i = new ShaderUniform2i(name);
        this.listUniforms.add(shaderuniform2i);
        return shaderuniform2i;
    }

    public ShaderUniform4i make4i(String name)
    {
        ShaderUniform4i shaderuniform4i = new ShaderUniform4i(name);
        this.listUniforms.add(shaderuniform4i);
        return shaderuniform4i;
    }

    public ShaderUniform1f make1f(String name)
    {
        ShaderUniform1f shaderuniform1f = new ShaderUniform1f(name);
        this.listUniforms.add(shaderuniform1f);
        return shaderuniform1f;
    }

    public ShaderUniform3f make3f(String name)
    {
        ShaderUniform3f shaderuniform3f = new ShaderUniform3f(name);
        this.listUniforms.add(shaderuniform3f);
        return shaderuniform3f;
    }

    public ShaderUniform4f make4f(String name)
    {
        ShaderUniform4f shaderuniform4f = new ShaderUniform4f(name);
        this.listUniforms.add(shaderuniform4f);
        return shaderuniform4f;
    }

    public ShaderUniformM4 makeM4(String name)
    {
        ShaderUniformM4 shaderuniformm4 = new ShaderUniformM4(name);
        this.listUniforms.add(shaderuniformm4);
        return shaderuniformm4;
    }
}
