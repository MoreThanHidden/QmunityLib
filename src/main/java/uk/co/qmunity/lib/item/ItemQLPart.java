package uk.co.qmunity.lib.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.co.qmunity.lib.QLModInfo;
import uk.co.qmunity.lib.part.IQLPart;
import uk.co.qmunity.lib.part.MultipartCompat;
import uk.co.qmunity.lib.transform.Rotation;
import uk.co.qmunity.lib.vec.Vector3;

public abstract class ItemQLPart extends QLItemBase {

    public ItemQLPart() {

        this(QLModInfo.MODID + ":multipart");
    }

    public ItemQLPart(String name) {

        super(name);
    }

    private double getHitDepth(Vector3 vhit, EnumFacing side) {

        return vhit.copy().scalarProject(Rotation.axes[side.ordinal()]) + (side.ordinal() % 2 ^ 1);
    }

    private boolean place(World world, BlockPos pos, EnumFacing side, Vector3 hit, ItemStack item, EntityPlayer player) {

        IQLPart part = newPart(world, pos, side, hit, item, player);
        if (part == null || !MultipartCompat.canAddPart(world, pos, part))
            return false;

        if (!world.isRemote)
            MultipartCompat.addPart(world, pos, part);
        if (!player.capabilities.isCreativeMode)
            item.setCount(item.getCount()- 1);
        return true;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        Vector3 hit = new Vector3(hitX, hitY, hitZ);
        double d = getHitDepth(hit, side);

        if (d < 1 && place(world, pos, side, hit, player.getHeldItem(hand), player))
            return EnumActionResult.SUCCESS;

        pos.offset(side);
        return place(world, pos, side, hit, player.getHeldItem(hand), player) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    /**
     * Create a new part based on the placement information parameters.
     */
    public abstract IQLPart newPart(World world, BlockPos pos, EnumFacing side, Vector3 hit, ItemStack item, EntityPlayer player);

}
