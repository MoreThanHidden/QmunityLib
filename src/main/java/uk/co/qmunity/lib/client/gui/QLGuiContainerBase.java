package uk.co.qmunity.lib.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import uk.co.qmunity.lib.client.gui.widget.IGuiWidget;
import uk.co.qmunity.lib.client.gui.widget.IWidgetAction;
import uk.co.qmunity.lib.client.gui.widget.IWidgetContainer;
import uk.co.qmunity.lib.client.gui.widget.IWidgetListener;
import uk.co.qmunity.lib.client.helper.InputHelper;
import uk.co.qmunity.lib.inventory.slot.ISlotPhantom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.co.qmunity.lib.client.gui.GuiRenderingUtils.drawHorizontalAlignedString;
import static uk.co.qmunity.lib.client.gui.GuiRenderingUtils.mc;

/**
 * @author MineMaarten
 * @author K-4U
 */
public class QLGuiContainerBase extends GuiContainer implements IWidgetListener, IWidgetContainer {

    protected final int xSize, ySize;
    protected int guiTop, guiLeft;
    protected ResourceLocation background;
    protected String title;

    protected List<IGuiWidget> widgets = new ArrayList<IGuiWidget>();
    protected IGuiWidget focus = null;

    protected ScaledResolution resolution;

    public QLGuiContainerBase(Container container, int xSize, int ySize, String background) {

        this(container, xSize, ySize, new ResourceLocation(background));
    }

    public QLGuiContainerBase(Container container, int xSize, int ySize, ResourceLocation background) {

        super(container);

        super.xSize = this.xSize = xSize;
        super.ySize = this.ySize = ySize;

        this.background = background;
    }

    public QLGuiContainerBase(Container container, int xSize, int ySize, String background, String title) {

        this(container, xSize, ySize, background);

        this.title = title;
    }

    public QLGuiContainerBase(Container container, int xSize, int ySize, ResourceLocation background, String title) {

        this(container, xSize, ySize, background);

        this.title = title;
    }

    @Override
    public void initGui() {

        super.initGui();

        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;

        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void onGuiClosed() {

        super.onGuiClosed();

        FMLCommonHandler.instance().bus().unregister(this);
    }

    @Override
    public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3) {

        widgets.clear();
        super.setWorldAndResolution(par1Minecraft, par2, par3);
        resolution = new ScaledResolution(mc());
    }

    public ResourceLocation getBackground() {

        return background;
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int mx, int my) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        drawBackground(mx, my, f);
        drawForeground(mx, my, f);
        drawTooltips(mx, my, f);
    }

    public void drawBackground(int mx, int my, float f) {

        mc.renderEngine.bindTexture(getBackground());
        drawTexturedModalRect(getLeft(), getTop(), 0, 0, getWidth(), getHeight());
    }

    public void drawForeground(int mx, int my, float f) {

        GL11.glPushMatrix();
        GL11.glTranslated(getLeft(), getTop(), 0);

        if (title != null)
            drawHorizontalAlignedString(0, 8, getWidth(), I18n.format(title), true);
        for (IGuiWidget widget : getWidgets()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            widget.render(mx - getLeft(), my - getTop(), f);
        }

        GL11.glPopMatrix();
    }

    public void drawTooltips(int mx, int my, float f) {

        List<String> tip = new ArrayList<String>();

        // Add tips for the widgets we're hovering over
        for (IGuiWidget widget : getWidgets())
            if (widget.isMouseOver(mx - getLeft(), my - getTop()))
                widget.addTooltip(mx - getLeft(), my - getTop(), tip);

        // If the list isn't empty
        if (tip != null && !tip.isEmpty()) {
            // Localize the tooltips
            for (int i = 0; i < tip.size(); i++)
                tip.set(i, I18n.format(tip.get(i)));
            // Render them
            drawHoveringText(tip, mx, my, fontRendererObj);
        }
    }

    @Override
    public void mouseClicked(int mx, int my, int btn) throws IOException {

        super.mouseClicked(mx, my, btn);

        for (IGuiWidget widget : getWidgets())
            if (widget.isMouseOver(mx - getLeft(), my - getTop()))
                widget.onMouseClicked(mx - getLeft(), my - getTop(), btn);
    }

    @Override
    protected void mouseReleased(int mx, int my, int btn) {
        if (btn == -1) {
            for (IGuiWidget widget : getWidgets())
                if (widget.isMouseOver(mx - getLeft(), my - getTop()))
                    widget.onMouseReleased(mx - getLeft(), my - getTop(), btn);
        } else {
            for (IGuiWidget widget : getWidgets())
                if (widget.isMouseOver(mx - getLeft(), my - getTop()))
                    widget.onMouseMoved(mx - getLeft(), my - getTop());
        }
    }


    @Override
    public void mouseClickMove(int mx, int my, int btn, long ticks) {

        super.mouseClickMove(mx, my, btn, ticks);

        for (IGuiWidget widget : getWidgets())
            if (widget.isMouseOver(mx - getLeft(), my - getTop()))
                widget.onMouseDragged(mx - getLeft(), my - getTop(), btn, ticks);
    }

    @Override
    public void keyTyped(char c, int k) throws IOException {

        super.keyTyped(c, k);

        // If we closed the gui, return
        if (mc().currentScreen == null)
            return;

        if (getFocused() != null)
            getFocused().onKeyTyped(k, c);
    }

    // Widget ticking

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;

        for (IGuiWidget widget : getWidgets())
            widget.update();

        int x = (Mouse.getX() / resolution.getScaleFactor()) - getLeft(), y = ((mc().displayHeight - Mouse.getY()) / resolution
                .getScaleFactor()) - getTop();
        int dWheel = InputHelper.getDWheel();
        if (dWheel != 0) {
            for (Slot s : (List<Slot>) inventorySlots.inventorySlots) {
                if (s instanceof ISlotPhantom && s.getHasStack() && x >= s.xPos && x < s.xPos + 16
                        && y >= s.yPos && y < s.yPos + 16) {
                    ItemStack stack = s.getStack().copy();

                    if (dWheel < 0)
                        stack.setCount(Math.min(stack.getCount()- 1,
                                Math.min(stack.getMaxStackSize(), s.inventory.getInventoryStackLimit())));
                    else if (dWheel > 0)
                        stack.setCount(Math.min(stack.getCount() + 1,
                                Math.min(stack.getMaxStackSize(), s.inventory.getInventoryStackLimit())));

                    if (stack.getCount() <= 0)
                        stack = ItemStack.EMPTY;
                    s.putStack(stack);

                    break;
                }
            }
        }
    }

    // IWidgetContainer

    @Override
    public void addWidget(IGuiWidget widget) {

        if (widget == null)
            throw new NullPointerException("Attempted to add a null widget.");
        if (widgets.contains(widget))
            throw new IllegalStateException("Attempted to add a widget that was already there.");

        widgets.add(widget);
        widget.addListener(this);
    }

    @Override
    public List<IGuiWidget> getWidgets() {

        return widgets;
    }

    @Override
    public void setFocus(IGuiWidget widget) {

        if (widget == null) {
            focus = null;
            return;
        }

        if (!widgets.contains(widget))
            throw new IllegalStateException("Attempted to set focus on a widget that's not part of the GUI.");
        focus = widget;
    }

    @Override
    public IGuiWidget getFocused() {

        return focus;
    }

    @Override
    public int getTop() {

        return guiTop;
    }

    @Override
    public int getLeft() {

        return guiLeft;
    }

    @Override
    public int getWidth() {

        return xSize;
    }

    @Override
    public int getHeight() {

        return ySize;
    }

    // IWidgetListener

    @Override
    public void actionPerformed(IGuiWidget widget, IWidgetAction action) {

    }

}