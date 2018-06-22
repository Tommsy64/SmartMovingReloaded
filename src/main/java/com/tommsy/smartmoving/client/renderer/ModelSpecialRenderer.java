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

package com.tommsy.smartmoving.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;

public class ModelSpecialRenderer extends ModelRotationRenderer {
    public boolean doPopPush;

    public ModelSpecialRenderer(ModelBase modelBase, int texOffX, int texOffY, ModelRotationRenderer baseRenderer) {
        super(modelBase, texOffX, texOffY, baseRenderer);
        ignoreRender = true;
    }

    public void beforeRender(boolean popPush) {
        doPopPush = popPush;
        ignoreRender = false;
    }

    @Override
    public void doRender(float f, boolean useParentTransformations) {
        if (doPopPush) {
            GL11.glPopMatrix();
            GL11.glPushMatrix();
        }
        super.doRender(f, true);
    }

    public void afterRender() {
        ignoreRender = true;
        doPopPush = false;
    }
}