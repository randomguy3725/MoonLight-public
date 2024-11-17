package net.optifine.entity.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.optifine.reflect.Reflector;

public class ModelAdapterBook extends ModelAdapter
{
    public ModelAdapterBook()
    {
        super(TileEntityEnchantmentTable.class, "book", 0.0F);
    }

    public ModelBase makeModel()
    {
        return new ModelBook();
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelBook modelbook))
        {
            return null;
        }
        else
        {
            return modelPart.equals("cover_right") ? modelbook.coverRight : (modelPart.equals("cover_left") ? modelbook.coverLeft : (modelPart.equals("pages_right") ? modelbook.pagesRight : (modelPart.equals("pages_left") ? modelbook.pagesLeft : (modelPart.equals("flipping_page_right") ? modelbook.flippingPageRight : (modelPart.equals("flipping_page_left") ? modelbook.flippingPageLeft : (modelPart.equals("book_spine") ? modelbook.bookSpine : null))))));
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"cover_right", "cover_left", "pages_right", "pages_left", "flipping_page_right", "flipping_page_left", "book_spine"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        TileEntityRendererDispatcher tileentityrendererdispatcher = TileEntityRendererDispatcher.instance;
        TileEntitySpecialRenderer tileentityspecialrenderer = tileentityrendererdispatcher.getSpecialRendererByClass(TileEntityEnchantmentTable.class);

        if (!(tileentityspecialrenderer instanceof TileEntityEnchantmentTableRenderer))
        {
            return null;
        }
        else
        {
            if (tileentityspecialrenderer.getEntityClass() == null)
            {
                tileentityspecialrenderer = new TileEntityEnchantmentTableRenderer();
                tileentityspecialrenderer.setRendererDispatcher(tileentityrendererdispatcher);
            }

            if (!Reflector.TileEntityEnchantmentTableRenderer_modelBook.exists())
            {
                Config.warn("Field not found: TileEntityEnchantmentTableRenderer.modelBook");
                return null;
            }
            else
            {
                Reflector.setFieldValue(tileentityspecialrenderer, Reflector.TileEntityEnchantmentTableRenderer_modelBook, modelBase);
                return tileentityspecialrenderer;
            }
        }
    }
}
