package uk.co.qmunity.lib.client.render;

import net.minecraft.client.renderer.Tessellator;
import uk.co.qmunity.lib.model.IVertexConsumer;
import uk.co.qmunity.lib.model.IVertexOperation;
import uk.co.qmunity.lib.model.IVertexSource;
import uk.co.qmunity.lib.model.QLModel;
import uk.co.qmunity.lib.model.Vertex;

public class VertexConsumerTessellator implements IVertexConsumer {

    public static final VertexConsumerTessellator instance = new VertexConsumerTessellator();

    public final RenderContext context = new RenderContext();
    public float overrideAlpha = -1;

    @Override
    public boolean consumeVertices(IVertexSource model, IVertexOperation... operations) {

        IVertexSource transformed = operations.length > 0 ? new QLModel(model).apply(operations) : model;
        Vertex[] vertices = transformed.getVertices();
        for (Vertex v : vertices) {
            if (context.useNormals())
                Tessellator.getInstance().getBuffer().normal((float) v.normal.x, (float) v.normal.y, (float) v.normal.z);

            if (context.useColors())
                Tessellator.getInstance().getBuffer().color((float) v.color.x, (float) v.color.y, (float) v.color.z,
                        overrideAlpha != -1 ? overrideAlpha : (float) v.color.s);
            else if (overrideAlpha != -1)
                Tessellator.getInstance().getBuffer().color(1, 1, 1, overrideAlpha);

            if (context.useLighting())
                Tessellator.getInstance().getBuffer().putBrightness4(v.brightness, v.brightness, v.brightness, v.brightness);

            if (context.useTextures())
                Tessellator.getInstance().getBuffer().tex(v.uv.u, v.uv.v);

            Tessellator.getInstance().getBuffer().pos(v.position.x, v.position.y, v.position.z).endVertex();
        }
        return vertices.length > 0;
    }
}
