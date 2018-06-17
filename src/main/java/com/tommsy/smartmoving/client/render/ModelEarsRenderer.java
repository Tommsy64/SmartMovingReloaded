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

import static com.tommsy.smartmoving.client.render.RenderUtils.RadiantToAngle;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;

public class ModelEarsRenderer extends ModelSpecialRenderer {
    private int _i = 0;
    private EntityPlayer entityplayer;

    public ModelEarsRenderer(ModelBase modelBase, int texOffX, int texOffY, ModelRotationRenderer baseRenderer) {
        super(modelBase, texOffX, texOffY, baseRenderer);
    }

    public void beforeRender(EntityPlayer entityplayer) {
        super.beforeRender(true);
        this.entityplayer = entityplayer;
    }

    @Override
    public void doRender(float f, boolean useParentTransformations) {
        reset();
        super.doRender(f, useParentTransformations);
    }

    @Override
    public void preTransform(float factor, boolean push) {
        if (entityplayer.isSneaking())
            GL11.glTranslated(0.0F, 0.2F * (entityplayer instanceof EntityPlayerSP ? Math.cos(entityplayer.rotationPitch / RadiantToAngle) : 1), 0.0F);

        super.preTransform(factor, push);

        int i = _i++ % 2;
        GL11.glTranslatef(0.375F * (i * 2 - 1), 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.375F, 0.0F);
        GL11.glScalef(1.333333F, 1.333333F, 1.333333F);
    }

    @Override
    public boolean canBeRandomBoxSource() {
        return false;
    }
}