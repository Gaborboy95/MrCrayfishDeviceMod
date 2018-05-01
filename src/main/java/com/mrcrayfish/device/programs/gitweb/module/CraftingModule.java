package com.mrcrayfish.device.programs.gitweb.module;

import com.mrcrayfish.device.programs.gitweb.component.ContainerBox;
import com.mrcrayfish.device.programs.gitweb.component.CraftingBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.util.Arrays;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class CraftingModule extends ContainerModule
{
    @Override
    public int getHeight()
    {
        return CraftingBox.HEIGHT;
    }

    @Override
    public ContainerBox createContainer(Map<String, String> data)
    {
        ItemStack[] ingredient = new ItemStack[9];
        Arrays.fill(ingredient, ItemStack.EMPTY);
        for(int i = 0; i < ingredient.length; i++)
        {
            if(data.containsKey("slot-" + (i + 1)))
            {
                try
                {
                    ingredient[i] = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-" + (i + 1))));
                }
                catch(NBTException e)
                {
                    e.printStackTrace();
                }
            }
        }

        ItemStack result = ItemStack.EMPTY;
        if(data.containsKey("slot-result"))
        {
            try
            {
                result = new ItemStack(JsonToNBT.getTagFromJson(data.get("slot-result")));
            }
            catch(NBTException e)
            {
                e.printStackTrace();
            }
        }

        return new CraftingBox(ingredient, result);
    }
}
