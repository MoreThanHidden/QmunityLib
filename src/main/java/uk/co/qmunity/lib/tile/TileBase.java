package uk.co.qmunity.lib.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import uk.co.qmunity.lib.network.annotation.DescSynced;
import uk.co.qmunity.lib.network.annotation.NetworkUtils;
import uk.co.qmunity.lib.network.annotation.SyncedField;
import uk.co.qmunity.lib.util.QLog;
import uk.co.qmunity.lib.vec.IWorldLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Base tile entity which provides you a few options. Notably, if you mark a field with @DescSynced, it will be automatically synchronize
 * with the client if the field its value gets changed. If this causes too much traffic, you can additionally mark it with @LazySynced. This will
 * not cause synchronization to occur when this particular field changes, but it will get send along when other fields marked with @DescSynced
 * change, or when sendUpdatePacket() is called manually. Marking @GuiSynced can be used to synchronize fields only with players that have
 * the container open that is associated with this tile entity. Be sure to extend your Container to ContainerBase!
 * @author MineMaarten
 */
public class TileBase extends TileEntity implements IRotatable, IWorldLocation, ITickable{

    private boolean isRedstonePowered;
    private int outputtingRedstone;
    private int ticker = 0;
    private EnumFacing rotation = EnumFacing.UP;
    private List<SyncedField> descriptionFields;

    /*************** BASIC TE FUNCTIONS **************/

    /**
     * This function gets called whenever the world/chunk loads
     */
    @Override
    public void readFromNBT(NBTTagCompound tCompound){

        super.readFromNBT(tCompound);
        isRedstonePowered = tCompound.getBoolean("isRedstonePowered");
        readFromPacketNBT(tCompound);
    }

    /**
     * This function gets called whenever the world/chunk is saved
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tCompound){

        super.writeToNBT(tCompound);
        tCompound.setBoolean("isRedstonePowered", isRedstonePowered);

        writeToPacketNBT(tCompound);

        return tCompound;
    }

    /**
     * Tags written in here are synced upon markBlockForUpdate.
     * 
     * @param tCompound
     */
    public void writeToPacketNBT(NBTTagCompound tCompound){

        tCompound.setByte("rotation", (byte)rotation.ordinal());
        tCompound.setByte("outputtingRedstone", (byte)outputtingRedstone);
    }

    public void readFromPacketNBT(NBTTagCompound tCompound){

        rotation = EnumFacing.getFront(tCompound.getByte("rotation"));
        if(rotation.ordinal() > 5) {
            QLog.warning("invalid rotation!");
            rotation = EnumFacing.UP;
        }
        outputtingRedstone = tCompound.getByte("outputtingRedstone");
        if(world != null) markForRenderUpdate();
    }

    public List<SyncedField> getDescriptionFields(){
        if(descriptionFields == null) {
            descriptionFields = NetworkUtils.getSyncedFields(this, DescSynced.class);
            for(SyncedField field : descriptionFields) {
                field.update();
            }
        }
        return descriptionFields;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){

        readFromPacketNBT(pkt.getNbtCompound());
    }

    protected void sendUpdatePacket(){

        if(!world.isRemote) world.markChunkDirty(pos, this);
    }

    protected void markForRenderUpdate(){

        if(world != null) world.markBlockRangeForRenderUpdate(getX(), getY(), getZ(), getX(), getY(), getZ());
    }

    protected void notifyNeighborBlockUpdate(){

        world.notifyBlockUpdate(pos, getBlockType().getDefaultState(), world.getBlockState(pos), 0);
    }

    /**
     * Function gets called every tick. Do not forget to call the super method!
     */
    @Override
    public void update(){

        if(ticker == 0) {
            onTileLoaded();
        }
        ticker++;

        if(!world.isRemote) {
            boolean descriptionPacketScheduled = false;
            if(descriptionFields == null) descriptionPacketScheduled = true;
            for(SyncedField field : getDescriptionFields()) {
                if(field.update()) {
                    descriptionPacketScheduled = true;
                }
            }

            if(descriptionPacketScheduled) {
                sendUpdatePacket();
            }
        }
    }

    /**
     * ************** ADDED FUNCTIONS ****************
     */

    public void onBlockNeighbourChanged(){

        checkRedstonePower();
    }

    /**
     * Checks if redstone has changed.
     */
    public void checkRedstonePower(){

        boolean isIndirectlyPowered = (getWorld().isBlockIndirectlyGettingPowered(getPos()) != 0);
        if(isIndirectlyPowered && !getIsRedstonePowered()) {
            redstoneChanged(true);
        } else if(getIsRedstonePowered() && !isIndirectlyPowered) {
            redstoneChanged(false);
        }
    }

    /**
     * Before being able to use this, remember to mark the block as redstone emitter by calling BlockContainerBase#emitsRedstone()
     * 
     * @param newValue
     */
    public void setOutputtingRedstone(boolean newValue){

        setOutputtingRedstone(newValue ? 15 : 0);
    }

    /**
     * Before being able to use this, remember to mark the block as redstone emitter by calling BlockContainerBase#emitsRedstone()
     * 
     * @param value
     */
    public void setOutputtingRedstone(int value){

        value = Math.max(0, value);
        value = Math.min(15, value);
        if(outputtingRedstone != value) {
            outputtingRedstone = value;
            notifyNeighborBlockUpdate();
        }
    }

    public int getOutputtingRedstone(){

        return outputtingRedstone;
    }

    /**
     * This method can be overwritten to get alerted when the redstone level has changed.
     * 
     * @param newValue
     *            The redstone level it is at now
     */
    protected void redstoneChanged(boolean newValue){

        isRedstonePowered = newValue;
    }

    /**
     * Check whether or not redstone level is high
     */
    public boolean getIsRedstonePowered(){

        return isRedstonePowered;
    }

    /**
     * Returns the ticker of the Tile, this number wll increase every tick
     * 
     * @return the ticker
     */
    public int getTicker(){

        return ticker;
    }

    /**
     * Gets called when the TileEntity ticks for the first time, the world is accessible and updateEntity() has not been ran yet
     */
    protected void onTileLoaded(){

        if(!getWorld().isRemote) onBlockNeighbourChanged();
    }

    public List<ItemStack> getDrops(){

        return new ArrayList<ItemStack>();
    }

    @Override
    public void setFacingDirection(EnumFacing dir){

        rotation = dir;
        if(getWorld() != null) {
            sendUpdatePacket();
            notifyNeighborBlockUpdate();
        }
    }

    @Override
    public EnumFacing getFacingDirection(){

        return rotation;
    }

    public boolean canConnectRedstone(){

        return false;
    }

    public void onNeighborBlockChanged(){

    }

    @Override
    public World getWorld(){
        return getWorld();
    }

    @Override
    public int getX(){
        return pos.getX();
    }

    @Override
    public int getY(){
        return pos.getY();
    }

    @Override
    public int getZ(){
        return pos.getZ();
    }
}
