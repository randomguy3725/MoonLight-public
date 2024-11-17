package net.minecraft.world;

public class WorldType
{
    public static final WorldType[] worldTypes = new WorldType[16];
    public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();
    public static final WorldType FLAT = new WorldType(1, "flat");
    public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
    public static final WorldType AMPLIFIED = (new WorldType(3, "amplified")).setNotificationData();
    public static final WorldType CUSTOMIZED = new WorldType(4, "customized");
    public static final WorldType DEBUG_WORLD = new WorldType(5, "debug_all_block_states");
    public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);
    private final int worldTypeId;
    private final String worldType;
    private final int generatorVersion;
    private boolean canBeCreated;
    private boolean isWorldTypeVersioned;
    private boolean hasNotificationData;

    private WorldType(int id, String name)
    {
        this(id, name, 0);
    }

    private WorldType(int id, String name, int version)
    {
        this.worldType = name;
        this.generatorVersion = version;
        this.canBeCreated = true;
        this.worldTypeId = id;
        worldTypes[id] = this;
    }

    public String getWorldTypeName()
    {
        return this.worldType;
    }

    public String getTranslateName()
    {
        return "generator." + this.worldType;
    }

    public String getTranslatedInfo()
    {
        return this.getTranslateName() + ".info";
    }

    public int getGeneratorVersion()
    {
        return this.generatorVersion;
    }

    public WorldType getWorldTypeForGeneratorVersion(int version)
    {
        return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
    }

    private WorldType setCanBeCreated(boolean enable)
    {
        this.canBeCreated = enable;
        return this;
    }

    public boolean getCanBeCreated()
    {
        return this.canBeCreated;
    }

    private WorldType setVersioned()
    {
        this.isWorldTypeVersioned = true;
        return this;
    }

    public boolean isVersioned()
    {
        return this.isWorldTypeVersioned;
    }

    public static WorldType parseWorldType(String type)
    {
        for (int i = 0; i < worldTypes.length; ++i)
        {
            if (worldTypes[i] != null && worldTypes[i].worldType.equalsIgnoreCase(type))
            {
                return worldTypes[i];
            }
        }

        return null;
    }

    public int getWorldTypeID()
    {
        return this.worldTypeId;
    }

    public boolean showWorldInfoNotice()
    {
        return this.hasNotificationData;
    }

    private WorldType setNotificationData()
    {
        this.hasNotificationData = true;
        return this;
    }
}
