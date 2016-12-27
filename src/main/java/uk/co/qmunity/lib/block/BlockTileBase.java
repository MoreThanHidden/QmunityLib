package uk.co.qmunity.lib.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import uk.co.qmunity.lib.misc.EnumFacingUtils;
import uk.co.qmunity.lib.tile.IRotatable;
import uk.co.qmunity.lib.tile.TileBase;

public abstract class BlockTileBase extends BlockBase implements ITileEntityProvider{

    private int guiId = -1;
    private Class<? extends TileBase> tileEntityClass;
    private boolean isRedstoneEmitter;

    public BlockTileBase(Material material, Class<? extends TileBase> tileEntityClass){
        super(material);
        isBlockContainer = true;
        setTileEntityClass(tileEntityClass);
    }

    public BlockTileBase setGuiId(int guiId){
        this.guiId = guiId;
        return this;
    }

    public int getGuiId(){
        return guiId;
    }

    public BlockTileBase setTileEntityClass(Class<? extends TileBase> tileEntityClass){
        if(tileEntityClass == null) throw new NullPointerException("Entity class can't be null!");
        this.tileEntityClass = tileEntityClass;
        return this;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata){

        try {
            return getTileEntity().newInstance();
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Fetches the TileEntity Class that goes with the block
     * 
     * @return a .class
     */
    protected Class<? extends TileEntity> getTileEntity(){

        return tileEntityClass;
    }

    public BlockTileBase emitsRedstone(){

        isRedstoneEmitter = true;
        return this;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return isRedstoneEmitter;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(world, pos, neighbor);
        // Only do this on the server side.
        if(!((World)world).isRemote) {
            TileBase tileEntity = (TileBase)world.getTileEntity(pos);
            if(tileEntity != null) {
                tileEntity.onBlockNeighbourChanged();
            }
        }
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        TileEntity te = blockAccess.getTileEntity(pos);
        if(te instanceof TileBase) {
            TileBase tileBase = (TileBase)te;
            return tileBase.getOutputtingRedstone();
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(player.isSneaking()) {
            return false;
        }

        TileEntity entity = world.getTileEntity(pos);
        if(entity == null || !(entity instanceof TileBase)) {
            return false;
        }

        if(getGuiId() >= 0) {
            if(!world.isRemote) player.openGui(getModInstance(), getGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return false;
    }

    protected abstract Object getModInstance();

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if(shouldDropItems()) {
            TileBase tile = (TileBase)world.getTileEntity(pos);
            if(tile != null) {
                for(ItemStack stack : tile.getDrops()) {
                    spawnItemInWorld(world, stack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                }
            }
        }
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    private static void spawnItemInWorld(World world, ItemStack itemStack, double x, double y, double z){

        if(world.isRemote) return;
        float dX = world.rand.nextFloat() * 0.8F + 0.1F;
        float dY = world.rand.nextFloat() * 0.8F + 0.1F;
        float dZ = world.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(world, x + dX, y + dY, z + dZ, new ItemStack(itemStack.getItem(), itemStack.getCount(), itemStack.getItemDamage()));

        if(itemStack.hasTagCompound()) {
            entityItem.getEntityItem().setTagCompound((NBTTagCompound)itemStack.getTagCompound().copy());
        }

        float factor = 0.05F;
        entityItem.motionX = world.rand.nextGaussian() * factor;
        entityItem.motionY = world.rand.nextGaussian() * factor + 0.2F;
        entityItem.motionZ = world.rand.nextGaussian() * factor;
        world.spawnEntity(entityItem);
        itemStack.setCount(0);
    }

    protected boolean shouldDropItems(){
        return true;
    }


    /**
     * Method to detect how the block was placed, and what way it's facing.
     */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof IRotatable) {
            ((IRotatable)te).setFacingDirection(EnumFacingUtils.getDirectionFacing(placer, canRotateVertical()).getOpposite());
        }
    }

    protected boolean canRotateVertical(){

        return true;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing dir) {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof IRotatable) {
            IRotatable rotatable = (IRotatable)te;
            if(dir != EnumFacing.UP && dir != EnumFacing.DOWN || canRotateVertical()) {
                rotatable.setFacingDirection(dir);
                return true;
            }
        }
        return false;
    }
}
