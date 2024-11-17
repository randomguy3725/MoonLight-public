package net.optifine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.IParserInt;
import net.optifine.config.NbtTagValue;
import net.optifine.config.ParserEnchantmentId;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.reflect.Reflector;
import net.optifine.render.Blender;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties
{
    public String name = null;
    public String basePath = null;
    public int type = 1;
    public int[] items = null;
    public String texture = null;
    public Map<String, String> mapTextures = null;
    public String model = null;
    public Map<String, String> mapModels = null;
    public RangeListInt damage = null;
    public boolean damagePercent = false;
    public int damageMask = 0;
    public RangeListInt stackSize = null;
    public RangeListInt enchantmentIds = null;
    public RangeListInt enchantmentLevels = null;
    public NbtTagValue[] nbtTagValues = null;
    public int hand = 0;
    public int blend = 1;
    public float speed = 0.0F;
    public float rotation = 0.0F;
    public int layer = 0;
    public float duration = 1.0F;
    public int weight = 0;
    public ResourceLocation textureLocation = null;
    public Map mapTextureLocations = null;
    public TextureAtlasSprite sprite = null;
    public Map mapSprites = null;
    public IBakedModel bakedModelTexture = null;
    public Map<String, IBakedModel> mapBakedModelsTexture = null;
    public IBakedModel bakedModelFull = null;
    public Map<String, IBakedModel> mapBakedModelsFull = null;
    private int textureWidth = 0;
    private int textureHeight = 0;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_ENCHANTMENT = 2;
    public static final int TYPE_ARMOR = 3;
    public static final int HAND_ANY = 0;
    public static final int HAND_MAIN = 1;
    public static final int HAND_OFF = 2;
    public static final String INVENTORY = "inventory";

    public CustomItemProperties(Properties props, String path)
    {
        this.name = parseName(path);
        this.basePath = parseBasePath(path);
        this.type = this.parseType(props.getProperty("type"));
        this.items = this.parseItems(props.getProperty("items"), props.getProperty("matchItems"));
        this.mapModels = parseModels(props, this.basePath);
        this.model = parseModel(props.getProperty("model"), path, this.basePath, this.type, this.mapModels);
        this.mapTextures = parseTextures(props, this.basePath);
        boolean flag = this.mapModels == null && this.model == null;
        this.texture = parseTexture(props.getProperty("texture"), props.getProperty("tile"), props.getProperty("source"), path, this.basePath, this.type, this.mapTextures, flag);
        String s = props.getProperty("damage");

        if (s != null)
        {
            this.damagePercent = s.contains("%");
            s = s.replace("%", "");
            this.damage = this.parseRangeListInt(s);
            this.damageMask = this.parseInt(props.getProperty("damageMask"), 0);
        }

        this.stackSize = this.parseRangeListInt(props.getProperty("stackSize"));
        this.enchantmentIds = this.parseRangeListInt(props.getProperty("enchantmentIDs"), new ParserEnchantmentId());
        this.enchantmentLevels = this.parseRangeListInt(props.getProperty("enchantmentLevels"));
        this.nbtTagValues = this.parseNbtTagValues(props);
        this.hand = this.parseHand(props.getProperty("hand"));
        this.blend = Blender.parseBlend(props.getProperty("blend"));
        this.speed = this.parseFloat(props.getProperty("speed"), 0.0F);
        this.rotation = this.parseFloat(props.getProperty("rotation"), 0.0F);
        this.layer = this.parseInt(props.getProperty("layer"), 0);
        this.weight = this.parseInt(props.getProperty("weight"), 0);
        this.duration = this.parseFloat(props.getProperty("duration"), 1.0F);
    }

    private static String parseName(String path)
    {
        String s = path;
        int i = path.lastIndexOf(47);

        if (i >= 0)
        {
            s = path.substring(i + 1);
        }

        int j = s.lastIndexOf(46);

        if (j >= 0)
        {
            s = s.substring(0, j);
        }

        return s;
    }

    private static String parseBasePath(String path)
    {
        int i = path.lastIndexOf(47);
        return i < 0 ? "" : path.substring(0, i);
    }

    private int parseType(String str)
    {
        if (str == null)
        {
            return 1;
        }
        else if (str.equals("item"))
        {
            return 1;
        }
        else if (str.equals("enchantment"))
        {
            return 2;
        }
        else if (str.equals("armor"))
        {
            return 3;
        }
        else
        {
            Config.warn("Unknown method: " + str);
            return 0;
        }
    }

    private int[] parseItems(String str, String str2)
    {
        if (str == null)
        {
            str = str2;
        }

        if (str == null)
        {
            return null;
        }
        else
        {
            str = str.trim();
            Set set = new TreeSet();
            String[] astring = Config.tokenize(str, " ");
            label45:

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                int j = Config.parseInt(s, -1);

                if (j >= 0)
                {
                    set.add(Integer.valueOf(j));
                }
                else
                {
                    if (s.contains("-"))
                    {
                        String[] astring1 = Config.tokenize(s, "-");

                        if (astring1.length == 2)
                        {
                            int k = Config.parseInt(astring1[0], -1);
                            int l = Config.parseInt(astring1[1], -1);

                            if (k >= 0 && l >= 0)
                            {
                                int i1 = Math.min(k, l);
                                int j1 = Math.max(k, l);
                                int k1 = i1;

                                while (true)
                                {
                                    if (k1 > j1)
                                    {
                                        continue label45;
                                    }

                                    set.add(Integer.valueOf(k1));
                                    ++k1;
                                }
                            }
                        }
                    }

                    Item item = Item.getByNameOrId(s);

                    if (item == null)
                    {
                        Config.warn("Item not found: " + s);
                    }
                    else
                    {
                        int i2 = Item.getIdFromItem(item);

                        if (i2 <= 0)
                        {
                            Config.warn("Item not found: " + s);
                        }
                        else
                        {
                            set.add(Integer.valueOf(i2));
                        }
                    }
                }
            }

            Integer[] ainteger = (Integer[]) set.toArray(new Integer[set.size()]);
            int[] aint = new int[ainteger.length];

            for (int l1 = 0; l1 < aint.length; ++l1)
            {
                aint[l1] = ainteger[l1].intValue();
            }

            return aint;
        }
    }

    private static String parseTexture(String texStr, String texStr2, String texStr3, String path, String basePath, int type, Map<String, String> mapTexs, boolean textureFromPath)
    {
        if (texStr == null)
        {
            texStr = texStr2;
        }

        if (texStr == null)
        {
            texStr = texStr3;
        }

        if (texStr != null)
        {
            String s2 = ".png";

            if (texStr.endsWith(s2))
            {
                texStr = texStr.substring(0, texStr.length() - s2.length());
            }

            texStr = fixTextureName(texStr, basePath);
            return texStr;
        }
        else if (type == 3)
        {
            return null;
        }
        else
        {
            if (mapTexs != null)
            {
                String s = mapTexs.get("texture.bow_standby");

                if (s != null)
                {
                    return s;
                }
            }

            if (!textureFromPath)
            {
                return null;
            }
            else
            {
                String s1 = path;
                int i = path.lastIndexOf(47);

                if (i >= 0)
                {
                    s1 = path.substring(i + 1);
                }

                int j = s1.lastIndexOf(46);

                if (j >= 0)
                {
                    s1 = s1.substring(0, j);
                }

                s1 = fixTextureName(s1, basePath);
                return s1;
            }
        }
    }

    private static Map parseTextures(Properties props, String basePath)
    {
        String s = "texture.";
        Map map = getMatchingProperties(props, s);

        if (map.size() <= 0)
        {
            return null;
        }
        else
        {
            Set set = map.keySet();
            Map map1 = new LinkedHashMap();

            for (Object o : set)
            {
                String s1 = (String) o;
                String s2 = (String)map.get(s1);
                s2 = fixTextureName(s2, basePath);
                map1.put(s1, s2);
            }

            return map1;
        }
    }

    private static String fixTextureName(String iconName, String basePath)
    {
        iconName = TextureUtils.fixResourcePath(iconName, basePath);

        if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("mcpatcher/"))
        {
            iconName = basePath + "/" + iconName;
        }

        if (iconName.endsWith(".png"))
        {
            iconName = iconName.substring(0, iconName.length() - 4);
        }

        if (iconName.startsWith("/"))
        {
            iconName = iconName.substring(1);
        }

        return iconName;
    }

    private static String parseModel(String modelStr, String path, String basePath, int type, Map<String, String> mapModelNames)
    {
        if (modelStr != null)
        {
            String s1 = ".json";

            if (modelStr.endsWith(s1))
            {
                modelStr = modelStr.substring(0, modelStr.length() - s1.length());
            }

            modelStr = fixModelName(modelStr, basePath);
            return modelStr;
        }
        else if (type == 3)
        {
            return null;
        }
        else
        {
            if (mapModelNames != null)
            {
                String s = mapModelNames.get("model.bow_standby");

                if (s != null)
                {
                    return s;
                }
            }

            return modelStr;
        }
    }

    private static Map parseModels(Properties props, String basePath)
    {
        String s = "model.";
        Map map = getMatchingProperties(props, s);

        if (map.size() <= 0)
        {
            return null;
        }
        else
        {
            Set set = map.keySet();
            Map map1 = new LinkedHashMap();

            for (Object o : set)
            {
                String s1 = (String) o;
                String s2 = (String)map.get(s1);
                s2 = fixModelName(s2, basePath);
                map1.put(s1, s2);
            }

            return map1;
        }
    }

    private static String fixModelName(String modelName, String basePath)
    {
        modelName = TextureUtils.fixResourcePath(modelName, basePath);
        boolean flag = modelName.startsWith("block/") || modelName.startsWith("item/");

        if (!modelName.startsWith(basePath) && !flag && !modelName.startsWith("mcpatcher/"))
        {
            modelName = basePath + "/" + modelName;
        }

        String s = ".json";

        if (modelName.endsWith(s))
        {
            modelName = modelName.substring(0, modelName.length() - s.length());
        }

        if (modelName.startsWith("/"))
        {
            modelName = modelName.substring(1);
        }

        return modelName;
    }

    private int parseInt(String str, int defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            str = str.trim();
            int i = Config.parseInt(str, Integer.MIN_VALUE);

            if (i == Integer.MIN_VALUE)
            {
                Config.warn("Invalid integer: " + str);
                return defVal;
            }
            else
            {
                return i;
            }
        }
    }

    private float parseFloat(String str, float defVal)
    {
        if (str == null)
        {
            return defVal;
        }
        else
        {
            str = str.trim();
            float f = Config.parseFloat(str, Float.MIN_VALUE);

            if (f == Float.MIN_VALUE)
            {
                Config.warn("Invalid float: " + str);
                return defVal;
            }
            else
            {
                return f;
            }
        }
    }

    private RangeListInt parseRangeListInt(String str)
    {
        return this.parseRangeListInt(str, null);
    }

    private RangeListInt parseRangeListInt(String str, IParserInt parser)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            String[] astring = Config.tokenize(str, " ");
            RangeListInt rangelistint = new RangeListInt();

            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];

                if (parser != null)
                {
                    int j = parser.parse(s, Integer.MIN_VALUE);

                    if (j != Integer.MIN_VALUE)
                    {
                        rangelistint.addRange(new RangeInt(j, j));
                        continue;
                    }
                }

                RangeInt rangeint = this.parseRangeInt(s);

                if (rangeint == null)
                {
                    Config.warn("Invalid range list: " + str);
                    return null;
                }

                rangelistint.addRange(rangeint);
            }

            return rangelistint;
        }
    }

    private RangeInt parseRangeInt(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            str = str.trim();
            int i = str.length() - str.replace("-", "").length();

            if (i > 1)
            {
                Config.warn("Invalid range: " + str);
                return null;
            }
            else
            {
                String[] astring = Config.tokenize(str, "- ");
                int[] aint = new int[astring.length];

                for (int j = 0; j < astring.length; ++j)
                {
                    String s = astring[j];
                    int k = Config.parseInt(s, -1);

                    if (k < 0)
                    {
                        Config.warn("Invalid range: " + str);
                        return null;
                    }

                    aint[j] = k;
                }

                if (aint.length == 1)
                {
                    int i1 = aint[0];

                    if (str.startsWith("-"))
                    {
                        return new RangeInt(0, i1);
                    }
                    else if (str.endsWith("-"))
                    {
                        return new RangeInt(i1, 65535);
                    }
                    else
                    {
                        return new RangeInt(i1, i1);
                    }
                }
                else if (aint.length == 2)
                {
                    int l = Math.min(aint[0], aint[1]);
                    int j1 = Math.max(aint[0], aint[1]);
                    return new RangeInt(l, j1);
                }
                else
                {
                    Config.warn("Invalid range: " + str);
                    return null;
                }
            }
        }
    }

    private NbtTagValue[] parseNbtTagValues(Properties props)
    {
        String s = "nbt.";
        Map map = getMatchingProperties(props, s);

        if (map.size() <= 0)
        {
            return null;
        }
        else
        {
            List list = new ArrayList();

            for (Object o : map.keySet())
            {
                String s1 = (String) o;
                String s2 = (String)map.get(s1);
                String s3 = s1.substring(s.length());
                NbtTagValue nbttagvalue = new NbtTagValue(s3, s2);
                list.add(nbttagvalue);
            }

            NbtTagValue[] anbttagvalue = (NbtTagValue[]) list.toArray(new NbtTagValue[list.size()]);
            return anbttagvalue;
        }
    }

    private static Map getMatchingProperties(Properties props, String keyPrefix)
    {
        Map map = new LinkedHashMap();

        for (Object o: props.keySet())
        {
            String s = (String) o;
            String s1 = props.getProperty(s);

            if (s.startsWith(keyPrefix))
            {
                map.put(s, s1);
            }
        }

        return map;
    }

    private int parseHand(String str)
    {
        if (str == null)
        {
            return 0;
        }
        else
        {
            str = str.toLowerCase();

            if (str.equals("any"))
            {
                return 0;
            }
            else if (str.equals("main"))
            {
                return 1;
            }
            else if (str.equals("off"))
            {
                return 2;
            }
            else
            {
                Config.warn("Invalid hand: " + str);
                return 0;
            }
        }
    }

    public boolean isValid(String path)
    {
        if (this.name != null && this.name.length() > 0)
        {
            if (this.basePath == null)
            {
                Config.warn("No base path found: " + path);
                return false;
            }
            else if (this.type == 0)
            {
                Config.warn("No type defined: " + path);
                return false;
            }
            else
            {
                if (this.type == 1 || this.type == 3)
                {
                    if (this.items == null)
                    {
                        this.items = this.detectItems();
                    }

                    if (this.items == null)
                    {
                        Config.warn("No items defined: " + path);
                        return false;
                    }
                }

                if (this.texture == null && this.mapTextures == null && this.model == null && this.mapModels == null)
                {
                    Config.warn("No texture or model specified: " + path);
                    return false;
                }
                else if (this.type == 2 && this.enchantmentIds == null)
                {
                    Config.warn("No enchantmentIDs specified: " + path);
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            Config.warn("No name found: " + path);
            return false;
        }
    }

    private int[] detectItems()
    {
        Item item = Item.getByNameOrId(this.name);

        if (item == null)
        {
            return null;
        }
        else
        {
            int i = Item.getIdFromItem(item);
            return i <= 0 ? null : new int[] {i};
        }
    }

    public void updateIcons(TextureMap textureMap)
    {
        if (this.texture != null)
        {
            this.textureLocation = this.getTextureLocation(this.texture);

            if (this.type == 1)
            {
                ResourceLocation resourcelocation = this.getSpriteLocation(this.textureLocation);
                this.sprite = textureMap.registerSprite(resourcelocation);
            }
        }

        if (this.mapTextures != null)
        {
            this.mapTextureLocations = new HashMap();
            this.mapSprites = new HashMap();

            for (String s : this.mapTextures.keySet())
            {
                String s1 = this.mapTextures.get(s);
                ResourceLocation resourcelocation1 = this.getTextureLocation(s1);
                this.mapTextureLocations.put(s, resourcelocation1);

                if (this.type == 1)
                {
                    ResourceLocation resourcelocation2 = this.getSpriteLocation(resourcelocation1);
                    TextureAtlasSprite textureatlassprite = textureMap.registerSprite(resourcelocation2);
                    this.mapSprites.put(s, textureatlassprite);
                }
            }
        }
    }

    private ResourceLocation getTextureLocation(String texName)
    {
        if (texName == null)
        {
            return null;
        }
        else
        {
            ResourceLocation resourcelocation = new ResourceLocation(texName);
            String s = resourcelocation.getResourceDomain();
            String s1 = resourcelocation.getResourcePath();

            if (!s1.contains("/"))
            {
                s1 = "textures/items/" + s1;
            }

            String s2 = s1 + ".png";
            ResourceLocation resourcelocation1 = new ResourceLocation(s, s2);
            boolean flag = Config.hasResource(resourcelocation1);

            if (!flag)
            {
                Config.warn("File not found: " + s2);
            }

            return resourcelocation1;
        }
    }

    private ResourceLocation getSpriteLocation(ResourceLocation resLoc)
    {
        String s = resLoc.getResourcePath();
        s = StrUtils.removePrefix(s, "textures/");
        s = StrUtils.removeSuffix(s, ".png");
        ResourceLocation resourcelocation = new ResourceLocation(resLoc.getResourceDomain(), s);
        return resourcelocation;
    }

    public void updateModelTexture(TextureMap textureMap, ItemModelGenerator itemModelGenerator)
    {
        if (this.texture != null || this.mapTextures != null)
        {
            String[] astring = this.getModelTextures();
            boolean flag = this.isUseTint();
            this.bakedModelTexture = makeBakedModel(textureMap, itemModelGenerator, astring, flag);

            if (this.type == 1 && this.mapTextures != null)
            {
                for (String s : this.mapTextures.keySet())
                {
                    String s1 = this.mapTextures.get(s);
                    String s2 = StrUtils.removePrefix(s, "texture.");

                    if (s2.startsWith("bow") || s2.startsWith("fishing_rod") || s2.startsWith("shield"))
                    {
                        String[] astring1 = new String[] {s1};
                        IBakedModel ibakedmodel = makeBakedModel(textureMap, itemModelGenerator, astring1, flag);

                        if (this.mapBakedModelsTexture == null)
                        {
                            this.mapBakedModelsTexture = new HashMap();
                        }

                        this.mapBakedModelsTexture.put(s2, ibakedmodel);
                    }
                }
            }
        }
    }

    private boolean isUseTint()
    {
        return true;
    }

    private static IBakedModel makeBakedModel(TextureMap textureMap, ItemModelGenerator itemModelGenerator, String[] textures, boolean useTint)
    {
        String[] astring = new String[textures.length];

        for (int i = 0; i < astring.length; ++i)
        {
            String s = textures[i];
            astring[i] = StrUtils.removePrefix(s, "textures/");
        }

        ModelBlock modelblock = makeModelBlock(astring);
        ModelBlock modelblock1 = itemModelGenerator.makeItemModel(textureMap, modelblock);
        IBakedModel ibakedmodel = bakeModel(textureMap, modelblock1, useTint);
        return ibakedmodel;
    }

    private String[] getModelTextures()
    {
        if (this.type == 1 && this.items.length == 1)
        {
            Item item = Item.getItemById(this.items[0]);

            if (item == Items.potionitem && this.damage != null && this.damage.getCountRanges() > 0)
            {
                RangeInt rangeint = this.damage.getRange(0);
                int i = rangeint.getMin();
                boolean flag = (i & 16384) != 0;
                String s5 = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
                String s6 = null;

                if (flag)
                {
                    s6 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash");
                }
                else
                {
                    s6 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
                }

                return new String[] {s5, s6};
            }

            if (item instanceof ItemArmor itemarmor)
            {

                if (itemarmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER)
                {
                    String s = "leather";
                    String s1 = "helmet";

                    if (itemarmor.armorType == 0)
                    {
                        s1 = "helmet";
                    }

                    if (itemarmor.armorType == 1)
                    {
                        s1 = "chestplate";
                    }

                    if (itemarmor.armorType == 2)
                    {
                        s1 = "leggings";
                    }

                    if (itemarmor.armorType == 3)
                    {
                        s1 = "boots";
                    }

                    String s2 = s + "_" + s1;
                    String s3 = this.getMapTexture(this.mapTextures, "texture." + s2, "items/" + s2);
                    String s4 = this.getMapTexture(this.mapTextures, "texture." + s2 + "_overlay", "items/" + s2 + "_overlay");
                    return new String[] {s3, s4};
                }
            }
        }

        return new String[] {this.texture};
    }

    private String getMapTexture(Map<String, String> map, String key, String def)
    {
        if (map == null)
        {
            return def;
        }
        else
        {
            String s = map.get(key);
            return s == null ? def : s;
        }
    }

    private static ModelBlock makeModelBlock(String[] modelTextures)
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("{\"parent\": \"builtin/generated\",\"textures\": {");

        for (int i = 0; i < modelTextures.length; ++i)
        {
            String s = modelTextures[i];

            if (i > 0)
            {
                stringbuffer.append(", ");
            }

            stringbuffer.append("\"layer" + i + "\": \"" + s + "\"");
        }

        stringbuffer.append("}}");
        String s1 = stringbuffer.toString();
        ModelBlock modelblock = ModelBlock.deserialize(s1);
        return modelblock;
    }

    private static IBakedModel bakeModel(TextureMap textureMap, ModelBlock modelBlockIn, boolean useTint)
    {
        ModelRotation modelrotation = ModelRotation.X0_Y0;
        boolean flag = false;
        String s = modelBlockIn.resolveTextureName("particle");
        TextureAtlasSprite textureatlassprite = textureMap.getAtlasSprite((new ResourceLocation(s)).toString());
        SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(modelBlockIn)).setTexture(textureatlassprite);

        for (BlockPart blockpart : modelBlockIn.getElements())
        {
            for (EnumFacing enumfacing : blockpart.mapFaces.keySet())
            {
                BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);

                if (!useTint)
                {
                    blockpartface = new BlockPartFace(blockpartface.cullFace, -1, blockpartface.texture, blockpartface.blockFaceUV);
                }

                String s1 = modelBlockIn.resolveTextureName(blockpartface.texture);
                TextureAtlasSprite textureatlassprite1 = textureMap.getAtlasSprite((new ResourceLocation(s1)).toString());
                BakedQuad bakedquad = makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, modelrotation, flag);

                if (blockpartface.cullFace == null)
                {
                    simplebakedmodel$builder.addGeneralQuad(bakedquad);
                }
                else
                {
                    simplebakedmodel$builder.addFaceQuad(modelrotation.rotateFace(blockpartface.cullFace), bakedquad);
                }
            }
        }

        return simplebakedmodel$builder.makeBakedModel();
    }

    private static BakedQuad makeBakedQuad(BlockPart blockPart, BlockPartFace blockPartFace, TextureAtlasSprite textureAtlasSprite, EnumFacing enumFacing, ModelRotation modelRotation, boolean uvLocked)
    {
        FaceBakery facebakery = new FaceBakery();
        return facebakery.makeBakedQuad(blockPart.positionFrom, blockPart.positionTo, blockPartFace, textureAtlasSprite, enumFacing, modelRotation, blockPart.partRotation, uvLocked, blockPart.shade);
    }

    public String toString()
    {
        return this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
    }

    public float getTextureWidth(TextureManager textureManager)
    {
        if (this.textureWidth <= 0)
        {
            if (this.textureLocation != null)
            {
                ITextureObject itextureobject = textureManager.getTexture(this.textureLocation);
                int i = itextureobject.getGlTextureId();
                int j = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i);
                this.textureWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                GlStateManager.bindTexture(j);
            }

            if (this.textureWidth <= 0)
            {
                this.textureWidth = 16;
            }
        }

        return (float)this.textureWidth;
    }

    public float getTextureHeight(TextureManager textureManager)
    {
        if (this.textureHeight <= 0)
        {
            if (this.textureLocation != null)
            {
                ITextureObject itextureobject = textureManager.getTexture(this.textureLocation);
                int i = itextureobject.getGlTextureId();
                int j = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i);
                this.textureHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                GlStateManager.bindTexture(j);
            }

            if (this.textureHeight <= 0)
            {
                this.textureHeight = 16;
            }
        }

        return (float)this.textureHeight;
    }

    public IBakedModel getBakedModel(ResourceLocation modelLocation, boolean fullModel)
    {
        IBakedModel ibakedmodel;
        Map<String, IBakedModel> map;

        if (fullModel)
        {
            ibakedmodel = this.bakedModelFull;
            map = this.mapBakedModelsFull;
        }
        else
        {
            ibakedmodel = this.bakedModelTexture;
            map = this.mapBakedModelsTexture;
        }

        if (modelLocation != null && map != null)
        {
            String s = modelLocation.getResourcePath();
            IBakedModel ibakedmodel1 = map.get(s);

            if (ibakedmodel1 != null)
            {
                return ibakedmodel1;
            }
        }

        return ibakedmodel;
    }

    public void loadModels(ModelBakery modelBakery)
    {
        if (this.model != null)
        {
            loadItemModel(modelBakery, this.model);
        }

        if (this.type == 1 && this.mapModels != null)
        {
            for (String s : this.mapModels.keySet())
            {
                String s1 = this.mapModels.get(s);
                String s2 = StrUtils.removePrefix(s, "model.");

                if (s2.startsWith("bow") || s2.startsWith("fishing_rod") || s2.startsWith("shield"))
                {
                    loadItemModel(modelBakery, s1);
                }
            }
        }
    }

    public void updateModelsFull()
    {
        ModelManager modelmanager = Config.getModelManager();
        IBakedModel ibakedmodel = modelmanager.getMissingModel();

        if (this.model != null)
        {
            ResourceLocation resourcelocation = getModelLocation(this.model);
            ModelResourceLocation modelresourcelocation = new ModelResourceLocation(resourcelocation, "inventory");
            this.bakedModelFull = modelmanager.getModel(modelresourcelocation);

            if (this.bakedModelFull == ibakedmodel)
            {
                Config.warn("Custom Items: Model not found " + modelresourcelocation.getResourcePath());
                this.bakedModelFull = null;
            }
        }

        if (this.type == 1 && this.mapModels != null)
        {
            for (String s : this.mapModels.keySet())
            {
                String s1 = this.mapModels.get(s);
                String s2 = StrUtils.removePrefix(s, "model.");

                if (s2.startsWith("bow") || s2.startsWith("fishing_rod") || s2.startsWith("shield"))
                {
                    ResourceLocation resourcelocation1 = getModelLocation(s1);
                    ModelResourceLocation modelresourcelocation1 = new ModelResourceLocation(resourcelocation1, "inventory");
                    IBakedModel ibakedmodel1 = modelmanager.getModel(modelresourcelocation1);

                    if (ibakedmodel1 == ibakedmodel)
                    {
                        Config.warn("Custom Items: Model not found " + modelresourcelocation1.getResourcePath());
                    }
                    else
                    {
                        if (this.mapBakedModelsFull == null)
                        {
                            this.mapBakedModelsFull = new HashMap();
                        }

                        this.mapBakedModelsFull.put(s2, ibakedmodel1);
                    }
                }
            }
        }
    }

    private static void loadItemModel(ModelBakery modelBakery, String model)
    {
        ResourceLocation resourcelocation = getModelLocation(model);
        ModelResourceLocation modelresourcelocation = new ModelResourceLocation(resourcelocation, "inventory");

        if (Reflector.ModelLoader.exists())
        {
            try
            {
                Object object = Reflector.ModelLoader_VanillaLoader_INSTANCE.getValue();
                checkNull(object, "vanillaLoader is null");
                Object object1 = Reflector.call(object, Reflector.ModelLoader_VanillaLoader_loadModel, modelresourcelocation);
                checkNull(object1, "iModel is null");
                Map map = (Map)Reflector.getFieldValue(modelBakery, Reflector.ModelLoader_stateModels);
                checkNull(map, "stateModels is null");
                map.put(modelresourcelocation, object1);
                Set set = (Set)Reflector.getFieldValue(modelBakery, Reflector.ModelLoader_textures);
                checkNull(set, "registryTextures is null");
                Collection collection = (Collection)Reflector.call(object1, Reflector.IModel_getTextures, new Object[0]);
                checkNull(collection, "modelTextures is null");
                set.addAll(collection);
            }
            catch (Exception exception)
            {
                Config.warn("Error registering model with ModelLoader: " + modelresourcelocation + ", " + exception.getClass().getName() + ": " + exception.getMessage());
            }
        }
        else
        {
            modelBakery.loadItemModel(resourcelocation.toString(), modelresourcelocation, resourcelocation);
        }
    }

    private static void checkNull(Object obj, String msg) throws NullPointerException
    {
        if (obj == null)
        {
            throw new NullPointerException(msg);
        }
    }

    private static ResourceLocation getModelLocation(String modelName)
    {
        return Reflector.ModelLoader.exists() && !modelName.startsWith("mcpatcher/") && !modelName.startsWith("optifine/") ? new ResourceLocation("models/" + modelName) : new ResourceLocation(modelName);
    }
}
