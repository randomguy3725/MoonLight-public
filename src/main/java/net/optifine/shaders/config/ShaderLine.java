package net.optifine.shaders.config;

import net.minecraft.src.Config;
import net.optifine.util.StrUtils;
import org.lwjglx.util.vector.Vector4f;

public class ShaderLine
{
    private final int type;
    private final String name;
    private final String value;
    private final String line;
    public static final int TYPE_UNIFORM = 1;
    public static final int TYPE_ATTRIBUTE = 2;
    public static final int TYPE_CONST_INT = 3;
    public static final int TYPE_CONST_FLOAT = 4;
    public static final int TYPE_CONST_BOOL = 5;
    public static final int TYPE_PROPERTY = 6;
    public static final int TYPE_EXTENSION = 7;
    public static final int TYPE_CONST_VEC4 = 8;

    public ShaderLine(int type, String name, String value, String line)
    {
        this.type = type;
        this.name = name;
        this.value = value;
        this.line = line;
    }

    public int getType()
    {
        return this.type;
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    public boolean isUniform()
    {
        return this.type == 1;
    }

    public boolean isUniform(String name)
    {
        return this.isUniform() && name.equals(this.name);
    }

    public boolean isAttribute()
    {
        return this.type == 2;
    }

    public boolean isAttribute(String name)
    {
        return this.isAttribute() && name.equals(this.name);
    }

    public boolean isProperty()
    {
        return this.type == 6;
    }

    public boolean isConstInt()
    {
        return this.type == 3;
    }

    public boolean isConstFloat()
    {
        return this.type == 4;
    }

    public boolean isConstBool()
    {
        return this.type == 5;
    }

    public boolean isExtension()
    {
        return this.type == 7;
    }

    public boolean isConstVec4()
    {
        return this.type == 8;
    }

    public boolean isProperty(String name)
    {
        return this.isProperty() && name.equals(this.name);
    }

    public boolean isProperty(String name, String value)
    {
        return this.isProperty(name) && value.equals(this.value);
    }

    public boolean isConstInt(String name)
    {
        return this.isConstInt() && name.equals(this.name);
    }

    public boolean isConstIntSuffix(String suffix)
    {
        return this.isConstInt() && this.name.endsWith(suffix);
    }

    public boolean isConstFloat(String name)
    {
        return this.isConstFloat() && name.equals(this.name);
    }

    public boolean isConstBool(String name)
    {
        return this.isConstBool() && name.equals(this.name);
    }

    public boolean isExtension(String name)
    {
        return this.isExtension() && name.equals(this.name);
    }

    public boolean isConstBoolSuffix(String suffix)
    {
        return this.isConstBool() && this.name.endsWith(suffix);
    }

    public boolean isConstBoolSuffix(String suffix, boolean val)
    {
        return this.isConstBoolSuffix(suffix) && this.getValueBool() == val;
    }

    public boolean isConstBool(String name1, String name2)
    {
        return this.isConstBool(name1) || this.isConstBool(name2);
    }

    public boolean isConstBool(String name1, String name2, String name3)
    {
        return this.isConstBool(name1) || this.isConstBool(name2) || this.isConstBool(name3);
    }

    public boolean isConstBool(String name, boolean val)
    {
        return this.isConstBool(name) && this.getValueBool() == val;
    }

    public boolean isConstBool(String name1, String name2, boolean val)
    {
        return this.isConstBool(name1, name2) && this.getValueBool() == val;
    }

    public boolean isConstBool(String name1, String name2, String name3, boolean val)
    {
        return this.isConstBool(name1, name2, name3) && this.getValueBool() == val;
    }

    public boolean isConstVec4Suffix(String suffix)
    {
        return this.isConstVec4() && this.name.endsWith(suffix);
    }

    public int getValueInt()
    {
        try
        {
            return Integer.parseInt(this.value);
        }
        catch (NumberFormatException var2)
        {
            throw new NumberFormatException("Invalid integer: " + this.value + ", line: " + this.line);
        }
    }

    public float getValueFloat()
    {
        try
        {
            return Float.parseFloat(this.value);
        }
        catch (NumberFormatException var2)
        {
            throw new NumberFormatException("Invalid float: " + this.value + ", line: " + this.line);
        }
    }

    public Vector4f getValueVec4()
    {
        if (this.value == null)
        {
            return null;
        }
        else
        {
            String s = this.value.trim();
            s = StrUtils.removePrefix(s, "vec4");
            s = StrUtils.trim(s, " ()");
            String[] astring = Config.tokenize(s, ", ");

            if (astring.length != 4)
            {
                return null;
            }
            else
            {
                float[] afloat = new float[4];

                for (int i = 0; i < astring.length; ++i)
                {
                    String s1 = astring[i];
                    s1 = StrUtils.removeSuffix(s1, new String[] {"F", "f"});
                    float f = Config.parseFloat(s1, Float.MAX_VALUE);

                    if (f == Float.MAX_VALUE)
                    {
                        return null;
                    }

                    afloat[i] = f;
                }

                return new Vector4f(afloat[0], afloat[1], afloat[2], afloat[3]);
            }
        }
    }

    public boolean getValueBool()
    {
        String s = this.value.toLowerCase();

        if (!s.equals("true") && !s.equals("false"))
        {
            throw new RuntimeException("Invalid boolean: " + this.value + ", line: " + this.line);
        }
        else
        {
            return Boolean.valueOf(this.value).booleanValue();
        }
    }
}
