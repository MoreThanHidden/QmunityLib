package uk.co.qmunity.lib.client;

import net.minecraft.client.renderer.Tessellator;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import uk.co.qmunity.lib.vec.Cuboid;

public class RenderHelper {

    public static void addVertex(double x, double y, double z) {

        GL11.glVertex3d(x, y, z);
    }

    public static void addVertexWithTexture(double x, double y, double z, double tx, double ty) {

        GL11.glTexCoord2d(tx, ty);
        GL11.glVertex3d(x, y, z);
    }

    public static void drawColoredCube(Cuboid vector) {

        // Top side
        GL11.glColor3f(1.0F, 0.0F, 0.0F);
        GL11.glNormal3d(0, 1, 0);
        addVertex(vector.min.x, vector.max.y, vector.max.z);
        addVertex(vector.max.x, vector.max.y, vector.max.z);
        addVertex(vector.max.x, vector.max.y, vector.min.z);
        addVertex(vector.min.x, vector.max.y, vector.min.z);

        // Bottom side
        GL11.glColor3f(1.0F, 1.0F, 0.0F);
        GL11.glNormal3d(0, -1, 0);
        addVertex(vector.max.x, vector.min.y, vector.max.z);
        addVertex(vector.min.x, vector.min.y, vector.max.z);
        addVertex(vector.min.x, vector.min.y, vector.min.z);
        addVertex(vector.max.x, vector.min.y, vector.min.z);

        // Draw west side:
        GL11.glColor3f(0.0F, 1.0F, 0.0F);
        GL11.glNormal3d(-1, 0, 0);
        addVertex(vector.min.x, vector.min.y, vector.max.z);
        addVertex(vector.min.x, vector.max.y, vector.max.z);
        addVertex(vector.min.x, vector.max.y, vector.min.z);
        addVertex(vector.min.x, vector.min.y, vector.min.z);

        // Draw east side:
        GL11.glColor3f(0.0F, 1.0F, 1.0F);
        GL11.glNormal3d(1, 0, 0);
        addVertex(vector.max.x, vector.min.y, vector.min.z);
        addVertex(vector.max.x, vector.max.y, vector.min.z);
        addVertex(vector.max.x, vector.max.y, vector.max.z);
        addVertex(vector.max.x, vector.min.y, vector.max.z);

        // Draw north side
        GL11.glColor3f(0.0F, 0.0F, 1.0F);
        GL11.glNormal3d(0, 0, -1);
        addVertex(vector.min.x, vector.min.y, vector.min.z);
        addVertex(vector.min.x, vector.max.y, vector.min.z);
        addVertex(vector.max.x, vector.max.y, vector.min.z);
        addVertex(vector.max.x, vector.min.y, vector.min.z);

        // Draw south side
        GL11.glColor3f(0.0F, 0.0F, 0.0F);
        GL11.glNormal3d(0, 0, 1);
        addVertex(vector.min.x, vector.min.y, vector.max.z);
        addVertex(vector.max.x, vector.min.y, vector.max.z);
        addVertex(vector.max.x, vector.max.y, vector.max.z);
        addVertex(vector.min.x, vector.max.y, vector.max.z);
    }

    public static void drawColoredCube(Cuboid vector, double r, double g, double b, double a, boolean... renderFaces) {

        GL11.glColor4d(r, g, b, a);

        // Top side
        if (renderFaces.length < 1 || renderFaces[0]) {
            GL11.glNormal3d(0, 1, 0);
            addVertex(vector.min.x, vector.max.y, vector.max.z);
            addVertex(vector.max.x, vector.max.y, vector.max.z);
            addVertex(vector.max.x, vector.max.y, vector.min.z);
            addVertex(vector.min.x, vector.max.y, vector.min.z);
        }

        // Bottom side
        if (renderFaces.length < 2 || renderFaces[1]) {
            GL11.glNormal3d(0, -1, 0);
            addVertex(vector.max.x, vector.min.y, vector.max.z);
            addVertex(vector.min.x, vector.min.y, vector.max.z);
            addVertex(vector.min.x, vector.min.y, vector.min.z);
            addVertex(vector.max.x, vector.min.y, vector.min.z);
        }

        // Draw west side:
        if (renderFaces.length < 3 || renderFaces[2]) {
            GL11.glNormal3d(-1, 0, 0);
            addVertex(vector.min.x, vector.min.y, vector.max.z);
            addVertex(vector.min.x, vector.max.y, vector.max.z);
            addVertex(vector.min.x, vector.max.y, vector.min.z);
            addVertex(vector.min.x, vector.min.y, vector.min.z);
        }

        // Draw east side:
        if (renderFaces.length < 4 || renderFaces[3]) {
            GL11.glNormal3d(1, 0, 0);
            addVertex(vector.max.x, vector.min.y, vector.min.z);
            addVertex(vector.max.x, vector.max.y, vector.min.z);
            addVertex(vector.max.x, vector.max.y, vector.max.z);
            addVertex(vector.max.x, vector.min.y, vector.max.z);
        }

        // Draw north side
        if (renderFaces.length < 5 || renderFaces[4]) {
            GL11.glNormal3d(0, 0, -1);
            addVertex(vector.min.x, vector.min.y, vector.min.z);
            addVertex(vector.min.x, vector.max.y, vector.min.z);
            addVertex(vector.max.x, vector.max.y, vector.min.z);
            addVertex(vector.max.x, vector.min.y, vector.min.z);
        }

        // Draw south side
        if (renderFaces.length < 6 || renderFaces[5]) {
            GL11.glNormal3d(0, 0, 1);
            addVertex(vector.min.x, vector.min.y, vector.max.z);
            addVertex(vector.max.x, vector.min.y, vector.max.z);
            addVertex(vector.max.x, vector.max.y, vector.max.z);
            addVertex(vector.min.x, vector.max.y, vector.max.z);
        }

        GL11.glColor4d(1, 1, 1, 1);
    }

    public static void drawTesselatedColoredCube(Cuboid vector, int r, int g, int b, int a) {

        Tessellator t = Tessellator.getInstance();
        VertexBuffer buffer = t.getBuffer();
        boolean wasTesselating = false;

        // Check if we were already tesselating
        try {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        } catch (IllegalStateException e) {
            wasTesselating = true;

        }

        buffer.color(r, g, b, a);
        
        buffer.normal(0, 1, 0);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);

        // Bottom side
        buffer.normal(0, -1, 0);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);

        // Draw west side:
        buffer.normal(-1, 0, 0);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);

        // Draw east side:
        buffer.normal(1, 0, 0);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);

        // Draw north side
        buffer.normal(0, 0, -1);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);

        // Draw south side
        buffer.normal(0, 0, 1);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);

        GL11.glColor4d(1, 1, 1, 1);

        if (!wasTesselating) {
            t.draw();
        }
    }

    public static void drawTesselatedColoredCube(Cuboid vector) {

        Tessellator t = Tessellator.getInstance();
        VertexBuffer buffer = t.getBuffer();
        boolean wasTesselating = false;

        // Check if we were already tesselating
        try {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        } catch (IllegalStateException e) {
            wasTesselating = true;
        }

        // Top side
        buffer.color(1.0F, 0.0F, 0.0F, 1.0F);
        buffer.normal(0, 1, 0);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);

        // Bottom side
        buffer.color(1.0F, 1.0F, 0.0F, 1.0F);
        buffer.normal(0, -1, 0);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);

        // Draw west side:
        buffer.color(0.0F, 1.0F, 0.0F, 1.0F);
        buffer.normal(-1, 0, 0);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);

        // Draw east side:
        buffer.color(0.0F, 1.0F, 1.0F, 1.0F);
        buffer.normal(1, 0, 0);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);

        // Draw north side
        buffer.color(0.0F, 0.0F, 1.0F, 1.0F);
        buffer.normal(0, 0, -1);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);

        // Draw south side
        buffer.color(0.0F, 0.0F, 0.0F, 1.0F);
        buffer.normal(0, 0, 1);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);

        if (!wasTesselating) {
            t.draw();
        }
    }

    public static void drawTesselatedTexturedCube(Cuboid vector) {

        Tessellator t = Tessellator.getInstance();
        VertexBuffer buffer = t.getBuffer();
        boolean wasTesselating = false;

        // Check if we were already tesselating
        try {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        } catch (IllegalStateException e) {
            wasTesselating = true;
        }

        double minU = 0;
        double maxU = 1;
        double minV = 0;
        double maxV = 1;

        // Top side
        buffer.normal(0, 1, 0);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z).tex(minU, maxV);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z).tex(minU, minV);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z).tex(maxU, minV);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z).tex(maxU, maxV);

        // Bottom side
        buffer.normal(0, -1, 0);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z).tex(minU, maxV);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z).tex(minU, minV);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z).tex(maxU, minV);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z).tex(maxU, maxV);

        // Draw west side:
        buffer.normal(-1, 0, 0);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z).tex(minU, maxV);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z).tex(minU, minV);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z).tex(maxU, minV);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z).tex(maxU, maxV);

        // Draw east side:
        buffer.normal(1, 0, 0);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z).tex(minU, maxV);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z).tex(minU, minV);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z).tex(maxU, minV);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z).tex(maxU, maxV);

        // Draw north side
        buffer.normal(0, 0, -1);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z).tex(minU, maxV);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z).tex(minU, minV);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z).tex(maxU, minV);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z).tex(maxU, maxV);

        // Draw south side
        buffer.normal(0, 0, 1);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z).tex(minU, maxV);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z).tex(minU, minV);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z).tex(maxU, minV);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z).tex(maxU, maxV);

        if (!wasTesselating) {
            t.draw();
        }
    }

    public static void drawTesselatedCube(Cuboid vector) {

        Tessellator t = Tessellator.getInstance();
        VertexBuffer buffer = t.getBuffer();
        boolean wasTesselating = false;

        // Check if we were already tesselating
        try {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        } catch (IllegalStateException e) {
            wasTesselating = true;
        }

        // Top side
        buffer.normal(0, 1, 0);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);

        // Bottom side
        buffer.normal(0, -1, 0);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);

        // Draw west side:
        buffer.normal(-1, 0, 0);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);

        // Draw east side:
        buffer.normal(1, 0, 0);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);

        // Draw north side
        buffer.normal(0, 0, -1);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);

        // Draw south side
        buffer.normal(0, 0, 1);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);

        if (!wasTesselating) {
            t.draw();
        }
    }

    public static void drawTesselatedCubeWithoutNormals(Cuboid vector) {

        Tessellator t = Tessellator.getInstance();
        VertexBuffer buffer = t.getBuffer();
        boolean wasTesselating = false;

        // Check if we were already tessellating
        try {
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        } catch (IllegalStateException e) {
            wasTesselating = true;
        }

        // Top side
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);

        // Bottom side
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);

        // Draw west side:
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);

        // Draw east side:
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);

        // Draw north side
        buffer.pos(vector.min.x, vector.min.y, vector.min.z);
        buffer.pos(vector.min.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.max.y, vector.min.z);
        buffer.pos(vector.max.x, vector.min.y, vector.min.z);

        // Draw south side
        buffer.pos(vector.min.x, vector.min.y, vector.max.z);
        buffer.pos(vector.max.x, vector.min.y, vector.max.z);
        buffer.pos(vector.max.x, vector.max.y, vector.max.z);
        buffer.pos(vector.min.x, vector.max.y, vector.max.z);

        if (!wasTesselating) {
            t.draw();
        }
    }

}
