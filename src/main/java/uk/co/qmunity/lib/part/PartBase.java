package uk.co.qmunity.lib.part;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import uk.co.qmunity.lib.client.render.RenderHelper;
import uk.co.qmunity.lib.helper.ItemHelper;
import uk.co.qmunity.lib.part.compat.PartUpdateManager;
import uk.co.qmunity.lib.raytrace.QMovingObjectPosition;
import uk.co.qmunity.lib.vec.Vec3d;
import uk.co.qmunity.lib.vec.Vec3dCube;
import uk.co.qmunity.lib.vec.Vec3i;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Basic implementation of the {@link IPart} interface.
 *
 * @author amadornes
 */
public abstract class PartBase implements IPart {

    private ITilePartHolder parent;

    @Override
    public World getWorld() {

        return getParent().getWorld();
    }

    @Override
    public int getX() {

        return getParent().getX();
    }

    @Override
    public int getY() {

        return getParent().getY();
    }

    @Override
    public int getZ() {

        return getParent().getZ();
    }

    @Override
    public ITilePartHolder getParent() {

        return parent;
    }

    @Override
    public void setParent(ITilePartHolder parent) {

        this.parent = parent;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {

    }

    @Override
    public void writeUpdateToNBT(NBTTagCompound tag) {

    }

    @Override
    public void readUpdateFromNBT(NBTTagCompound tag) {

    }

    @Override
    public void sendUpdatePacket() {

        if (parent != null)
            PartUpdateManager.sendPartUpdate(parent, this);
    }

    @Override
    public ItemStack getPickedItem(QMovingObjectPosition mop) {

        return getItem();
    }

    @Override
    public List<ItemStack> getDrops() {

        List<ItemStack> items = new ArrayList<ItemStack>();

        ItemStack is = getItem();
        if (is != null) {
            is.stackSize = 1;
            items.add(is);
        }

        return items;
    }

    @Override
    public void breakAndDrop(boolean creative) {

        List<ItemStack> drops = getDrops();
        if (!creative && drops != null && drops.size() > 0)
            for (ItemStack item : drops)
                ItemHelper.dropItem(getWorld(), getX(), getY(), getZ(), item);

        getParent().removePart(this);
    }

    @Override
    public double getHardness(EntityPlayer player, QMovingObjectPosition mop) {

        return 0.1;
    }

    @Override
    public int getLightValue() {

        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderBreaking(Vec3i translation, RenderHelper renderer, RenderBlocks renderBlocks, int pass, QMovingObjectPosition mop) {

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderStatic(Vec3i translation, RenderHelper renderer, RenderBlocks renderBlocks, int pass) {

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Vec3d translation, double delta, int pass) {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRenderOnPass(int pass) {

        return pass == 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3dCube getRenderBounds() {

        return new Vec3dCube(0, 0, 0, 1, 1, 1);
    }

}
