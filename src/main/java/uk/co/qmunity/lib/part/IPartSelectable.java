package uk.co.qmunity.lib.part;

import net.minecraft.util.math.Vec3d;
import uk.co.qmunity.lib.raytrace.QRayTraceResult;
import uk.co.qmunity.lib.vec.Vec3dCube;

import java.util.List;

/**
 * Interface implemented by parts that have selection boxes.
 *
 * @author amadornes
 */
public interface IPartSelectable extends IPart {

    /**
     * Raytraces the part from the start to the end point.
     */
    public QRayTraceResult rayTrace(Vec3d start, Vec3d end);

    /**
     * Gets this part's selection boxes.
     */
    public List<Vec3dCube> getSelectionBoxes();

}
