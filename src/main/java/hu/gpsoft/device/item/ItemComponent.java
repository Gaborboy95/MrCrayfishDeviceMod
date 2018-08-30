package hu.gpsoft.device.item;

import com.mrcrayfish.device.MrCrayfishDeviceMod;
import hu.gpsoft.device.GPSoft;
import net.minecraft.item.Item;

/**
 * Author: MrCrayfish
 */
public class ItemComponent extends Item
{
    public ItemComponent(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(id);
        this.setCreativeTab(GPSoft.TAB_DEVICE);
    }
}
