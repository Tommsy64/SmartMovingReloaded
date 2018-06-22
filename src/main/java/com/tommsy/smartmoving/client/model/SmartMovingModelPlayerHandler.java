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

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;

import com.tommsy.smartmoving.client.renderer.ModelCapeRenderer;
import com.tommsy.smartmoving.client.renderer.ModelEarsRenderer;
import com.tommsy.smartmoving.client.renderer.ModelRotationRenderer;

import static com.tommsy.smartmoving.client.renderer.RenderUtils.Sixtyfourth;

public class SmartMovingModelPlayerHandler extends SmartMovingModelBipedHandler {

    // These are the same object, just different types
    private final SmartMovingModelPlayer smModel;
    private final ModelPlayer model;

    protected ModelEarsRenderer bipedEars;
    protected ModelCapeRenderer bipedCape;

    protected ModelRotationRenderer bipedBodywear;
    protected ModelRotationRenderer bipedRightArmwear;
    protected ModelRotationRenderer bipedLeftArmwear;
    protected ModelRotationRenderer bipedRightLegwear;
    protected ModelRotationRenderer bipedLeftLegwear;

    public SmartMovingModelPlayerHandler(SmartMovingModelPlayer smModel) {
        super(smModel, false);
        model = (this.smModel = smModel).getImplementation();
        this.smallArmsRotationPointY = model.smallArms ? 2.5F : 2.0F;
    }

    @Override
    protected void initializeRenderers() {
        super.initializeRenderers();
        bipedBodywear = new ModelRotationRenderer(model, bipedBody, model.bipedBodyWear);
        bipedRightArmwear = new ModelRotationRenderer(model, bipedRightArm, model.bipedRightArmwear);
        bipedLeftArmwear = new ModelRotationRenderer(model, bipedLeftArm, model.bipedLeftArmwear);
        bipedRightLegwear = new ModelRotationRenderer(model, bipedRightLeg, model.bipedRightLegwear);
        bipedLeftLegwear = new ModelRotationRenderer(model, bipedLeftLeg, model.bipedLeftLegwear);

        bipedCape = new ModelCapeRenderer(model, 0, 0, bipedBreast, bipedOuter);
        bipedCape.copyFrom(model.bipedCape);
        bipedEars = new ModelEarsRenderer(model, 24, 0, bipedHead);
        bipedEars.copyFrom(model.bipedDeadmau5Head);

        model.bipedBodyWear = bipedBodywear;
        model.bipedRightArmwear = bipedRightArmwear;
        model.bipedLeftArmwear = bipedLeftArmwear;
        model.bipedRightLegwear = bipedRightLegwear;
        model.bipedLeftLegwear = bipedLeftLegwear;

        model.bipedCape = bipedCape;
        bipedCape.ignoreBase = false;
        bipedCape.rotateAngleX = Sixtyfourth;

        model.bipedDeadmau5Head = bipedEars;
    }

    public void preRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.preRender(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        bipedBodywear.ignoreRender = bipedRightArmwear.ignoreRender = bipedLeftArmwear.ignoreRender = bipedRightLegwear.ignoreRender = bipedLeftLegwear.ignoreRender = true;
    }

    public void postRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.postRender(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        bipedBodywear.ignoreRender = bipedRightArmwear.ignoreRender = bipedLeftArmwear.ignoreRender = bipedRightLegwear.ignoreRender = bipedLeftLegwear.ignoreRender = false;

        bipedBodywear.renderIgnoreBase(scale);
        bipedRightArmwear.renderIgnoreBase(scale);
        bipedLeftArmwear.renderIgnoreBase(scale);
        bipedRightLegwear.renderIgnoreBase(scale);
        bipedLeftLegwear.renderIgnoreBase(scale);

        GL11.glPopMatrix();
    }

    @Override
    public boolean preSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor,
            Entity entity) {
        boolean cancel = super.preSetRotationAngles(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor, entity);
        // Cancel is false when the player model is being rendered in the inventory GUI
        if (!cancel) {
            bipedBodywear.ignoreBase = true;
            bipedHeadwear.ignoreBase = true;
            bipedRightArmwear.ignoreBase = true;
            bipedLeftArmwear.ignoreBase = true;
            bipedRightLegwear.ignoreBase = true;
            bipedLeftLegwear.ignoreBase = true;

            bipedEars.ignoreBase = true;
            bipedCape.ignoreBase = true;

            bipedBodywear.forceRender = false;
            bipedHeadwear.forceRender = false;
            bipedRightArmwear.forceRender = false;
            bipedLeftArmwear.forceRender = false;
            bipedRightLegwear.forceRender = false;
            bipedLeftLegwear.forceRender = false;

            bipedEars.forceRender = false;
            bipedCape.forceRender = false;

            return false;
        }

        return true;
    }

    @Override
    public void postSetRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.postSetRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
    }

    private final float smallArmsRotationPointY;

    @Override
    public void reset() {
        bipedOuter.reset();
        bipedTorso.reset();
        bipedBody.reset();
        bipedBreast.reset();
        bipedNeck.reset();
        bipedHead.reset();
        bipedRightShoulder.reset();
        bipedRightArm.reset();
        bipedLeftShoulder.reset();
        bipedLeftArm.reset();
        bipedPelvic.reset();
        bipedRightLeg.reset();
        bipedLeftLeg.reset();

        bipedBodywear.reset();
        bipedRightArmwear.reset();
        bipedLeftArmwear.reset();
        bipedRightLegwear.reset();
        bipedLeftLegwear.reset();

        bipedEars.reset();
        bipedCape.reset();

        bipedRightShoulder.setRotationPoint(-5F, smallArmsRotationPointY, 0.0F);
        bipedLeftShoulder.setRotationPoint(5F, smallArmsRotationPointY, 0.0F);
        bipedPelvic.setRotationPoint(0.0F, 12.0F, 0.1F);
        bipedRightLeg.setRotationPoint(-1.9F, 0.0F, 0.0F);
        bipedLeftLeg.setRotationPoint(1.9F, 0.0F, 0.0F);

        bipedCape.setRotationPoint(0.0F, 0.0F, 2.0F);
    }
}
