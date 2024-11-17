package net.optifine.config;

public class GlVersion
{
    private final int major;
    private final int minor;
    private final int release;
    private final String suffix;

    public GlVersion(int major, int minor)
    {
        this(major, minor, 0);
    }

    public GlVersion(int major, int minor, int release)
    {
        this(major, minor, release, null);
    }

    public GlVersion(int major, int minor, int release, String suffix)
    {
        this.major = major;
        this.minor = minor;
        this.release = release;
        this.suffix = suffix;
    }

    public int getMajor()
    {
        return this.major;
    }

    public int getMinor()
    {
        return this.minor;
    }

    public int getRelease()
    {
        return this.release;
    }

    public int toInt()
    {
        return this.minor > 9 ? this.major * 100 + this.minor : (this.release > 9 ? this.major * 100 + this.minor * 10 + 9 : this.major * 100 + this.minor * 10 + this.release);
    }

    public String toString()
    {
        return this.suffix == null ? this.major + "." + this.minor + "." + this.release : this.major + "." + this.minor + "." + this.release + this.suffix;
    }
}
