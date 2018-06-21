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

import net.minecraft.client.model.ModelBase;

import static com.tommsy.smartmoving.client.render.RenderUtils.Half;
import static com.tommsy.smartmoving.client.render.RenderUtils.Whole;

public class ModelPreviousRotationRenderer extends ModelRotationRenderer {
    public final PreviousRendererData previous;

    public boolean fadeEnabled;

    public boolean fadeOffsetX, fadeOffsetY, fadeOffsetZ;
    public boolean fadeRotateAngleX, fadeRotateAngleY, fadeRotateAngleZ;
    public boolean fadeRotationPointX, fadeRotationPointY, fadeRotationPointZ;

    public static class PreviousRendererData {
        public float offsetX, offsetY, offsetZ;
        public float rotateAngleX, rotateAngleY, rotateAngleZ;
        public float rotationPointX, rotationPointY, rotationPointZ;
        public float totalTime = Float.MIN_VALUE;
    }

    public ModelPreviousRotationRenderer(ModelBase modelBase, int texOffX, int texOffY, ModelRotationRenderer baseRenderer) {
        super(modelBase, texOffX, texOffY, baseRenderer);
        previous = new PreviousRendererData();
    }

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

    @Override
    public void reset() {
        super.reset();
        fadeOffsetX = fadeOffsetY = fadeOffsetZ = false;
        fadeRotateAngleX = fadeRotateAngleY = fadeRotateAngleZ = false;
        fadeRotationPointX = fadeRotationPointY = fadeRotationPointZ = false;
        previous.offsetX = previous.offsetY = previous.offsetZ = 0;
        previous.rotateAngleX = previous.rotateAngleY = previous.rotateAngleZ = 0;
        previous.totalTime = Float.MIN_VALUE;
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
}
