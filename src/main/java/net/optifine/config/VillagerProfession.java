package net.optifine.config;

import net.minecraft.src.Config;

public class VillagerProfession
{
    private final int profession;
    private int[] careers;

    public VillagerProfession(int profession)
    {
        this(profession, null);
    }

    public VillagerProfession(int profession, int career)
    {
        this(profession, new int[] {career});
    }

    public VillagerProfession(int profession, int[] careers)
    {
        this.profession = profession;
        this.careers = careers;
    }

    public boolean matches(int prof, int car)
    {
        return this.profession == prof && (this.careers == null || Config.equalsOne(car, this.careers));
    }

    private boolean hasCareer(int car)
    {
        return this.careers != null && Config.equalsOne(car, this.careers);
    }

    public boolean addCareer(int car)
    {
        if (this.careers == null)
        {
            this.careers = new int[] {car};
            return true;
        }
        else if (this.hasCareer(car))
        {
            return false;
        }
        else
        {
            this.careers = Config.addIntToArray(this.careers, car);
            return true;
        }
    }

    public int getProfession()
    {
        return this.profession;
    }

    public int[] getCareers()
    {
        return this.careers;
    }

    public String toString()
    {
        return this.careers == null ? "" + this.profession : this.profession + ":" + Config.arrayToString(this.careers);
    }
}
