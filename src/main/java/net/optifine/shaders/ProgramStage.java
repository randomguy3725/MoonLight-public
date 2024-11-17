package net.optifine.shaders;

public enum ProgramStage
{
    NONE(""),
    SHADOW("shadow"),
    GBUFFERS("gbuffers"),
    DEFERRED("deferred"),
    COMPOSITE("composite");

    private final String name;

    ProgramStage(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
