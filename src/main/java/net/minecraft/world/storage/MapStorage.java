package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

public class MapStorage
{
    private final ISaveHandler saveHandler;
    protected Map<String, WorldSavedData> loadedDataMap = Maps.newHashMap();
    private final List<WorldSavedData> loadedDataList = Lists.newArrayList();
    private final Map<String, Short> idCounts = Maps.newHashMap();

    public MapStorage(ISaveHandler saveHandlerIn)
    {
        this.saveHandler = saveHandlerIn;
        this.loadIdCounts();
    }

    public WorldSavedData loadData(Class <? extends WorldSavedData > clazz, String dataIdentifier)
    {
        WorldSavedData worldsaveddata = this.loadedDataMap.get(dataIdentifier);

        if (worldsaveddata != null)
        {
            return worldsaveddata;
        }
        else
        {
            if (this.saveHandler != null)
            {
                try
                {
                    File file1 = this.saveHandler.getMapFileFromName(dataIdentifier);

                    if (file1 != null && file1.exists())
                    {
                        try
                        {
                            worldsaveddata = clazz.getConstructor(new Class[] {String.class}).newInstance(dataIdentifier);
                        }
                        catch (Exception exception)
                        {
                            throw new RuntimeException("Failed to instantiate " + clazz.toString(), exception);
                        }

                        FileInputStream fileinputstream = new FileInputStream(file1);
                        NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
                        fileinputstream.close();
                        worldsaveddata.readFromNBT(nbttagcompound.getCompoundTag("data"));
                    }
                }
                catch (Exception exception1)
                {
                    exception1.printStackTrace();
                }
            }

            if (worldsaveddata != null)
            {
                this.loadedDataMap.put(dataIdentifier, worldsaveddata);
                this.loadedDataList.add(worldsaveddata);
            }

            return worldsaveddata;
        }
    }

    public void setData(String dataIdentifier, WorldSavedData data)
    {
        if (this.loadedDataMap.containsKey(dataIdentifier))
        {
            this.loadedDataList.remove(this.loadedDataMap.remove(dataIdentifier));
        }

        this.loadedDataMap.put(dataIdentifier, data);
        this.loadedDataList.add(data);
    }

    public void saveAllData()
    {
        for (int i = 0; i < this.loadedDataList.size(); ++i)
        {
            WorldSavedData worldsaveddata = this.loadedDataList.get(i);

            if (worldsaveddata.isDirty())
            {
                this.saveData(worldsaveddata);
                worldsaveddata.setDirty(false);
            }
        }
    }

    private void saveData(WorldSavedData p_75747_1_)
    {
        if (this.saveHandler != null)
        {
            try
            {
                File file1 = this.saveHandler.getMapFileFromName(p_75747_1_.mapName);

                if (file1 != null)
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    p_75747_1_.writeToNBT(nbttagcompound);
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setTag("data", nbttagcompound);
                    FileOutputStream fileoutputstream = new FileOutputStream(file1);
                    CompressedStreamTools.writeCompressed(nbttagcompound1, fileoutputstream);
                    fileoutputstream.close();
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    private void loadIdCounts()
    {
        try
        {
            this.idCounts.clear();

            if (this.saveHandler == null)
            {
                return;
            }

            File file1 = this.saveHandler.getMapFileFromName("idcounts");

            if (file1 != null && file1.exists())
            {
                DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
                NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
                datainputstream.close();

                for (String s : nbttagcompound.getKeySet())
                {
                    NBTBase nbtbase = nbttagcompound.getTag(s);

                    if (nbtbase instanceof NBTTagShort nbttagshort)
                    {
                        short short1 = nbttagshort.getShort();
                        this.idCounts.put(s, Short.valueOf(short1));
                    }
                }
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public int getUniqueDataId(String key)
    {
        Short oshort = this.idCounts.get(key);

        if (oshort == null)
        {
            oshort = Short.valueOf((short)0);
        }
        else
        {
            oshort = Short.valueOf((short)(oshort.shortValue() + 1));
        }

        this.idCounts.put(key, oshort);

        if (this.saveHandler == null)
        {
            return oshort.shortValue();
        }
        else
        {
            try
            {
                File file1 = this.saveHandler.getMapFileFromName("idcounts");

                if (file1 != null)
                {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();

                    for (String s : this.idCounts.keySet())
                    {
                        short short1 = this.idCounts.get(s).shortValue();
                        nbttagcompound.setShort(s, short1);
                    }

                    DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));
                    CompressedStreamTools.write(nbttagcompound, dataoutputstream);
                    dataoutputstream.close();
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            return oshort.shortValue();
        }
    }
}
