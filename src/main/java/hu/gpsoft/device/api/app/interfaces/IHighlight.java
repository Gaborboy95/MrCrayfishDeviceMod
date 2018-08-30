package hu.gpsoft.device.api.app.interfaces;

import net.minecraft.util.text.TextFormatting;

/**
 * Author: MrCrayfish
 */
public interface IHighlight
{
    TextFormatting[] getKeywordFormatting(String text);
}
