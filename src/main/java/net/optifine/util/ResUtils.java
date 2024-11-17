package net.optifine.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;

public class ResUtils
{
    public static String[] collectFiles(String prefix, String suffix)
    {
        return collectFiles(new String[] {prefix}, new String[] {suffix});
    }

    public static String[] collectFiles(String[] prefixes, String[] suffixes)
    {
        Set<String> set = new LinkedHashSet();
        IResourcePack[] airesourcepack = Config.getResourcePacks();

        for (int i = 0; i < airesourcepack.length; ++i)
        {
            IResourcePack iresourcepack = airesourcepack[i];
            String[] astring = collectFiles(iresourcepack, prefixes, suffixes, null);
            set.addAll(Arrays.asList(astring));
        }

        String[] astring1 = set.toArray(new String[set.size()]);
        return astring1;
    }

    public static String[] collectFiles(IResourcePack rp, String prefix, String suffix, String[] defaultPaths)
    {
        return collectFiles(rp, new String[] {prefix}, new String[] {suffix}, defaultPaths);
    }

    public static String[] collectFiles(IResourcePack rp, String[] prefixes, String[] suffixes)
    {
        return collectFiles(rp, prefixes, suffixes, null);
    }

    public static String[] collectFiles(IResourcePack rp, String[] prefixes, String[] suffixes, String[] defaultPaths)
    {
        if (rp instanceof DefaultResourcePack)
        {
            return collectFilesFixed(rp, defaultPaths);
        }
        else if (!(rp instanceof AbstractResourcePack abstractresourcepack))
        {
            Config.warn("Unknown resource pack type: " + rp);
            return new String[0];
        }
        else
        {
            File file1 = abstractresourcepack.resourcePackFile;

            if (file1 == null)
            {
                return new String[0];
            }
            else if (file1.isDirectory())
            {
                return collectFilesFolder(file1, "", prefixes, suffixes);
            }
            else if (file1.isFile())
            {
                return collectFilesZIP(file1, prefixes, suffixes);
            }
            else
            {
                Config.warn("Unknown resource pack file: " + file1);
                return new String[0];
            }
        }
    }

    private static String[] collectFilesFixed(IResourcePack rp, String[] paths)
    {
        if (paths == null)
        {
            return new String[0];
        }
        else
        {
            List list = new ArrayList();

            for (int i = 0; i < paths.length; ++i)
            {
                String s = paths[i];
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (rp.resourceExists(resourcelocation))
                {
                    list.add(s);
                }
            }

            String[] astring = (String[]) list.toArray(new String[list.size()]);
            return astring;
        }
    }

    private static String[] collectFilesFolder(File tpFile, String basePath, String[] prefixes, String[] suffixes)
    {
        List list = new ArrayList();
        String s = "assets/minecraft/";
        File[] afile = tpFile.listFiles();

        if (afile == null)
        {
            return new String[0];
        }
        else
        {
            for (int i = 0; i < afile.length; ++i)
            {
                File file1 = afile[i];

                if (file1.isFile())
                {
                    String s3 = basePath + file1.getName();

                    if (s3.startsWith(s))
                    {
                        s3 = s3.substring(s.length());

                        if (StrUtils.startsWith(s3, prefixes) && StrUtils.endsWith(s3, suffixes))
                        {
                            list.add(s3);
                        }
                    }
                }
                else if (file1.isDirectory())
                {
                    String s1 = basePath + file1.getName() + "/";
                    String[] astring = collectFilesFolder(file1, s1, prefixes, suffixes);

                    Collections.addAll(list, astring);
                }
            }

            String[] astring1 = (String[]) list.toArray(new String[list.size()]);
            return astring1;
        }
    }

    private static String[] collectFilesZIP(File tpFile, String[] prefixes, String[] suffixes)
    {
        List list = new ArrayList();
        String s = "assets/minecraft/";

        try
        {
            ZipFile zipfile = new ZipFile(tpFile);
            Enumeration enumeration = zipfile.entries();

            while (enumeration.hasMoreElements())
            {
                ZipEntry zipentry = (ZipEntry)enumeration.nextElement();
                String s1 = zipentry.getName();

                if (s1.startsWith(s))
                {
                    s1 = s1.substring(s.length());

                    if (StrUtils.startsWith(s1, prefixes) && StrUtils.endsWith(s1, suffixes))
                    {
                        list.add(s1);
                    }
                }
            }

            zipfile.close();
            String[] astring = (String[]) list.toArray(new String[list.size()]);
            return astring;
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
            return new String[0];
        }
    }

    private static boolean isLowercase(String str)
    {
        return str.equals(str.toLowerCase(Locale.ROOT));
    }

    public static Properties readProperties(String path, String module)
    {
        ResourceLocation resourcelocation = new ResourceLocation(path);

        try
        {
            InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null)
            {
                return null;
            }
            else
            {
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                Config.dbg(module + ": Loading " + path);
                return properties;
            }
        }
        catch (FileNotFoundException var5)
        {
            return null;
        }
        catch (IOException var6)
        {
            Config.warn(module + ": Error reading " + path);
            return null;
        }
    }

    public static Properties readProperties(InputStream in, String module)
    {
        if (in == null)
        {
            return null;
        }
        else
        {
            try
            {
                Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                return properties;
            }
            catch (FileNotFoundException var3)
            {
                return null;
            }
            catch (IOException var4)
            {
                return null;
            }
        }
    }
}
