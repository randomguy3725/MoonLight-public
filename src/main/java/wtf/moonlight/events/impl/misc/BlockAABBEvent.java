package wtf.moonlight.events.impl.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import wtf.moonlight.events.impl.CancellableEvent;

@Getter
@Setter
@AllArgsConstructor
public class BlockAABBEvent extends CancellableEvent {

    private final World world;
    private final Block block;
    private final BlockPos blockPos;
    private AxisAlignedBB boundingBox;
    private final AxisAlignedBB maskBoundingBox;

}
