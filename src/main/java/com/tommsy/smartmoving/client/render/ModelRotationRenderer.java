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

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

import static com.tommsy.smartmoving.client.render.RenderUtils.RadianToAngle;

public class ModelRotationRenderer extends ModelRenderer {

    protected ModelRotationRenderer base;

    public boolean ignoreRender;
    public boolean forceRender;

    public RotationOrder rotationOrder;

    public float scaleX, scaleY, scaleZ;

    public boolean ignoreBase;
    public boolean ignoreSuperRotation;

    public static enum RotationOrder {
        XYZ, XZY, YXZ, YZX, ZXY, ZYX
    }

    public ModelRotationRenderer(ModelBase modelBase, int texOffX, int texOffY, ModelRotationRenderer baseRenderer) {
        super(modelBase, texOffX, texOffY);
        rotationOrder = RotationOrder.XYZ;
        compiled = false;

        base = baseRenderer;
        if (base != null)
            base.addChild(this);

        scaleX = scaleY = scaleZ = 1.0F;
    }

    public void copyFrom(ModelRenderer original) {
        if (original.childModels != null)
            for (Object childModel : original.childModels)
                this.addChild((ModelRenderer) childModel);
        if (original.cubeList != null)
            for (Object cube : original.cubeList)
                this.cubeList.add((ModelBox) cube);
        this.mirror = original.mirror;
        this.isHidden = original.isHidden;
        this.showModel = original.showModel;
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

    private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    private static float[] array = new float[16];

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

    private static void rotate(RotationOrder rotationOrder, float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
        if (rotationOrder == RotationOrder.ZXY && rotateAngleY != 0.0F)
            GL11.glRotatef(rotateAngleY * RadianToAngle, 0.0F, 1.0F, 0.0F);

        if (rotationOrder == RotationOrder.YXZ && rotateAngleZ != 0.0F)
            GL11.glRotatef(rotateAngleZ * RadianToAngle, 0.0F, 0.0F, 1.0F);

        if ((rotationOrder == RotationOrder.YZX || rotationOrder == RotationOrder.YXZ || rotationOrder == RotationOrder.ZXY || rotationOrder == RotationOrder.ZYX)
                && rotateAngleX != 0.0F)
            GL11.glRotatef(rotateAngleX * RadianToAngle, 1.0F, 0.0F, 0.0F);

        if ((rotationOrder == RotationOrder.XZY || rotationOrder == RotationOrder.ZYX)
                && rotateAngleY != 0.0F)
            GL11.glRotatef(rotateAngleY * RadianToAngle, 0.0F, 1.0F, 0.0F);

        if ((rotationOrder == RotationOrder.XYZ || rotationOrder == RotationOrder.XZY || rotationOrder == RotationOrder.YZX || rotationOrder == RotationOrder.ZXY
                || rotationOrder == RotationOrder.ZYX) && rotateAngleZ != 0.0F)
            GL11.glRotatef(rotateAngleZ * RadianToAngle, 0.0F, 0.0F, 1.0F);

        if ((rotationOrder == RotationOrder.XYZ || rotationOrder == RotationOrder.YXZ || rotationOrder == RotationOrder.YZX) && rotateAngleY != 0.0F)
            GL11.glRotatef(rotateAngleY * RadianToAngle, 0.0F, 1.0F, 0.0F);

        if ((rotationOrder == RotationOrder.XYZ || rotationOrder == RotationOrder.XZY) && rotateAngleX != 0.0F)
            GL11.glRotatef(rotateAngleX * RadianToAngle, 1.0F, 0.0F, 0.0F);
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
        rotationOrder = RotationOrder.XYZ;

        scaleX = scaleY = scaleZ = 1.0F;

        rotationPointX = rotationPointY = rotationPointZ = 0F;

        rotateAngleX = rotateAngleY = rotateAngleZ = 0F;

        ignoreBase = false;
        ignoreSuperRotation = false;
        forceRender = false;

        offsetX = offsetY = offsetZ = 0;
    }

    @Override
    public void postRender(float f) {
        if (!preRender(f))
            return;
        preTransforms(f, false, true);
    }

    public boolean canBeRandomBoxSource() {
        return true;
    }
}