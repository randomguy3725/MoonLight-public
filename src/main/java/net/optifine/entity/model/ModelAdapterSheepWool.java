package net.optifine.entity.model;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerSheepWool;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.src.Config;

public class ModelAdapterSheepWool extends ModelAdapterQuadruped
{
    public ModelAdapterSheepWool()
    {
        super(EntitySheep.class, "sheep_wool", 0.7F);
    }

    public ModelBase makeModel()
    {
        return new ModelSheep1();
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        Render render = rendermanager.getEntityRenderMap().get(EntitySheep.class);

        if (!(render instanceof RenderSheep rendersheep))
        {
            Config.warn("Not a RenderSheep: " + render);
            return null;
        }
        else
        {
            if (render.getEntityClass() == null)
            {
                render = new RenderSheep(rendermanager, new ModelSheep2(), 0.7F);
            }

            List<LayerRenderer<EntitySheep>> list = rendersheep.getLayerRenderers();
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                LayerRenderer layerrenderer = (LayerRenderer)iterator.next();

                if (layerrenderer instanceof LayerSheepWool)
                {
                    iterator.remove();
                }
            }

            LayerSheepWool layersheepwool = new LayerSheepWool(rendersheep);
            layersheepwool.sheepModel = (ModelSheep1)modelBase;
            rendersheep.addLayer(layersheepwool);
            return rendersheep;
        }
    }
}
