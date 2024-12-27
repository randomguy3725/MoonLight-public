/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & opZywl & lucas]
 */
package wtf.moonlight.gui.click.skeet.component;

public interface ExpandableComponent
{
    float getExpandedX();
    
    float getExpandedY();
    
    float getExpandedWidth();
    
    float getExpandedHeight();
    
    void setExpanded(boolean p0);
    
    boolean isExpanded();
}
