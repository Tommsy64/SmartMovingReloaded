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

package com.tommsy.smartmoving.mixin.client;

import static com.tommsy.smartmoving.client.render.RenderUtils.Eighth;
import static com.tommsy.smartmoving.client.render.RenderUtils.Half;
import static com.tommsy.smartmoving.client.render.RenderUtils.Quarter;
import static com.tommsy.smartmoving.client.render.RenderUtils.RadiantToAngle;
import static com.tommsy.smartmoving.client.render.RenderUtils.Sixteenth;
import static com.tommsy.smartmoving.client.render.RenderUtils.Sixtyfourth;
import static com.tommsy.smartmoving.client.render.RenderUtils.Thirtytwoth;
import static com.tommsy.smartmoving.client.render.RenderUtils.Whole;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tommsy.smartmoving.SmartMovingMod;
import com.tommsy.smartmoving.client.AbstractSmartMovingClientPlayerHandler.SmartMovingRenderState;
import com.tommsy.smartmoving.client.SmartMovingClient;
import com.tommsy.smartmoving.client.model.SmartMovingModelPlayer;
import com.tommsy.smartmoving.client.render.FeetClimbing;
import com.tommsy.smartmoving.client.render.HandsClimbing;
import com.tommsy.smartmoving.client.render.ModelCapeRenderer;
import com.tommsy.smartmoving.client.render.ModelEarsRenderer;
import com.tommsy.smartmoving.client.render.ModelRotationRenderer;
import com.tommsy.smartmoving.client.render.RendererData;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(ModelPlayer.class)
public abstract class MixinModelPlayer extends MixinModelBiped implements SmartMovingModelPlayer {

    @Shadow
    @Final
    private boolean smallArms;

    @Shadow
    public ModelRenderer bipedLeftArmwear;
    @Shadow
    public ModelRenderer bipedRightArmwear;
    @Shadow
    public ModelRenderer bipedLeftLegwear;
    @Shadow
    public ModelRenderer bipedRightLegwear;
    @Shadow
    public ModelRenderer bipedBodyWear;

    @Shadow
    @Final
    private ModelRenderer bipedCape;
    @Shadow
    @Final
    private ModelRenderer bipedDeadmau5Head;

    private ModelRotationRenderer smBipedOuter;
    private ModelRotationRenderer smBipedTorso;
    private ModelRotationRenderer smBipedBody;
    private ModelRotationRenderer smBipedBreast;
    private ModelRotationRenderer smBipedNeck;
    private ModelRotationRenderer smBipedHead;
    private ModelRotationRenderer smBipedRightShoulder;
    private ModelRotationRenderer smBipedRightArm;
    private ModelRotationRenderer smBipedLeftShoulder;
    private ModelRotationRenderer smBipedLeftArm;
    private ModelRotationRenderer smBipedPelvic;
    private ModelRotationRenderer smBipedRightLeg;
    private ModelRotationRenderer smBipedLeftLeg;

    private ModelRotationRenderer smBipedBodywear;
    private ModelRotationRenderer smBipedHeadwear;
    private ModelRotationRenderer smBipedRightArmwear;
    private ModelRotationRenderer smBipedLeftArmwear;
    private ModelRotationRenderer smBipedRightLegwear;
    private ModelRotationRenderer smBipedLeftLegwear;

    private ModelEarsRenderer smBipedEars;
    private ModelCapeRenderer smBipedCape;

    @Override
    public ModelEarsRenderer getBipedEars() {
        return this.smBipedEars;
    }

    @Override
    public ModelCapeRenderer getBipedCape() {
        return this.smBipedCape;
    }

    @Setter
    private boolean isSleeping;
    private boolean firstPerson;
    @Setter
    private boolean isInventory;

    @Setter
    private RendererData prevOuterRenderData;

    @Setter
    private float totalVerticalDistance;
    @Setter
    private float currentVerticalSpeed;
    @Setter
    private float totalDistance;
    @Setter
    private float currentSpeed;

    @Setter
    private double distance;
    @Setter
    private double verticalDistance;
    @Setter
    private double horizontalDistance;
    @Setter
    @Getter
    private float currentCameraAngle;
    @Setter
    private float currentVerticalAngle;
    @Setter
    private float currentHorizontalAngle;

    @Setter
    private float rotationYaw;
    @Setter
    private float forwardRotation;
    @Setter
    private float workingAngle;

    // ======
    // SmartMovingModel
    // ======

    public boolean isStandard;

    @Setter
    private SmartMovingRenderState renderState;
    @Setter
    private float currentHorizontalSpeedFlattened;
    @Setter
    private float smallOverGroundHeight;
    @Setter
    private Block overGroundBlock;

    public int scaleArmType;
    public int scaleLegType;

    // TODO
    private final boolean isModelPlayer = true;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructed(float modelSize, boolean useSmallArms, CallbackInfo ci) {
        this.boxList.clear();

        this.renderState = new SmartMovingRenderState();

        smBipedOuter = create(null);
        smBipedOuter.fadeEnabled = true;

        smBipedTorso = create(smBipedOuter);
        smBipedBody = create(smBipedTorso, this.bipedBody);
        smBipedBreast = create(smBipedTorso);
        smBipedNeck = create(smBipedBreast);
        smBipedHead = create(smBipedNeck, this.bipedHead);
        smBipedRightShoulder = create(smBipedBreast);
        smBipedRightArm = create(smBipedRightShoulder, this.bipedRightArm);
        smBipedLeftShoulder = create(smBipedBreast);
        smBipedLeftShoulder.mirror = true;
        smBipedLeftArm = create(smBipedLeftShoulder, this.bipedLeftArm);
        smBipedPelvic = create(smBipedTorso);
        smBipedRightLeg = create(smBipedPelvic, this.bipedRightLeg);
        smBipedLeftLeg = create(smBipedPelvic, this.bipedLeftLeg);

        smBipedBodywear = create(smBipedBody, this.bipedBodyWear);
        smBipedHeadwear = create(smBipedHead, this.bipedHeadwear);
        smBipedRightArmwear = create(smBipedRightArm, this.bipedRightArmwear);
        smBipedLeftArmwear = create(smBipedLeftArm, this.bipedLeftArmwear);
        smBipedRightLegwear = create(smBipedRightLeg, this.bipedRightLegwear);
        smBipedLeftLegwear = create(smBipedLeftLeg, this.bipedLeftLegwear);

        if (this.bipedCape != null) {
            smBipedCape = new ModelCapeRenderer(this, 0, 0, smBipedBreast, smBipedOuter);
            copy(smBipedCape, this.bipedCape);
        }

        if (this.bipedDeadmau5Head != null) {
            smBipedEars = new ModelEarsRenderer(this, 24, 0, smBipedHead);
            copy(smBipedEars, this.bipedDeadmau5Head);
        }

        reset(); // set default rotation points

        this.bipedBody = smBipedBody;
        this.bipedHead = smBipedHead;
        this.bipedRightArm = smBipedRightArm;
        this.bipedLeftArm = smBipedLeftArm;
        this.bipedRightLeg = smBipedRightLeg;
        this.bipedLeftLeg = smBipedLeftLeg;

        if (isModelPlayer) {
            this.bipedBodyWear = smBipedBodywear;
            this.bipedHeadwear = smBipedHeadwear;
            this.bipedRightArmwear = smBipedRightArmwear;
            this.bipedLeftArmwear = smBipedLeftArmwear;
            this.bipedRightLegwear = smBipedRightLegwear;
            this.bipedLeftLegwear = smBipedLeftLegwear;
        }

        SmartMovingClient.modelBipedInstances.add(this);
    }

    private ModelRotationRenderer create(ModelRotationRenderer base) {
        return new ModelRotationRenderer(this, -1, -1, base);
    }

    private ModelRotationRenderer create(ModelRotationRenderer base, ModelRenderer original) {
        if (original == null)
            return null;

        ModelRotationRenderer local = new ModelRotationRenderer(this, original.textureOffsetX, original.textureOffsetY, base);
        copy(local, original);
        return local;
    }

    private static void copy(ModelRotationRenderer local, ModelRenderer original) {
        if (original.childModels != null)
            for (Object childModel : original.childModels)
                local.addChild((ModelRenderer) childModel);
        if (original.cubeList != null)
            for (Object cube : original.cubeList)
                local.cubeList.add((ModelBox) cube);
        local.mirror = original.mirror;
        local.isHidden = original.isHidden;
        local.showModel = original.showModel;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void preRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        GL11.glPushMatrix();
        if (entity.isSneaking())
            GL11.glTranslatef(0.0F, 0.2F, 0.0F);

        smBipedBody.ignoreRender = smBipedHead.ignoreRender = smBipedRightArm.ignoreRender = smBipedLeftArm.ignoreRender = smBipedRightLeg.ignoreRender = smBipedLeftLeg.ignoreRender = true;
        if (isModelPlayer)
            smBipedBodywear.ignoreRender = smBipedHeadwear.ignoreRender = smBipedRightArmwear.ignoreRender = smBipedLeftArmwear.ignoreRender = smBipedRightLegwear.ignoreRender = smBipedLeftLegwear.ignoreRender = true;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (isModelPlayer)
            smBipedBodywear.ignoreRender = smBipedHeadwear.ignoreRender = smBipedRightArmwear.ignoreRender = smBipedLeftArmwear.ignoreRender = smBipedRightLegwear.ignoreRender = smBipedLeftLegwear.ignoreRender = false;
        smBipedBody.ignoreRender = smBipedHead.ignoreRender = smBipedRightArm.ignoreRender = smBipedLeftArm.ignoreRender = smBipedRightLeg.ignoreRender = smBipedLeftLeg.ignoreRender = false;

        smBipedOuter.render(scale);

        smBipedOuter.renderIgnoreBase(scale);
        smBipedTorso.renderIgnoreBase(scale);
        smBipedBody.renderIgnoreBase(scale);
        smBipedBreast.renderIgnoreBase(scale);
        smBipedNeck.renderIgnoreBase(scale);
        smBipedHead.renderIgnoreBase(scale);
        smBipedRightShoulder.renderIgnoreBase(scale);
        smBipedRightArm.renderIgnoreBase(scale);
        smBipedLeftShoulder.renderIgnoreBase(scale);
        smBipedLeftArm.renderIgnoreBase(scale);
        smBipedPelvic.renderIgnoreBase(scale);
        smBipedRightLeg.renderIgnoreBase(scale);
        smBipedLeftLeg.renderIgnoreBase(scale);

        if (isModelPlayer) {
            smBipedBodywear.renderIgnoreBase(scale);
            smBipedHeadwear.renderIgnoreBase(scale);
            smBipedRightArmwear.renderIgnoreBase(scale);
            smBipedLeftArmwear.renderIgnoreBase(scale);
            smBipedRightLegwear.renderIgnoreBase(scale);
            smBipedLeftLegwear.renderIgnoreBase(scale);
        }

        GL11.glPopMatrix();
    }

    @Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
    private void preSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor,
            Entity entity, CallbackInfo ci) {
        reset();

        if (firstPerson || isInventory) {
            smBipedBody.ignoreBase = true;
            smBipedHead.ignoreBase = true;
            smBipedRightArm.ignoreBase = true;
            smBipedLeftArm.ignoreBase = true;
            smBipedRightLeg.ignoreBase = true;
            smBipedLeftLeg.ignoreBase = true;

            if (isModelPlayer) {
                smBipedBodywear.ignoreBase = true;
                smBipedHeadwear.ignoreBase = true;
                smBipedRightArmwear.ignoreBase = true;
                smBipedLeftArmwear.ignoreBase = true;
                smBipedRightLegwear.ignoreBase = true;
                smBipedLeftLegwear.ignoreBase = true;

                smBipedEars.ignoreBase = true;
                smBipedCape.ignoreBase = true;
            }

            smBipedBody.forceRender = firstPerson;
            smBipedHead.forceRender = firstPerson;
            smBipedRightArm.forceRender = firstPerson;
            smBipedLeftArm.forceRender = firstPerson;
            smBipedRightLeg.forceRender = firstPerson;
            smBipedLeftLeg.forceRender = firstPerson;

            if (isModelPlayer) {
                smBipedBodywear.forceRender = firstPerson;
                smBipedHeadwear.forceRender = firstPerson;
                smBipedRightArmwear.forceRender = firstPerson;
                smBipedLeftArmwear.forceRender = firstPerson;
                smBipedRightLegwear.forceRender = firstPerson;
                smBipedLeftLegwear.forceRender = firstPerson;

                smBipedEars.forceRender = firstPerson;
                smBipedCape.forceRender = firstPerson;
            }

            smBipedRightArm.setRotationPoint(-5F, 2.0F, 0.0F);
            smBipedLeftArm.setRotationPoint(5F, 2.0F, 0.0F);
            smBipedRightLeg.setRotationPoint(-2F, 12F, 0.0F);
            smBipedLeftLeg.setRotationPoint(2.0F, 12F, 0.0F);

            return;
        }
        ci.cancel(); // Don't continue base setRotationAngles

        if (isSleeping) {
            prevOuterRenderData.rotateAngleX = 0;
            prevOuterRenderData.rotateAngleY = 0;
            prevOuterRenderData.rotateAngleZ = 0;
        }

        smBipedOuter.previous = prevOuterRenderData;

        smBipedOuter.rotateAngleY = rotationYaw / RadiantToAngle;
        smBipedOuter.fadeRotateAngleY = !entity.isRiding();

        // imp.animateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
        animateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);

        if (isSleeping)
            // imp.animateSleeping(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
            animateSleeping();

        // imp.animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
        animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);

        if (this.isRiding)
            // imp.animateRiding(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
            animateRiding();

        // TODO FIXME restore these
        // if(mp.heldItemLeft != 0)
        // imp.animateLeftArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        //
        // if(mp.heldItemRight != 0)
        // imp.animateRightArmItemHolding(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

        if (this.leftArmPose == ModelBiped.ArmPose.ITEM) // || == ModelBiped.ArmPose.BLOCK??
            this.animateLeftArmItemHolding();

        if (this.rightArmPose == ModelBiped.ArmPose.ITEM) // || == ModelBiped.ArmPose.BLOCK??
            this.animateRightArmItemHolding();

        if (this.swingProgress > -9990F) {
            // imp.animateWorkingBody(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
            animateWorkingBody(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
            animateWorkingArms();
            // imp.animateWorkingArms(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
        }

        if (this.isSneak)
            // imp.animateSneaking(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
            animateSneaking();

        // imp.animateArms(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);
        animateArms(ageInTicks);

        // TODO FIXME restore this
        // if (this.aimedBow)
        // imp.animateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

        if (this.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW && this.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW)
            animateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);

        if (smBipedOuter.previous != null && !smBipedOuter.fadeRotateAngleX)
            smBipedOuter.previous.rotateAngleX = smBipedOuter.rotateAngleX;

        if (smBipedOuter.previous != null && !smBipedOuter.fadeRotateAngleY)
            smBipedOuter.previous.rotateAngleY = smBipedOuter.rotateAngleY;

        smBipedOuter.fadeIntermediate(ageInTicks);
        smBipedOuter.fadeStore(ageInTicks);

        if (isModelPlayer) {
            smBipedCape.ignoreBase = false;
            smBipedCape.rotateAngleX = Sixtyfourth;
        }
    }

    private void setRotationAnglesSM(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset,
            float factor) {
        final float FrequenceFactor = 0.6662F;

        isStandard = false;

        float currentCameraAngle = this.currentCameraAngle;
        float currentHorizontalAngle = this.currentHorizontalAngle;
        float currentVerticalAngle = this.currentVerticalAngle;
        float forwardRotation = this.forwardRotation;
        float currentVerticalSpeed = this.currentVerticalSpeed;
        float totalVerticalDistance = this.totalVerticalDistance;
        float totalDistance = this.totalDistance;
        double horizontalDistance = this.horizontalDistance;
        float currentSpeed = this.currentSpeed;
        if (!Float.isNaN(currentHorizontalSpeedFlattened))
            currentHorizontalSpeed = currentHorizontalSpeedFlattened;

        ModelRotationRenderer bipedOuter = this.smBipedOuter;
        ModelRotationRenderer bipedTorso = this.smBipedTorso;
        ModelRotationRenderer bipedBody = this.smBipedBody;
        ModelRotationRenderer bipedBreast = this.smBipedBreast;
        ModelRotationRenderer bipedHead = this.smBipedHead;
        ModelRotationRenderer bipedRightShoulder = this.smBipedRightShoulder;
        ModelRotationRenderer bipedRightArm = this.smBipedRightArm;
        ModelRotationRenderer bipedLeftShoulder = this.smBipedLeftShoulder;
        ModelRotationRenderer bipedLeftArm = this.smBipedLeftArm;
        ModelRotationRenderer bipedPelvic = this.smBipedPelvic;
        ModelRotationRenderer bipedRightLeg = this.smBipedRightLeg;
        ModelRotationRenderer bipedLeftLeg = this.smBipedLeftLeg;

        if (renderState.climb || renderState.crawlClimb) {
            bipedOuter.rotateAngleY = forwardRotation / RadiantToAngle;

            bipedHead.rotateAngleY = 0.0F;
            bipedHead.rotateAngleX = viewVerticalAngelOffset / RadiantToAngle;

            bipedLeftLeg.rotationOrder = ModelRotationRenderer.YZX;
            bipedRightLeg.rotationOrder = ModelRotationRenderer.YZX;

            float handsFrequenceUpFactor, handsDistanceUpFactor, handsDistanceUpOffset, feetFrequenceUpFactor, feetDistanceUpFactor, feetDistanceUpOffset;
            float handsFrequenceSideFactor, handsDistanceSideFactor, handsDistanceSideOffset, feetFrequenceSideFactor, feetDistanceSideFactor, feetDistanceSideOffset;

            int handsClimbType = renderState.handsClimbType;
            if (renderState.handsVineClimb && handsClimbType == HandsClimbing.MiddleGrab)
                handsClimbType = HandsClimbing.UpGrab;

            float verticalSpeed = Math.min(0.5f, currentVerticalSpeed);
            float horizontalSpeed = Math.min(0.5f, currentHorizontalSpeed);

            switch (handsClimbType) {
            case HandsClimbing.MiddleGrab:
                handsFrequenceSideFactor = FrequenceFactor;
                handsDistanceSideFactor = 1.0F;
                handsDistanceSideOffset = 0.0F;

                handsFrequenceUpFactor = FrequenceFactor;
                handsDistanceUpFactor = 2F;
                handsDistanceUpOffset = -Quarter;
                break;
            case HandsClimbing.UpGrab:
                handsFrequenceSideFactor = FrequenceFactor;
                handsDistanceSideFactor = 1.0F;
                handsDistanceSideOffset = 0.0F;

                handsFrequenceUpFactor = FrequenceFactor;
                handsDistanceUpFactor = 2F;
                handsDistanceUpOffset = -2.5F;
                break;
            default:
                handsFrequenceSideFactor = FrequenceFactor;
                handsDistanceSideFactor = 1.0F;
                handsDistanceSideOffset = 0.0F;

                handsFrequenceUpFactor = FrequenceFactor;
                handsDistanceUpFactor = 0F;
                handsDistanceUpOffset = -0.5F;
                break;
            }

            switch (renderState.feetClimbType) {
            case HandsClimbing.UpGrab:
                feetFrequenceUpFactor = FrequenceFactor;
                feetDistanceUpFactor = 0.3F / verticalSpeed;
                feetDistanceUpOffset = -0.3F;

                feetFrequenceSideFactor = FrequenceFactor;
                feetDistanceSideFactor = 0.5F;
                feetDistanceSideOffset = 0.0F;
                break;
            default:
                feetFrequenceUpFactor = FrequenceFactor;
                feetDistanceUpFactor = 0.0F;
                feetDistanceUpOffset = 0.0F;

                feetFrequenceSideFactor = FrequenceFactor;
                feetDistanceSideFactor = 0.0F;
                feetDistanceSideOffset = 0.0F;
                break;
            }
            SmartMovingMod.logger.error("HIAZ999");
            bipedRightArm.rotateAngleX = MathHelper.cos(totalVerticalDistance * handsFrequenceUpFactor + Half) * verticalSpeed * handsDistanceUpFactor + handsDistanceUpOffset;
            bipedLeftArm.rotateAngleX = MathHelper.cos(totalVerticalDistance * handsFrequenceUpFactor) * verticalSpeed * handsDistanceUpFactor + handsDistanceUpOffset;

            bipedRightArm.rotateAngleY = MathHelper.cos(totalHorizontalDistance * handsFrequenceSideFactor + Quarter) * horizontalSpeed * handsDistanceSideFactor
                    + handsDistanceSideOffset;
            bipedLeftArm.rotateAngleY = MathHelper.cos(totalHorizontalDistance * handsFrequenceSideFactor) * horizontalSpeed * handsDistanceSideFactor + handsDistanceSideOffset;

            if (renderState.handsVineClimb) {
                bipedLeftArm.rotateAngleY *= 1F + handsFrequenceSideFactor;
                bipedRightArm.rotateAngleY *= 1F + handsFrequenceSideFactor;

                bipedLeftArm.rotateAngleY += Eighth;
                bipedRightArm.rotateAngleY -= Eighth;

                setArmScales(Math.abs(MathHelper.cos(bipedRightArm.rotateAngleX)), Math.abs(MathHelper.cos(bipedLeftArm.rotateAngleX)));
            }

            if (!renderState.feetVineClimb) {
                bipedRightLeg.rotateAngleX = MathHelper.cos(totalVerticalDistance * feetFrequenceUpFactor) * feetDistanceUpFactor * verticalSpeed + feetDistanceUpOffset;
                bipedLeftLeg.rotateAngleX = MathHelper.cos(totalVerticalDistance * feetFrequenceUpFactor + Half) * feetDistanceUpFactor * verticalSpeed + feetDistanceUpOffset;
            }

            bipedRightLeg.rotateAngleZ = -(MathHelper.cos(totalHorizontalDistance * feetFrequenceSideFactor) - 1.0F) * horizontalSpeed * feetDistanceSideFactor
                    + feetDistanceSideOffset;
            bipedLeftLeg.rotateAngleZ = -(MathHelper.cos(totalHorizontalDistance * feetFrequenceSideFactor + Quarter) + 1.0F) * horizontalSpeed * feetDistanceSideFactor
                    + feetDistanceSideOffset;

            if (renderState.feetVineClimb) {
                float total = (MathHelper.cos(totalDistance + Half) + 1) * Thirtytwoth + Sixteenth;
                bipedRightLeg.rotateAngleX = -total;
                bipedLeftLeg.rotateAngleX = -total;

                float difference = Math.max(0, MathHelper.cos(totalDistance - Quarter)) * Sixtyfourth;
                bipedLeftLeg.rotateAngleZ += -difference;
                bipedRightLeg.rotateAngleZ += difference;

                setLegScales(Math.abs(MathHelper.cos(bipedRightLeg.rotateAngleX)), Math.abs(MathHelper.cos(bipedLeftLeg.rotateAngleX)));
            }

            if (renderState.crawlClimb) {
                float height = smallOverGroundHeight + 0.25F;
                float bodyLength = 0.7F;
                float legLength = 0.55F;

                float bodyAngleX, legAngleX, legAngleZ;
                if (height < bodyLength) {
                    bodyAngleX = Math.max(0, (float) Math.acos(height / bodyLength));
                    legAngleX = Quarter - bodyAngleX;
                    legAngleZ = Thirtytwoth;
                } else if (height < bodyLength + legLength) {
                    bodyAngleX = 0F;
                    legAngleX = Math.max(0, (float) Math.acos((height - bodyLength) / legLength));
                    legAngleZ = Thirtytwoth * (legAngleX / 1.537F);
                } else {
                    bodyAngleX = 0F;
                    legAngleX = 0F;
                    legAngleZ = 0F;
                }

                bipedTorso.rotateAngleX = bodyAngleX;

                bipedRightShoulder.rotateAngleX = -bodyAngleX;
                bipedLeftShoulder.rotateAngleX = -bodyAngleX;

                bipedHead.rotateAngleX = -bodyAngleX;

                bipedRightLeg.rotateAngleX = legAngleX;
                bipedLeftLeg.rotateAngleX = legAngleX;

                bipedRightLeg.rotateAngleZ = legAngleZ;
                bipedLeftLeg.rotateAngleZ = -legAngleZ;
            }

            if (handsClimbType == HandsClimbing.NoGrab && renderState.feetClimbType != FeetClimbing.NoStep) {
                bipedTorso.rotateAngleX = 0.5F;
                bipedHead.rotateAngleX -= 0.5F;
                bipedPelvic.rotateAngleX -= 0.5F;

                bipedTorso.rotationPointZ = -6.0F;
            }
        } else if (renderState.climbJump) {
            bipedRightArm.rotateAngleX = Half + Sixteenth;
            bipedLeftArm.rotateAngleX = Half + Sixteenth;

            bipedRightArm.rotateAngleZ = -Thirtytwoth;
            bipedLeftArm.rotateAngleZ = Thirtytwoth;
        } else if (renderState.ceilingClimb) {
            float distance = totalHorizontalDistance * 0.7F;
            float walkFactor = factor(currentHorizontalSpeed, 0F, 0.12951545F);
            float standFactor = factor(currentHorizontalSpeed, 0.12951545F, 0F);
            float horizontalAngle = horizontalDistance < 0.015F ? currentCameraAngle : currentHorizontalAngle;

            bipedLeftArm.rotateAngleX = (MathHelper.cos(distance) * 0.52F + Half) * walkFactor + Half * standFactor;
            bipedRightArm.rotateAngleX = (MathHelper.cos(distance + Half) * 0.52F - Half) * walkFactor - Half * standFactor;

            bipedLeftLeg.rotateAngleX = -MathHelper.cos(distance) * 0.12F * walkFactor;
            bipedRightLeg.rotateAngleX = -MathHelper.cos(distance + Half) * 0.32F * walkFactor;

            float rotateY = MathHelper.cos(distance) * 0.44F * walkFactor;
            bipedOuter.rotateAngleY = rotateY + horizontalAngle;

            bipedRightArm.rotateAngleY = bipedLeftArm.rotateAngleY = -rotateY;
            bipedRightLeg.rotateAngleY = bipedLeftLeg.rotateAngleY = -rotateY;

            bipedHead.rotateAngleY = -rotateY;
        } else if (renderState.swim) {
            float distance = totalHorizontalDistance;
            float walkFactor = factor(currentHorizontalSpeed, 0.15679921F, 0.52264464F);
            float sneakFactor = Math.min(factor(currentHorizontalSpeed, 0, 0.15679921F), factor(currentHorizontalSpeed, 0.52264464F, 0.15679921F));
            float standFactor = factor(currentHorizontalSpeed, 0.15679921F, 0F);
            float standSneakFactor = standFactor + sneakFactor;
            float horizontalAngle = horizontalDistance < (renderState.genericSneak ? 0.005 : 0.015F) ? currentCameraAngle : currentHorizontalAngle;

            bipedHead.rotationOrder = ModelRotationRenderer.YXZ;
            bipedHead.rotateAngleY = MathHelper.cos(distance / 2.0F - Quarter) * walkFactor;
            bipedHead.rotateAngleX = -Eighth * standSneakFactor;
            bipedHead.rotationPointZ = -2F;

            bipedOuter.fadeRotateAngleX = true;
            bipedOuter.rotateAngleX = Quarter - Sixteenth * standSneakFactor;
            bipedOuter.rotateAngleY = horizontalAngle;

            bipedBreast.rotateAngleY = bipedBody.rotateAngleY = MathHelper.cos(distance / 2.0F - Quarter) * walkFactor;

            bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

            bipedRightArm.rotateAngleZ = Quarter + Eighth + MathHelper.cos(totalTime * 0.1F) * standSneakFactor * 0.8F;
            bipedLeftArm.rotateAngleZ = -Quarter - Eighth - MathHelper.cos(totalTime * 0.1F) * standSneakFactor * 0.8F;

            bipedRightArm.rotateAngleX = ((distance * 0.5F) % Whole - Half) * walkFactor + Sixteenth * standSneakFactor;
            bipedLeftArm.rotateAngleX = ((distance * 0.5F + Half) % Whole - Half) * walkFactor + Sixteenth * standSneakFactor;

            bipedRightLeg.rotateAngleX = MathHelper.cos(distance) * 0.52264464F * walkFactor;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(distance + Half) * 0.52264464F * walkFactor;

            float rotateFeetAngleZ = Sixteenth * standSneakFactor + MathHelper.cos(totalTime * 0.1F) * 0.4F * (standFactor - sneakFactor);
            bipedRightLeg.rotateAngleZ = rotateFeetAngleZ;
            bipedLeftLeg.rotateAngleZ = -rotateFeetAngleZ;

            if (scaleLegType != NoScaleStart)
                setLegScales(
                        1F + (MathHelper.cos(totalTime * 0.1F + Quarter) - 1F) * 0.15F * sneakFactor,
                        1F + (MathHelper.cos(totalTime * 0.1F + Quarter) - 1F) * 0.15F * sneakFactor);

            if (scaleArmType != NoScaleStart)
                setArmScales(
                        1F + (MathHelper.cos(totalTime * 0.1F - Quarter) - 1F) * 0.15F * sneakFactor,
                        1F + (MathHelper.cos(totalTime * 0.1F - Quarter) - 1F) * 0.15F * sneakFactor);
        } else if (renderState.dive) {
            float distance = totalDistance * 0.7F;
            float walkFactor = factor(currentSpeed, 0F, 0.15679921F);
            float standFactor = factor(currentSpeed, 0.15679921F, 0F);
            float horizontalAngle = totalDistance < (renderState.genericSneak ? 0.005 : 0.015F) ? currentCameraAngle : currentHorizontalAngle;

            bipedHead.rotateAngleX = -Eighth;
            bipedHead.rotationPointZ = -2F;

            bipedOuter.fadeRotateAngleX = true;
            bipedOuter.rotateAngleX = renderState.levitate ? Quarter - Sixteenth : (renderState.jump ? 0F : Quarter - currentVerticalAngle);
            bipedOuter.rotateAngleY = horizontalAngle;

            bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance) + 1F) * 0.52264464F * walkFactor + Sixteenth * standFactor;
            bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance + Half) - 1F) * 0.52264464F * walkFactor - Sixteenth * standFactor;

            if (scaleLegType != NoScaleStart)
                setLegScales(
                        1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor,
                        1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor);

            bipedRightArm.rotateAngleZ = (MathHelper.cos(distance + Half) * 0.52264464F * 2.5F + Quarter) * walkFactor + (Quarter + Eighth) * standFactor;
            bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * 0.52264464F * 2.5F - Quarter) * walkFactor - (Quarter + Eighth) * standFactor;

            if (scaleArmType != NoScaleStart)
                setArmScales(
                        1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor,
                        1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor);
        } else if (renderState.crawl) {
            float distance = totalHorizontalDistance * 1.3F;
            float walkFactor = factor(currentHorizontalSpeedFlattened, 0F, 0.12951545F);
            float standFactor = factor(currentHorizontalSpeedFlattened, 0.12951545F, 0F);

            bipedHead.rotateAngleZ = -viewHorizontalAngelOffset / RadiantToAngle;
            bipedHead.rotateAngleX = -Eighth;
            bipedHead.rotationPointZ = -2F;

            bipedTorso.rotationOrder = ModelRotationRenderer.YZX;
            bipedTorso.rotateAngleX = Quarter - Thirtytwoth;
            bipedTorso.rotationPointY = 3F;
            bipedTorso.rotateAngleZ = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor;
            bipedBody.rotateAngleY = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor;

            bipedRightLeg.rotateAngleX = (MathHelper.cos(distance - Quarter) * Sixtyfourth + Thirtytwoth) * walkFactor + Thirtytwoth * standFactor;
            bipedLeftLeg.rotateAngleX = (MathHelper.cos(distance - Half - Quarter) * Sixtyfourth + Thirtytwoth) * walkFactor + Thirtytwoth * standFactor;

            bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) + 1F) * 0.25F * walkFactor + Thirtytwoth * standFactor;
            bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor - Thirtytwoth * standFactor;

            if (scaleLegType != NoScaleStart)
                setLegScales(
                        1F + (MathHelper.cos(distance + Quarter - Quarter) - 1F) * 0.25F * walkFactor,
                        1F + (MathHelper.cos(distance - Quarter - Quarter) - 1F) * 0.25F * walkFactor);

            bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

            bipedRightArm.rotateAngleX = Half + Eighth;
            bipedLeftArm.rotateAngleX = Half + Eighth;

            bipedRightArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth + Thirtytwoth) * walkFactor + Sixteenth * standFactor;
            bipedLeftArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth - Thirtytwoth) * walkFactor - Sixteenth * standFactor;

            bipedRightArm.rotateAngleY = -Quarter;
            bipedLeftArm.rotateAngleY = Quarter;

            if (scaleArmType != NoScaleStart)
                setArmScales(
                        1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor,
                        1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.15F * walkFactor);
        } else if (renderState.slide) {
            float distance = totalHorizontalDistance * 0.7F;
            float walkFactor = factor(currentHorizontalSpeed, 0F, 1F) * 0.8F;

            bipedHead.rotateAngleZ = -viewHorizontalAngelOffset / RadiantToAngle;
            bipedHead.rotateAngleX = -Eighth - Sixteenth;
            bipedHead.rotationPointZ = -2F;

            bipedOuter.fadeRotateAngleY = false;
            bipedOuter.rotateAngleY = currentHorizontalAngle;
            bipedOuter.rotationPointY = 5F;
            bipedOuter.rotateAngleX = Quarter;

            bipedBody.rotationOrder = ModelRotationRenderer.YXZ;
            bipedBody.offsetY = -0.4F;
            bipedBody.rotationPointY = +6.5F;
            bipedBody.rotateAngleX = MathHelper.cos(distance - Eighth) * Sixtyfourth * walkFactor;
            bipedBody.rotateAngleY = MathHelper.cos(distance + Eighth) * Sixtyfourth * walkFactor;

            bipedRightLeg.rotateAngleX = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor + Sixtyfourth;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor + Sixtyfourth;

            bipedRightLeg.rotateAngleZ = Thirtytwoth;
            bipedLeftLeg.rotateAngleZ = -Thirtytwoth;

            bipedRightArm.rotationOrder = ModelRotationRenderer.YZX;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.YZX;

            bipedRightArm.rotateAngleX = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor + Half - Sixtyfourth;
            bipedLeftArm.rotateAngleX = MathHelper.cos(distance - Half) * Sixtyfourth * walkFactor + Half - Sixtyfourth;

            bipedRightArm.rotateAngleZ = Sixteenth;
            bipedLeftArm.rotateAngleZ = -Sixteenth;

            bipedRightArm.rotateAngleY = -Quarter;
            bipedLeftArm.rotateAngleY = Quarter;
        } else if (renderState.flying) {
            float distance = totalDistance * 0.08F;
            float walkFactor = factor(currentSpeed, 0F, 1);
            float standFactor = factor(currentSpeed, 1F, 0F);
            float time = totalTime * 0.15F;
            float verticalAngle = renderState.jump ? Math.abs(currentVerticalAngle) : currentVerticalAngle;
            float horizontalAngle = horizontalDistance < 0.05F ? currentCameraAngle : currentHorizontalAngle;

            bipedOuter.fadeRotateAngleX = true;
            bipedOuter.rotateAngleX = (Quarter - verticalAngle) * walkFactor;
            bipedOuter.rotateAngleY = horizontalAngle;

            bipedHead.rotateAngleX = -bipedOuter.rotateAngleX / 2F;

            bipedRightArm.rotationOrder = ModelRotationRenderer.XZY;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.XZY;

            bipedRightArm.rotateAngleY = (MathHelper.cos(time) * Sixteenth) * standFactor;
            bipedLeftArm.rotateAngleY = (MathHelper.cos(time) * Sixteenth) * standFactor;

            bipedRightArm.rotateAngleZ = (MathHelper.cos(distance + Half) * Sixtyfourth + (Half - Sixteenth)) * walkFactor + Quarter * standFactor;
            bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * Sixtyfourth - (Half - Sixteenth)) * walkFactor - Quarter * standFactor;

            bipedRightLeg.rotateAngleX = MathHelper.cos(distance) * Sixtyfourth * walkFactor + MathHelper.cos(time + Half) * Sixtyfourth * standFactor;
            bipedLeftLeg.rotateAngleX = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor + MathHelper.cos(time) * Sixtyfourth * standFactor;

            bipedRightLeg.rotateAngleZ = Sixtyfourth;
            bipedLeftLeg.rotateAngleZ = -Sixtyfourth;
        } else if (renderState.headJump) {
            bipedOuter.fadeRotateAngleX = true;
            bipedOuter.rotateAngleX = (Quarter - currentVerticalAngle);
            bipedOuter.rotateAngleY = currentHorizontalAngle;

            bipedHead.rotateAngleX = -bipedOuter.rotateAngleX / 2F;

            float bendFactor = Math.min(factor(currentVerticalAngle, Quarter, 0), factor(currentVerticalAngle, -Quarter, 0));
            bipedRightArm.rotateAngleX = bendFactor * -Eighth;
            bipedLeftArm.rotateAngleX = bendFactor * -Eighth;

            bipedRightLeg.rotateAngleX = bendFactor * -Eighth;
            bipedLeftLeg.rotateAngleX = bendFactor * -Eighth;

            float armFactorZ = factor(currentVerticalAngle, Quarter, -Quarter);
            if (overGroundBlock != null && overGroundBlock.getBlockState().getBaseState().getMaterial().isSolid())
                armFactorZ = Math.min(armFactorZ, smallOverGroundHeight / 5F);

            bipedRightArm.rotateAngleZ = Half - Sixteenth + armFactorZ * Eighth;
            bipedLeftArm.rotateAngleZ = Sixteenth - Half - armFactorZ * Eighth;

            float legFactorZ = factor(currentVerticalAngle, -Quarter, Quarter);
            bipedRightLeg.rotateAngleZ = Sixtyfourth * legFactorZ;
            bipedLeftLeg.rotateAngleZ = -Sixtyfourth * legFactorZ;
        } else if (renderState.falling) {
            float distance = totalDistance * 0.1F;

            bipedRightArm.rotationOrder = ModelRotationRenderer.XZY;
            bipedLeftArm.rotationOrder = ModelRotationRenderer.XZY;

            bipedRightArm.rotateAngleY = (MathHelper.cos(distance + Quarter) * Eighth);
            bipedLeftArm.rotateAngleY = (MathHelper.cos(distance + Quarter) * Eighth);

            bipedRightArm.rotateAngleZ = (MathHelper.cos(distance) * Eighth + Quarter);
            bipedLeftArm.rotateAngleZ = (MathHelper.cos(distance) * Eighth - Quarter);

            bipedRightLeg.rotateAngleX = (MathHelper.cos(distance + Half + Quarter) * Sixteenth + Thirtytwoth);
            bipedLeftLeg.rotateAngleX = (MathHelper.cos(distance + Quarter) * Sixteenth + Thirtytwoth);

            bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance) * Sixteenth + Thirtytwoth);
            bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance) * Sixteenth - Thirtytwoth);
        } else
            isStandard = true;
    }

    // @Inject(method = "setRotationAngles", at = @At("RETURN"))
    // public void postSetRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn,
    // CallbackInfo ci) {
    //
    // }

    private boolean isWorking() {
        return this.swingProgress > 0F;
    }

    private void animateAngleJumping() {
        float angle = renderState.angleJumpType * Eighth;
        this.smBipedPelvic.rotateAngleY -= this.smBipedOuter.rotateAngleY;
        this.smBipedPelvic.rotateAngleY += this.currentCameraAngle;

        float backness = 1F - Math.abs(angle - Half) / Quarter;
        float leftness = -Math.min(angle - Half, 0F) / Quarter;
        float rightness = Math.max(angle - Half, 0F) / Quarter;

        this.smBipedLeftLeg.rotateAngleX = Thirtytwoth * (1F + rightness);
        this.smBipedRightLeg.rotateAngleX = Thirtytwoth * (1F + leftness);
        this.smBipedLeftLeg.rotateAngleY = -angle;
        this.smBipedRightLeg.rotateAngleY = -angle;
        this.smBipedLeftLeg.rotateAngleZ = Thirtytwoth * backness;
        this.smBipedRightLeg.rotateAngleZ = -Thirtytwoth * backness;

        this.smBipedLeftLeg.rotationOrder = ModelRotationRenderer.ZXY;
        this.smBipedRightLeg.rotationOrder = ModelRotationRenderer.ZXY;

        this.smBipedLeftArm.rotateAngleZ = -Sixteenth * rightness;
        this.smBipedRightArm.rotateAngleZ = Sixteenth * leftness;

        this.smBipedLeftArm.rotateAngleX = -Eighth * backness;
        this.smBipedRightArm.rotateAngleX = -Eighth * backness;
    }

    private void animateNonStandardWorking(float viewVerticalAngelOffset) {
        this.smBipedRightShoulder.ignoreSuperRotation = true;
        this.smBipedRightShoulder.rotateAngleX = viewVerticalAngelOffset / RadiantToAngle;
        this.smBipedRightShoulder.rotateAngleY = this.workingAngle / RadiantToAngle;
        this.smBipedRightShoulder.rotateAngleZ = Half;
        this.smBipedRightShoulder.rotationOrder = ModelRotationRenderer.ZYX;
        this.smBipedRightArm.reset();
    }

    private void animateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset,
            float factor) {
        setRotationAnglesSM(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);

        if (isStandard) {
            // imp.superAnimateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
            animateStandardHeadRotation(viewHorizontalAngelOffset, viewVerticalAngelOffset);
        }
    }

    private void animateStandardHeadRotation(float headYawAngle, float headPitchAngle) {
        smBipedNeck.ignoreBase = true;
        smBipedHead.rotateAngleY = (rotationYaw + headYawAngle) / RadiantToAngle;
        smBipedHead.rotateAngleX = headPitchAngle / RadiantToAngle;
    }

    private void animateSleeping() {
        if (isStandard) {
            smBipedNeck.ignoreBase = false;
            smBipedHead.rotateAngleY = 0F;
            smBipedHead.rotateAngleX = Eighth;
            smBipedTorso.rotationPointZ = -17F;
        }
    }

    private void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset,
            float factor) {
        if (isStandard) {
            if (renderState.angleJump)
                animateAngleJumping();
            else
                // imp.superAnimateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
                animateStandardArmSwinging(totalHorizontalDistance, currentHorizontalSpeed);
        }
    }

    private void animateStandardArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed) {
        smBipedRightArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 2.0F * currentHorizontalSpeed * 0.5F;
        smBipedLeftArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 2.0F * currentHorizontalSpeed * 0.5F;

        smBipedRightLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 1.4F * currentHorizontalSpeed;
        smBipedLeftLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 1.4F * currentHorizontalSpeed;
    }

    private void animateRiding() {
        if (isStandard) {
            smBipedRightArm.rotateAngleX += -0.6283185F;
            smBipedLeftArm.rotateAngleX += -0.6283185F;
            smBipedRightLeg.rotateAngleX = -1.256637F;
            smBipedLeftLeg.rotateAngleX = -1.256637F;
            smBipedRightLeg.rotateAngleY = 0.3141593F;
            smBipedLeftLeg.rotateAngleY = -0.3141593F;
        }
    }

    private void animateLeftArmItemHolding() {
        if (isStandard) {
            smBipedLeftArm.rotateAngleX = smBipedLeftArm.rotateAngleX * 0.5F - 0.3141593F;// * mp.heldItemLeft;
        }
    }

    private void animateRightArmItemHolding() {
        if (isStandard) {
            smBipedRightArm.rotateAngleX = smBipedRightArm.rotateAngleX * 0.5F - 0.3141593F;// * mp.heldItemRight;
            // if(mp.heldItemRight == 3)
            // bipedRightArm.rotateAngleY = -0.5235988F;
        }
    }

    private void animateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset,
            float factor) {
        if (isStandard) {
            float angle = MathHelper.sin(MathHelper.sqrt(this.swingProgress) * Whole) * 0.2F;
            smBipedBreast.rotateAngleY = smBipedBody.rotateAngleY += angle;
            smBipedBreast.rotationOrder = smBipedBody.rotationOrder = ModelRotationRenderer.YXZ;
            smBipedLeftArm.rotateAngleX += angle;
        } else if (isWorking())
            animateNonStandardWorking(viewVerticalAngelOffset);
    }

    private void animateWorkingArms() {
        if (isStandard || isWorking()) {
            float f6 = 1.0F - this.swingProgress;
            f6 = 1.0F - f6 * f6 * f6;
            float f7 = MathHelper.sin(f6 * Half);
            float f8 = MathHelper.sin(this.swingProgress * Half) * -(smBipedHead.rotateAngleX - 0.7F) * 0.75F;
            smBipedRightArm.rotateAngleX -= f7 * 1.2D + f8;
            smBipedRightArm.rotateAngleY += MathHelper.sin(MathHelper.sqrt(this.swingProgress) * Whole) * 0.4F;
            smBipedRightArm.rotateAngleZ -= MathHelper.sin(this.swingProgress * Half) * 0.4F;
        }
    }

    private void animateSneaking() {
        if (isStandard && !renderState.angleJump) {
            smBipedTorso.rotateAngleX += 0.5F;
            smBipedRightLeg.rotateAngleX += -0.5F;
            smBipedLeftLeg.rotateAngleX += -0.5F;
            smBipedRightArm.rotateAngleX += -0.1F;
            smBipedLeftArm.rotateAngleX += -0.1F;

            smBipedPelvic.offsetY = -0.13652F;
            smBipedPelvic.offsetZ = -0.05652F;

            smBipedBreast.offsetY = -0.01872F;
            smBipedBreast.offsetZ = -0.07502F;

            smBipedNeck.offsetY = 0.0621F;
        }
    }

    private void animateArms(float ageInTicks) {
        if (isStandard) {
            smBipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            smBipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            smBipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            smBipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        }
    }

    private void animateNonStandardBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset,
            float viewVerticalAngelOffset, float factor) {
        this.smBipedRightShoulder.ignoreSuperRotation = true;
        this.smBipedRightShoulder.rotateAngleY = this.workingAngle / RadiantToAngle;
        this.smBipedRightShoulder.rotateAngleZ = Half;
        this.smBipedRightShoulder.rotationOrder = ModelRotationRenderer.ZYX;

        this.smBipedLeftShoulder.ignoreSuperRotation = true;
        this.smBipedLeftShoulder.rotateAngleY = this.workingAngle / RadiantToAngle;
        this.smBipedLeftShoulder.rotateAngleZ = Half;
        this.smBipedLeftShoulder.rotationOrder = ModelRotationRenderer.ZYX;

        this.smBipedRightArm.reset();
        this.smBipedLeftArm.reset();

        float headRotateAngleY = this.smBipedHead.rotateAngleY;
        float outerRotateAngleY = this.smBipedOuter.rotateAngleY;
        float headRotateAngleX = this.smBipedHead.rotateAngleX;

        this.smBipedHead.rotateAngleY = 0;
        this.smBipedOuter.rotateAngleY = 0;
        this.smBipedHead.rotateAngleX = 0;

        // imp.superAnimateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        animateStandardBowAiming(totalTime);

        this.smBipedHead.rotateAngleY = headRotateAngleY;
        this.smBipedOuter.rotateAngleY = outerRotateAngleY;
        this.smBipedHead.rotateAngleX = headRotateAngleX;
    }

    private void animateStandardBowAiming(float totalTime) {
        smBipedRightArm.rotateAngleZ = 0.0F;
        smBipedLeftArm.rotateAngleZ = 0.0F;
        smBipedRightArm.rotateAngleY = -0.1F + smBipedHead.rotateAngleY - smBipedOuter.rotateAngleY;
        smBipedLeftArm.rotateAngleY = 0.1F + smBipedHead.rotateAngleY + 0.4F - smBipedOuter.rotateAngleY;
        smBipedRightArm.rotateAngleX = -1.570796F + smBipedHead.rotateAngleX;
        smBipedLeftArm.rotateAngleX = -1.570796F + smBipedHead.rotateAngleX;
        smBipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        smBipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        smBipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
        smBipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
    }

    private void animateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset,
            float factor) {
        if (isStandard)
            animateStandardBowAiming(totalTime);
        else
            animateNonStandardBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    private void reset() {
        smBipedOuter.reset();
        smBipedTorso.reset();
        smBipedBody.reset();
        smBipedBreast.reset();
        smBipedNeck.reset();
        smBipedHead.reset();
        smBipedRightShoulder.reset();
        smBipedRightArm.reset();
        smBipedLeftShoulder.reset();
        smBipedLeftArm.reset();
        smBipedPelvic.reset();
        smBipedRightLeg.reset();
        smBipedLeftLeg.reset();

        if (isModelPlayer) {
            smBipedBodywear.reset();
            smBipedHeadwear.reset();
            smBipedRightArmwear.reset();
            smBipedLeftArmwear.reset();
            smBipedRightLegwear.reset();
            smBipedLeftLegwear.reset();

            smBipedEars.reset();
            smBipedCape.reset();
        }

        smBipedRightShoulder.setRotationPoint(-5F, isModelPlayer && smallArms ? 2.5F : 2.0F, 0.0F);
        smBipedLeftShoulder.setRotationPoint(5F, isModelPlayer && smallArms ? 2.5F : 2.0F, 0.0F);
        smBipedPelvic.setRotationPoint(0.0F, 12.0F, 0.1F);
        smBipedRightLeg.setRotationPoint(-1.9F, 0.0F, 0.0F);
        smBipedLeftLeg.setRotationPoint(1.9F, 0.0F, 0.0F);

        if (isModelPlayer)
            smBipedCape.setRotationPoint(0.0F, 0.0F, 2.0F);
    }

    @Override
    public ModelRenderer getRandomModelBox(Random rand) {
        List<ModelRenderer> boxList = this.boxList;
        int size = boxList.size();
        int renderersWithBoxes = 0;

        for (int i = 0; i < size; i++) {
            ModelRenderer renderer = boxList.get(i);
            if (canBeRandomBoxSource(renderer))
                renderersWithBoxes++;
        }

        if (renderersWithBoxes != 0) {
            int randInt = rand.nextInt(renderersWithBoxes);
            renderersWithBoxes = -1;

            for (int i = 0; i < size; i++) {
                ModelRenderer renderer = boxList.get(i);
                if (canBeRandomBoxSource(renderer))
                    renderersWithBoxes++;
                if (renderersWithBoxes == randInt)
                    return renderer;
            }
        }

        return null;
    }

    private static boolean canBeRandomBoxSource(ModelRenderer renderer) {
        return renderer.cubeList != null && renderer.cubeList.size() > 0 && (!(renderer instanceof ModelRotationRenderer) || ((ModelRotationRenderer) renderer)
                .canBeRandomBoxSource());
    }

    private void setArmScales(float rightScale, float leftScale) {
        if (scaleArmType == Scale) {
            this.smBipedRightArm.scaleY = rightScale;
            this.smBipedLeftArm.scaleY = leftScale;
        } else if (scaleArmType == NoScaleEnd) {
            this.smBipedRightArm.offsetY -= (1F - rightScale) * 0.5F;
            this.smBipedLeftArm.offsetY -= (1F - leftScale) * 0.5F;
        }
    }

    private void setLegScales(float rightScale, float leftScale) {
        if (scaleLegType == Scale) {
            this.smBipedRightLeg.scaleY = rightScale;
            this.smBipedLeftLeg.scaleY = leftScale;
        } else if (scaleLegType == NoScaleEnd) {
            this.smBipedRightLeg.offsetY -= (1F - rightScale) * 0.5F;
            this.smBipedLeftLeg.offsetY -= (1F - leftScale) * 0.5F;
        }
    }

    private static float factor(float x, float x0, float x1) {
        if (x0 > x1) {
            if (x <= x1)
                return 1F;
            if (x >= x0)
                return 0F;
            return (x0 - x) / (x0 - x1);
        } else {
            if (x >= x1)
                return 1F;
            if (x <= x0)
                return 0F;
            return (x - x0) / (x1 - x0);
        }
    }

    private static final int Scale = 0;
    private static final int NoScaleStart = 1;
    private static final int NoScaleEnd = 2;
}
