package net.optifine.shaders.config;

import java.util.ArrayList;
import java.util.List;
import net.optifine.Lang;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.Shaders;

public class ShaderOptionProfile extends ShaderOption
{
    private ShaderProfile[] profiles = null;
    private ShaderOption[] options = null;
    private static final String NAME_PROFILE = "<profile>";
    private static final String VALUE_CUSTOM = "<custom>";

    public ShaderOptionProfile(ShaderProfile[] profiles, ShaderOption[] options)
    {
        super("<profile>", "", detectProfileName(profiles, options), getProfileNames(profiles), detectProfileName(profiles, options, true), null);
        this.profiles = profiles;
        this.options = options;
    }

    public void nextValue()
    {
        super.nextValue();

        if (this.getValue().equals("<custom>"))
        {
            super.nextValue();
        }

        this.applyProfileOptions();
    }

    public void updateProfile()
    {
        ShaderProfile shaderprofile = this.getProfile(this.getValue());

        if (!ShaderUtils.matchProfile(shaderprofile, this.options, false))
        {
            String s = detectProfileName(this.profiles, this.options);
            this.setValue(s);
        }
    }

    private void applyProfileOptions()
    {
        ShaderProfile shaderprofile = this.getProfile(this.getValue());

        if (shaderprofile != null)
        {
            String[] astring = shaderprofile.getOptions();

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                ShaderOption shaderoption = this.getOption(s);

                if (shaderoption != null)
                {
                    String s1 = shaderprofile.getValue(s);
                    shaderoption.setValue(s1);
                }
            }
        }
    }

    private ShaderOption getOption(String name)
    {
        for (int i = 0; i < this.options.length; ++i)
        {
            ShaderOption shaderoption = this.options[i];

            if (shaderoption.getName().equals(name))
            {
                return shaderoption;
            }
        }

        return null;
    }

    private ShaderProfile getProfile(String name)
    {
        for (int i = 0; i < this.profiles.length; ++i)
        {
            ShaderProfile shaderprofile = this.profiles[i];

            if (shaderprofile.getName().equals(name))
            {
                return shaderprofile;
            }
        }

        return null;
    }

    public String getNameText()
    {
        return Lang.get("of.shaders.profile");
    }

    public String getValueText(String val)
    {
        return val.equals("<custom>") ? Lang.get("of.general.custom", "<custom>") : Shaders.translate("profile." + val, val);
    }

    public String getValueColor(String val)
    {
        return val.equals("<custom>") ? "\u00a7c" : "\u00a7a";
    }

    public String getDescriptionText()
    {
        String s = Shaders.translate("profile.comment", null);

        if (s != null)
        {
            return s;
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer();

            for (int i = 0; i < this.profiles.length; ++i)
            {
                String s1 = this.profiles[i].getName();

                if (s1 != null)
                {
                    String s2 = Shaders.translate("profile." + s1 + ".comment", null);

                    if (s2 != null)
                    {
                        stringbuffer.append(s2);

                        if (!s2.endsWith(". "))
                        {
                            stringbuffer.append(". ");
                        }
                    }
                }
            }

            return stringbuffer.toString();
        }
    }

    private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts)
    {
        return detectProfileName(profs, opts, false);
    }

    private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts, boolean def)
    {
        ShaderProfile shaderprofile = ShaderUtils.detectProfile(profs, opts, def);
        return shaderprofile == null ? "<custom>" : shaderprofile.getName();
    }

    private static String[] getProfileNames(ShaderProfile[] profs)
    {
        List<String> list = new ArrayList();

        for (int i = 0; i < profs.length; ++i)
        {
            ShaderProfile shaderprofile = profs[i];
            list.add(shaderprofile.getName());
        }

        list.add("<custom>");
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}
