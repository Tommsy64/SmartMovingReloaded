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

package com.tommsy.smartmoving.client.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;
import com.tommsy.smartmoving.client.renderer.ModelSpecialRenderer;
import com.tommsy.smartmoving.client.renderer.RenderUtils;
import com.tommsy.smartmoving.common.SmartMovingPlayerState;

@SideOnly(Side.CLIENT)
public class SmartMovingModelElytra extends ModelBase {

    private final ModelSpecialRenderer leftWing, rightWing;

    public SmartMovingModelElytra(SmartMovingModelPlayer smModel) {
        SmartMovingModelPlayerHandler handle = smModel.getHandler();

        this.leftWing = new ModelSpecialRenderer(this, 22, 0, handle.bipedBody);
        this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);

        this.rightWing = new ModelSpecialRenderer(this, 22, 0, handle.bipedBody);
        this.rightWing.mirror = true;
        this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        beforeRender();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableCull();

        if (((EntityLivingBase) entity).isChild()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 1.5F, -0.1F);
            this.leftWing.render(scale);
            this.rightWing.render(scale);
            GlStateManager.popMatrix();
        } else {
            this.leftWing.render(scale);
            this.rightWing.render(scale);
        }
        afterRender();
    }

    private void beforeRender() {
        this.leftWing.beforeRender(true);
        this.rightWing.beforeRender(true);
    }

    private void afterRender() {
        this.leftWing.afterRender();
        this.rightWing.afterRender();
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entity);
        float angleFromNeck = 0.2617994F;
        float wingSpread = -0.2617994F;
        float offsetFromNeck = 0.0F;
        float f3 = 0.0F;

        SmartMovingAbstractClientPlayer smPlayer = (SmartMovingAbstractClientPlayer) entity;
        SmartMovingPlayerState state = smPlayer.getState();

        if (((EntityLivingBase) entity).isElytraFlying()) {
            float f4 = 1.0F;

            if (entity.motionY < 0.0D) {
                Vec3d vec3d = (new Vec3d(entity.motionX, entity.motionY, entity.motionZ)).normalize();
                f4 = 1.0F - (float) Math.pow(-vec3d.y, 1.5D);
            }

            angleFromNeck = f4 * 0.34906584F + (1.0F - f4) * angleFromNeck;
            wingSpread = f4 * -((float) Math.PI / 2F) + (1.0F - f4) * wingSpread;
        } else if (state.isCrouching) {
            angleFromNeck = RenderUtils.Whole / 9F;
            wingSpread = -RenderUtils.Eighth;
            offsetFromNeck = 3.0F;
            f3 = RenderUtils.Half / 36F;
        } else if (state.isCrawling) {
            angleFromNeck = RenderUtils.Sixtyfourth;
            wingSpread = -RenderUtils.Sixteenth;
            offsetFromNeck = 3.0F;
            f3 = RenderUtils.Half / 36F;
        }

        this.leftWing.rotationPointX = 5.0F;
        this.leftWing.rotationPointY = offsetFromNeck;

        AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) entity;
        abstractclientplayer.rotateElytraX = (float) ((double) abstractclientplayer.rotateElytraX + (double) (angleFromNeck - abstractclientplayer.rotateElytraX) * 0.1D);
        abstractclientplayer.rotateElytraY = (float) ((double) abstractclientplayer.rotateElytraY + (double) (f3 - abstractclientplayer.rotateElytraY) * 0.1D);
        abstractclientplayer.rotateElytraZ = (float) ((double) abstractclientplayer.rotateElytraZ + (double) (wingSpread - abstractclientplayer.rotateElytraZ) * 0.1D);
        this.leftWing.rotateAngleX = abstractclientplayer.rotateElytraX;
        this.leftWing.rotateAngleY = abstractclientplayer.rotateElytraY;
        this.leftWing.rotateAngleZ = abstractclientplayer.rotateElytraZ;

        this.rightWing.rotationPointX = -this.leftWing.rotationPointX;
        this.rightWing.rotateAngleY = -this.leftWing.rotateAngleY;
        this.rightWing.rotationPointY = this.leftWing.rotationPointY;
        this.rightWing.rotateAngleX = this.leftWing.rotateAngleX;
        this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
    }
}
