package uk.co.qmunity.lib.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import uk.co.qmunity.lib.part.IPart;
import uk.co.qmunity.lib.part.PartRegistry;
import uk.co.qmunity.lib.part.compat.MultipartCompatibility;
import uk.co.qmunity.lib.ref.Names;

public abstract class ItemMultipart extends Item {

    public ItemMultipart() {

        setUnlocalizedName(Names.Unlocalized.Items.MULTIPART);
    }


    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IPart part = createPart(player.getHeldItem(hand), player, world, new RayTraceResult(new Vec3d(hitX, hitY, hitZ), facing, pos));

        if (part == null)
           return EnumActionResult.PASS;

        return MultipartCompatibility.placePartInWorld(part, world, pos, facing, player, player.getHeldItem(hand)) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    public abstract String getCreatedPartType(ItemStack item, EntityPlayer player, World world, RayTraceResult mop);

    public IPart createPart(ItemStack item, EntityPlayer player, World world, RayTraceResult mop) {

        return PartRegistry.createPart(getCreatedPartType(item, player, world, mop), world.isRemote);
    }

}
