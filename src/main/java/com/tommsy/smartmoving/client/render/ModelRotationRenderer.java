/*
* Smart Moving Reloaded
* Copyright (C) 2018  Tommsy64
*
* Smart Moving Reloaded is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Smart Moving Reloaded is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Smart Moving Reloaded.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.tommsy.smartmoving.client.render;

import static com.tommsy.smartmoving.client.render.RenderUtils.Half;
import static com.tommsy.smartmoving.client.render.RenderUtils.RadiantToAngle;
import static com.tommsy.smartmoving.client.render.RenderUtils.Whole;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelRotationRenderer extends ModelRenderer {

    public ModelRotationRenderer(ModelBase modelBase, int texOffX, int texOffY, ModelRotationRenderer baseRenderer) {
        super(modelBase, texOffX, texOffY);
        rotationOrder = XYZ;
        compiled = false;

        base = baseRenderer;
        if (base != null)
            base.addChild(this);

        scaleX = scaleY = scaleZ = 1.0F;

        fadeEnabled = false;
    }

    @Override
    public void render(float f) {
        if ((!ignoreRender && !ignoreBase) || forceRender)
            doRender(f, ignoreBase);
    }

    public void renderIgnoreBase(float f) {
        if (ignoreBase)
            doRender(f, false);
    }

    public void doRender(float f, boolean useParentTransformations) {
        if (!preRender(f))
            return;
        preTransforms(f, true, useParentTransformations);
        GL11.glCallList(displayList);
        if (childModels != null)
            for (int i = 0; i < childModels.size(); i++)
                childModels.get(i).render(f);
        postTransforms(f, true, useParentTransformations);
    }

    private boolean preRender(float f) {
        if (isHidden || !showModel)
            return false;
        if (!compiled)
            this.compileDisplayList(f);
        return true;
    }

    public void preTransforms(float f, boolean push, boolean useParentTransformations) {
        if (base != null && !ignoreBase && useParentTransformations)
            base.preTransforms(f, push, true);
        preTransform(f, push);
    }

    public void preTransform(float f, boolean push) {
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F || ignoreSuperRotation) {
            if (push)
                GL11.glPushMatrix();

            GL11.glTranslatef(rotationPointX * f, rotationPointY * f, rotationPointZ * f);

            if (ignoreSuperRotation) {
                buffer.rewind();
                GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
                buffer.get(array);

                GL11.glLoadIdentity();
                GL11.glTranslatef(array[12] / array[15], array[13] / array[15], array[14] / array[15]);
            }

            rotate(rotationOrder, rotateAngleX, rotateAngleY, rotateAngleZ);

            GL11.glScalef(scaleX, scaleY, scaleZ);
            GL11.glTranslatef(offsetX, offsetY, offsetZ);
        } else if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F || scaleX != 1.0F || scaleY != 1.0F || scaleZ != 1.0F || offsetX != 0.0F
                || offsetY != 0.0F || offsetZ != 0.0F) {
            GL11.glTranslatef(rotationPointX * f, rotationPointY * f, rotationPointZ * f);
            GL11.glScalef(scaleX, scaleY, scaleZ);
            GL11.glTranslatef(offsetX, offsetY, offsetZ);
        }
    }

    private static void rotate(int rotationOrder, float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
        if ((rotationOrder == ZXY) && rotateAngleY != 0.0F)
            GL11.glRotatef(rotateAngleY * RadiantToAngle, 0.0F, 1.0F, 0.0F);

        if ((rotationOrder == YXZ) && rotateAngleZ != 0.0F)
            GL11.glRotatef(rotateAngleZ * RadiantToAngle, 0.0F, 0.0F, 1.0F);

        if ((rotationOrder == YZX || rotationOrder == YXZ || rotationOrder == ZXY || rotationOrder == ZYX) && rotateAngleX != 0.0F)
            GL11.glRotatef(rotateAngleX * RadiantToAngle, 1.0F, 0.0F, 0.0F);

        if ((rotationOrder == XZY || rotationOrder == ZYX) && rotateAngleY != 0.0F)
            GL11.glRotatef(rotateAngleY * RadiantToAngle, 0.0F, 1.0F, 0.0F);

        if ((rotationOrder == XYZ || rotationOrder == XZY || rotationOrder == YZX || rotationOrder == ZXY || rotationOrder == ZYX) && rotateAngleZ != 0.0F)
            GL11.glRotatef(rotateAngleZ * RadiantToAngle, 0.0F, 0.0F, 1.0F);

        if ((rotationOrder == XYZ || rotationOrder == YXZ || rotationOrder == YZX) && rotateAngleY != 0.0F)
            GL11.glRotatef(rotateAngleY * RadiantToAngle, 0.0F, 1.0F, 0.0F);

        if ((rotationOrder == XYZ || rotationOrder == XZY) && rotateAngleX != 0.0F)
            GL11.glRotatef(rotateAngleX * RadiantToAngle, 1.0F, 0.0F, 0.0F);
    }

    public void postTransform(float f, boolean pop) {
        if (rotateAngleX != 0.0F || rotateAngleY != 0.0F || rotateAngleZ != 0.0F || ignoreSuperRotation) {
            if (pop)
                GL11.glPopMatrix();
        } else if (rotationPointX != 0.0F || rotationPointY != 0.0F || rotationPointZ != 0.0F || scaleX != 1.0F || scaleY != 1.0F || scaleZ != 1.0F || offsetX != 0.0F
                || offsetY != 0.0F || offsetZ != 0.0F) {
            GL11.glTranslatef(-offsetX, -offsetY, -offsetZ);
            GL11.glScalef(1F / scaleX, 1F / scaleY, 1F / scaleZ);
            GL11.glTranslatef(-rotationPointX * f, -rotationPointY * f, -rotationPointZ * f);
        }
    }

    public void postTransforms(float f, boolean pop, boolean useParentTransformations) {
        postTransform(f, pop);
        if (base != null && !ignoreBase && useParentTransformations)
            base.postTransforms(f, pop, true);
    }

    public void reset() {
        rotationOrder = XYZ;

        scaleX = 1.0F;
        scaleY = 1.0F;
        scaleZ = 1.0F;

        rotationPointX = 0F;
        rotationPointY = 0F;
        rotationPointZ = 0F;

        rotateAngleX = 0F;
        rotateAngleY = 0F;
        rotateAngleZ = 0F;

        ignoreBase = false;
        ignoreSuperRotation = false;
        forceRender = false;

        offsetX = 0;
        offsetY = 0;
        offsetZ = 0;

        fadeOffsetX = false;
        fadeOffsetY = false;
        fadeOffsetZ = false;
        fadeRotateAngleX = false;
        fadeRotateAngleY = false;
        fadeRotateAngleZ = false;
        fadeRotationPointX = false;
        fadeRotationPointY = false;
        fadeRotationPointZ = false;

        previous = null;
    }

    @Override
    public void postRender(float f) {
        if (!preRender(f))
            return;
        preTransforms(f, false, true);
    }

    protected ModelRotationRenderer base;

    public boolean ignoreRender;
    public boolean forceRender;

    public int rotationOrder;

    public float scaleX, scaleY, scaleZ;

    public boolean ignoreBase;
    public boolean ignoreSuperRotation;

    public static final int XYZ = 0;
    public static final int XZY = 1;
    public static final int YXZ = 2;
    public static final int YZX = 3;
    public static final int ZXY = 4;
    public static final int ZYX = 5;

    public boolean fadeEnabled;

    public boolean fadeOffsetX;
    public boolean fadeOffsetY;
    public boolean fadeOffsetZ;
    public boolean fadeRotateAngleX;
    public boolean fadeRotateAngleY;
    public boolean fadeRotateAngleZ;
    public boolean fadeRotationPointX;
    public boolean fadeRotationPointY;
    public boolean fadeRotationPointZ;

    public RendererData previous;

    public void fadeStore(float totalTime) {
        if (previous != null) {
            previous.offsetX = offsetX;
            previous.offsetY = offsetY;
            previous.offsetZ = offsetZ;
            previous.rotateAngleX = rotateAngleX;
            previous.rotateAngleY = rotateAngleY;
            previous.rotateAngleZ = rotateAngleZ;
            previous.rotationPointX = rotationPointX;
            previous.rotationPointY = rotationPointY;
            previous.rotationPointZ = rotationPointZ;
            previous.totalTime = totalTime;
        }
    }

    public void fadeIntermediate(float totalTime) {
        if (previous != null && totalTime - previous.totalTime <= 2F) {
            offsetX = getIntermediatePosition(previous.offsetX, offsetX, fadeOffsetX, previous.totalTime, totalTime);
            offsetY = getIntermediatePosition(previous.offsetY, offsetY, fadeOffsetY, previous.totalTime, totalTime);
            offsetZ = getIntermediatePosition(previous.offsetZ, offsetZ, fadeOffsetZ, previous.totalTime, totalTime);

            rotateAngleX = getIntermediateAngle(previous.rotateAngleX, rotateAngleX, fadeRotateAngleX, previous.totalTime, totalTime);
            rotateAngleY = getIntermediateAngle(previous.rotateAngleY, rotateAngleY, fadeRotateAngleY, previous.totalTime, totalTime);
            rotateAngleZ = getIntermediateAngle(previous.rotateAngleZ, rotateAngleZ, fadeRotateAngleZ, previous.totalTime, totalTime);

            rotationPointX = getIntermediatePosition(previous.rotationPointX, rotationPointX, fadeRotationPointX, previous.totalTime, totalTime);
            rotationPointY = getIntermediatePosition(previous.rotationPointY, rotationPointY, fadeRotationPointY, previous.totalTime, totalTime);
            rotationPointZ = getIntermediatePosition(previous.rotationPointZ, rotationPointZ, fadeRotationPointZ, previous.totalTime, totalTime);
        }
    }

    public boolean canBeRandomBoxSource() {
        return true;
    }

    private static float getIntermediatePosition(float prevPosition, float shouldPosition, boolean fade, float lastTotalTime, float totalTime) {
        if (!fade || shouldPosition == prevPosition)
            return shouldPosition;

        return prevPosition + (shouldPosition - prevPosition) * (totalTime - lastTotalTime) * 0.2F;
    }

    private static float getIntermediateAngle(float prevAngle, float shouldAngle, boolean fade, float lastTotalTime, float totalTime) {
        if (!fade || shouldAngle == prevAngle)
            return shouldAngle;

        while (prevAngle >= Whole)
            prevAngle -= Whole;
        while (prevAngle < 0F)
            prevAngle += Whole;

        while (shouldAngle >= Whole)
            shouldAngle -= Whole;
        while (shouldAngle < 0F)
            shouldAngle += Whole;

        if (shouldAngle > prevAngle && (shouldAngle - prevAngle) > Half)
            prevAngle += Whole;

        if (shouldAngle < prevAngle && (prevAngle - shouldAngle) > Half)
            shouldAngle += Whole;

        return prevAngle + (shouldAngle - prevAngle) * (totalTime - lastTotalTime) * 0.2F;
    }

    private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    private static float[] array = new float[16];
}