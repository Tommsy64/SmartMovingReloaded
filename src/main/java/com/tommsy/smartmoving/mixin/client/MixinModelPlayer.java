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

import com.tommsy.smartmoving.client.SmartMovingClient;
import com.tommsy.smartmoving.client.AbstractSmartMovingClientPlayerHandler.SmartMovingRenderState;
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
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Mixin(ModelPlayer.class)
public class MixinModelPlayer extends MixinModelBiped implements SmartMovingModelPlayer {

    @Shadow
    @Final
    private boolean smallArms;

    @Shadow
    public ModelRenderer shadow$bipedLeftArmwear;
    @Shadow
    public ModelRenderer shadow$bipedRightArmwear;
    @Shadow
    public ModelRenderer shadow$bipedLeftLegwear;
    @Shadow
    public ModelRenderer shadow$bipedRightLegwear;
    @Shadow
    public ModelRenderer shadow$bipedBodyWear;

    @Shadow
    @Final
    private ModelRenderer shadow$bipedCape;
    @Shadow
    @Final
    private ModelRenderer shadow$bipedDeadmau5Head;

    private ModelRotationRenderer bipedOuter;
    private ModelRotationRenderer bipedTorso;
    private ModelRotationRenderer bipedBody;
    private ModelRotationRenderer bipedBreast;
    private ModelRotationRenderer bipedNeck;
    private ModelRotationRenderer bipedHead;
    private ModelRotationRenderer bipedRightShoulder;
    private ModelRotationRenderer bipedRightArm;
    private ModelRotationRenderer bipedLeftShoulder;
    private ModelRotationRenderer bipedLeftArm;
    private ModelRotationRenderer bipedPelvic;
    private ModelRotationRenderer bipedRightLeg;
    private ModelRotationRenderer bipedLeftLeg;

    private ModelRotationRenderer bipedBodywear;
    private ModelRotationRenderer bipedHeadwear;
    private ModelRotationRenderer bipedRightArmwear;
    private ModelRotationRenderer bipedLeftArmwear;
    private ModelRotationRenderer bipedRightLegwear;
    private ModelRotationRenderer bipedLeftLegwear;

    @Getter
    private ModelEarsRenderer bipedEars;
    @Getter
    private ModelCapeRenderer bipedCape;

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

    // TODO: Initialize
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
    private void onConstructed(RenderManager renderManager, boolean useSmallArms, CallbackInfo ci) {
        this.boxList.clear();

        bipedOuter = create(null);
        bipedOuter.fadeEnabled = true;

        bipedTorso = create(bipedOuter);
        bipedBody = create(bipedTorso, this.shadow$bipedBody);
        bipedBreast = create(bipedTorso);
        bipedNeck = create(bipedBreast);
        bipedHead = create(bipedNeck, this.shadow$bipedHead);
        bipedRightShoulder = create(bipedBreast);
        bipedRightArm = create(bipedRightShoulder, this.shadow$bipedRightArm);
        bipedLeftShoulder = create(bipedBreast);
        bipedLeftShoulder.mirror = true;
        bipedLeftArm = create(bipedLeftShoulder, this.shadow$bipedLeftArm);
        bipedPelvic = create(bipedTorso);
        bipedRightLeg = create(bipedPelvic, this.shadow$bipedRightLeg);
        bipedLeftLeg = create(bipedPelvic, this.shadow$bipedLeftLeg);

        bipedBodywear = create(bipedBody, this.shadow$bipedBodyWear);
        bipedHeadwear = create(bipedHead, this.shadow$bipedHeadwear);
        bipedRightArmwear = create(bipedRightArm, this.shadow$bipedRightArmwear);
        bipedLeftArmwear = create(bipedLeftArm, this.shadow$bipedLeftArmwear);
        bipedRightLegwear = create(bipedRightLeg, this.shadow$bipedRightLegwear);
        bipedLeftLegwear = create(bipedLeftLeg, this.shadow$bipedLeftLegwear);

        if (this.shadow$bipedCape != null) {
            bipedCape = new ModelCapeRenderer(this, 0, 0, bipedBreast, bipedOuter);
            copy(bipedCape, this.shadow$bipedCape);
        }

        if (this.shadow$bipedDeadmau5Head != null) {
            bipedEars = new ModelEarsRenderer(this, 24, 0, bipedHead);
            copy(bipedEars, this.shadow$bipedDeadmau5Head);
        }

        reset(); // set default rotation points

        this.shadow$bipedBody = bipedBody;
        this.shadow$bipedHead = bipedHead;
        this.shadow$bipedRightArm = bipedRightArm;
        this.shadow$bipedLeftArm = bipedLeftArm;
        this.shadow$bipedRightLeg = bipedRightLeg;
        this.shadow$bipedLeftLeg = bipedLeftLeg;

        if (isModelPlayer) {
            this.shadow$bipedBodyWear = bipedBodywear;
            this.shadow$bipedHeadwear = bipedHeadwear;
            this.shadow$bipedRightArmwear = bipedRightArmwear;
            this.shadow$bipedLeftArmwear = bipedLeftArmwear;
            this.shadow$bipedRightLegwear = bipedRightLegwear;
            this.shadow$bipedLeftLegwear = bipedLeftLegwear;
        }

        // if (SmartRenderRender.CurrentMainModel != null) {
        // isInventory = SmartRenderRender.CurrentMainModel.isInventory;
        //
        // totalVerticalDistance = SmartRenderRender.CurrentMainModel.totalVerticalDistance;
        // currentVerticalSpeed = SmartRenderRender.CurrentMainModel.currentVerticalSpeed;
        // totalDistance = SmartRenderRender.CurrentMainModel.totalDistance;
        // currentSpeed = SmartRenderRender.CurrentMainModel.currentSpeed;
        //
        // distance = SmartRenderRender.CurrentMainModel.distance;
        // verticalDistance = SmartRenderRender.CurrentMainModel.verticalDistance;
        // horizontalDistance = SmartRenderRender.CurrentMainModel.horizontalDistance;
        // currentCameraAngle = SmartRenderRender.CurrentMainModel.currentCameraAngle;
        // currentVerticalAngle = SmartRenderRender.CurrentMainModel.currentVerticalAngle;
        // currentHorizontalAngle = SmartRenderRender.CurrentMainModel.currentHorizontalAngle;
        // prevOuterRenderData = SmartRenderRender.CurrentMainModel.prevOuterRenderData;
        // isSleeping = SmartRenderRender.CurrentMainModel.isSleeping;
        //
        // actualRotation = SmartRenderRender.CurrentMainModel.actualRotation;
        // forwardRotation = SmartRenderRender.CurrentMainModel.forwardRotation;
        // workingAngle = SmartRenderRender.CurrentMainModel.workingAngle;
        // }
        //
        // if(SmartMovingRender.CurrentMainModel != null)
        // {
        // isClimb = SmartMovingRender.CurrentMainModel.isClimb;
        // isClimbJump = SmartMovingRender.CurrentMainModel.isClimbJump;
        // handsClimbType = SmartMovingRender.CurrentMainModel.handsClimbType;
        // feetClimbType = SmartMovingRender.CurrentMainModel.feetClimbType;
        // isHandsVineClimbing = SmartMovingRender.CurrentMainModel.isHandsVineClimbing;
        // isFeetVineClimbing = SmartMovingRender.CurrentMainModel.isFeetVineClimbing;
        // isCeilingClimb = SmartMovingRender.CurrentMainModel.isCeilingClimb;
        // isSwim = SmartMovingRender.CurrentMainModel.isSwim;
        // isDive = SmartMovingRender.CurrentMainModel.isDive;
        // isCrawl = SmartMovingRender.CurrentMainModel.isCrawl;
        // isCrawlClimb = SmartMovingRender.CurrentMainModel.isCrawlClimb;
        // isJump = SmartMovingRender.CurrentMainModel.isJump;
        // isHeadJump = SmartMovingRender.CurrentMainModel.isHeadJump;
        // isSlide = SmartMovingRender.CurrentMainModel.isSlide;
        // isFlying = SmartMovingRender.CurrentMainModel.isFlying;
        // isLevitate = SmartMovingRender.CurrentMainModel.isLevitate;
        // isFalling = SmartMovingRender.CurrentMainModel.isFalling;
        // isGenericSneaking = SmartMovingRender.CurrentMainModel.isGenericSneaking;
        // isAngleJumping = SmartMovingRender.CurrentMainModel.isAngleJumping;
        // angleJumpType = SmartMovingRender.CurrentMainModel.angleJumpType;
        // isRopeSliding = SmartMovingRender.CurrentMainModel.isRopeSliding;
        //
        // currentHorizontalSpeedFlattened = SmartMovingRender.CurrentMainModel.currentHorizontalSpeedFlattened;
        // smallOverGroundHeight = SmartMovingRender.CurrentMainModel.smallOverGroundHeight;
        // overGroundBlock = SmartMovingRender.CurrentMainModel.overGroundBlock;
        // }

        SmartMovingClient.modelPlayerInstances.add((ModelPlayer) ((Object) this));
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

        bipedBody.ignoreRender = bipedHead.ignoreRender = bipedRightArm.ignoreRender = bipedLeftArm.ignoreRender = bipedRightLeg.ignoreRender = bipedLeftLeg.ignoreRender = true;
        if (isModelPlayer)
            bipedBodywear.ignoreRender = bipedHeadwear.ignoreRender = bipedRightArmwear.ignoreRender = bipedLeftArmwear.ignoreRender = bipedRightLegwear.ignoreRender = bipedLeftLegwear.ignoreRender = true;
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        if (isModelPlayer)
            bipedBodywear.ignoreRender = bipedHeadwear.ignoreRender = bipedRightArmwear.ignoreRender = bipedLeftArmwear.ignoreRender = bipedRightLegwear.ignoreRender = bipedLeftLegwear.ignoreRender = false;
        bipedBody.ignoreRender = bipedHead.ignoreRender = bipedRightArm.ignoreRender = bipedLeftArm.ignoreRender = bipedRightLeg.ignoreRender = bipedLeftLeg.ignoreRender = false;

        bipedOuter.render(scale);

        bipedOuter.renderIgnoreBase(scale);
        bipedTorso.renderIgnoreBase(scale);
        bipedBody.renderIgnoreBase(scale);
        bipedBreast.renderIgnoreBase(scale);
        bipedNeck.renderIgnoreBase(scale);
        bipedHead.renderIgnoreBase(scale);
        bipedRightShoulder.renderIgnoreBase(scale);
        bipedRightArm.renderIgnoreBase(scale);
        bipedLeftShoulder.renderIgnoreBase(scale);
        bipedLeftArm.renderIgnoreBase(scale);
        bipedPelvic.renderIgnoreBase(scale);
        bipedRightLeg.renderIgnoreBase(scale);
        bipedLeftLeg.renderIgnoreBase(scale);

        if (isModelPlayer) {
            bipedBodywear.renderIgnoreBase(scale);
            bipedHeadwear.renderIgnoreBase(scale);
            bipedRightArmwear.renderIgnoreBase(scale);
            bipedLeftArmwear.renderIgnoreBase(scale);
            bipedRightLegwear.renderIgnoreBase(scale);
            bipedLeftLegwear.renderIgnoreBase(scale);
        }

        GL11.glPopMatrix();
    }

    @Inject(method = "setRotationAngles", at = @At("HEAD"), cancellable = true)
    private void preSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor,
            Entity entity, CallbackInfo ci) {
        reset();

        if (firstPerson || isInventory) {
            bipedBody.ignoreBase = true;
            bipedHead.ignoreBase = true;
            bipedRightArm.ignoreBase = true;
            bipedLeftArm.ignoreBase = true;
            bipedRightLeg.ignoreBase = true;
            bipedLeftLeg.ignoreBase = true;

            if (isModelPlayer) {
                bipedBodywear.ignoreBase = true;
                bipedHeadwear.ignoreBase = true;
                bipedRightArmwear.ignoreBase = true;
                bipedLeftArmwear.ignoreBase = true;
                bipedRightLegwear.ignoreBase = true;
                bipedLeftLegwear.ignoreBase = true;

                bipedEars.ignoreBase = true;
                bipedCape.ignoreBase = true;
            }

            bipedBody.forceRender = firstPerson;
            bipedHead.forceRender = firstPerson;
            bipedRightArm.forceRender = firstPerson;
            bipedLeftArm.forceRender = firstPerson;
            bipedRightLeg.forceRender = firstPerson;
            bipedLeftLeg.forceRender = firstPerson;

            if (isModelPlayer) {
                bipedBodywear.forceRender = firstPerson;
                bipedHeadwear.forceRender = firstPerson;
                bipedRightArmwear.forceRender = firstPerson;
                bipedLeftArmwear.forceRender = firstPerson;
                bipedRightLegwear.forceRender = firstPerson;
                bipedLeftLegwear.forceRender = firstPerson;

                bipedEars.forceRender = firstPerson;
                bipedCape.forceRender = firstPerson;
            }

            bipedRightArm.setRotationPoint(-5F, 2.0F, 0.0F);
            bipedLeftArm.setRotationPoint(5F, 2.0F, 0.0F);
            bipedRightLeg.setRotationPoint(-2F, 12F, 0.0F);
            bipedLeftLeg.setRotationPoint(2.0F, 12F, 0.0F);

            return;
        }
        ci.cancel(); // Don't continue base setRotationAngles

        if (isSleeping) {
            prevOuterRenderData.rotateAngleX = 0;
            prevOuterRenderData.rotateAngleY = 0;
            prevOuterRenderData.rotateAngleZ = 0;
        }

        bipedOuter.previous = prevOuterRenderData;

        bipedOuter.rotateAngleY = rotationYaw / RadiantToAngle;
        bipedOuter.fadeRotateAngleY = !entity.isRiding();

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

        if (bipedOuter.previous != null && !bipedOuter.fadeRotateAngleX)
            bipedOuter.previous.rotateAngleX = bipedOuter.rotateAngleX;

        if (bipedOuter.previous != null && !bipedOuter.fadeRotateAngleY)
            bipedOuter.previous.rotateAngleY = bipedOuter.rotateAngleY;

        bipedOuter.fadeIntermediate(ageInTicks);
        bipedOuter.fadeStore(ageInTicks);

        if (isModelPlayer) {
            bipedCape.ignoreBase = false;
            bipedCape.rotateAngleX = Sixtyfourth;
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

        ModelRotationRenderer bipedOuter = this.bipedOuter;
        ModelRotationRenderer bipedTorso = this.bipedTorso;
        ModelRotationRenderer bipedBody = this.bipedBody;
        ModelRotationRenderer bipedBreast = this.bipedBreast;
        ModelRotationRenderer bipedHead = this.bipedHead;
        ModelRotationRenderer bipedRightShoulder = this.bipedRightShoulder;
        ModelRotationRenderer bipedRightArm = this.bipedRightArm;
        ModelRotationRenderer bipedLeftShoulder = this.bipedLeftShoulder;
        ModelRotationRenderer bipedLeftArm = this.bipedLeftArm;
        ModelRotationRenderer bipedPelvic = this.bipedPelvic;
        ModelRotationRenderer bipedRightLeg = this.bipedRightLeg;
        ModelRotationRenderer bipedLeftLeg = this.bipedLeftLeg;

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
            float walkFactor = Factor(currentHorizontalSpeed, 0F, 0.12951545F);
            float standFactor = Factor(currentHorizontalSpeed, 0.12951545F, 0F);
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
            float walkFactor = Factor(currentHorizontalSpeed, 0.15679921F, 0.52264464F);
            float sneakFactor = Math.min(Factor(currentHorizontalSpeed, 0, 0.15679921F), Factor(currentHorizontalSpeed, 0.52264464F, 0.15679921F));
            float standFactor = Factor(currentHorizontalSpeed, 0.15679921F, 0F);
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
            float walkFactor = Factor(currentSpeed, 0F, 0.15679921F);
            float standFactor = Factor(currentSpeed, 0.15679921F, 0F);
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
            float walkFactor = Factor(currentHorizontalSpeedFlattened, 0F, 0.12951545F);
            float standFactor = Factor(currentHorizontalSpeedFlattened, 0.12951545F, 0F);

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
            float walkFactor = Factor(currentHorizontalSpeed, 0F, 1F) * 0.8F;

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
            float walkFactor = Factor(currentSpeed, 0F, 1);
            float standFactor = Factor(currentSpeed, 1F, 0F);
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

            float bendFactor = Math.min(Factor(currentVerticalAngle, Quarter, 0), Factor(currentVerticalAngle, -Quarter, 0));
            bipedRightArm.rotateAngleX = bendFactor * -Eighth;
            bipedLeftArm.rotateAngleX = bendFactor * -Eighth;

            bipedRightLeg.rotateAngleX = bendFactor * -Eighth;
            bipedLeftLeg.rotateAngleX = bendFactor * -Eighth;

            float armFactorZ = Factor(currentVerticalAngle, Quarter, -Quarter);
            if (overGroundBlock != null && overGroundBlock.getBlockState().getBaseState().getMaterial().isSolid())
                armFactorZ = Math.min(armFactorZ, smallOverGroundHeight / 5F);

            bipedRightArm.rotateAngleZ = Half - Sixteenth + armFactorZ * Eighth;
            bipedLeftArm.rotateAngleZ = Sixteenth - Half - armFactorZ * Eighth;

            float legFactorZ = Factor(currentVerticalAngle, -Quarter, Quarter);
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
        this.bipedPelvic.rotateAngleY -= this.bipedOuter.rotateAngleY;
        this.bipedPelvic.rotateAngleY += this.currentCameraAngle;

        float backness = 1F - Math.abs(angle - Half) / Quarter;
        float leftness = -Math.min(angle - Half, 0F) / Quarter;
        float rightness = Math.max(angle - Half, 0F) / Quarter;

        this.bipedLeftLeg.rotateAngleX = Thirtytwoth * (1F + rightness);
        this.bipedRightLeg.rotateAngleX = Thirtytwoth * (1F + leftness);
        this.bipedLeftLeg.rotateAngleY = -angle;
        this.bipedRightLeg.rotateAngleY = -angle;
        this.bipedLeftLeg.rotateAngleZ = Thirtytwoth * backness;
        this.bipedRightLeg.rotateAngleZ = -Thirtytwoth * backness;

        this.bipedLeftLeg.rotationOrder = ModelRotationRenderer.ZXY;
        this.bipedRightLeg.rotationOrder = ModelRotationRenderer.ZXY;

        this.bipedLeftArm.rotateAngleZ = -Sixteenth * rightness;
        this.bipedRightArm.rotateAngleZ = Sixteenth * leftness;

        this.bipedLeftArm.rotateAngleX = -Eighth * backness;
        this.bipedRightArm.rotateAngleX = -Eighth * backness;
    }

    private void animateNonStandardWorking(float viewVerticalAngelOffset) {
        this.bipedRightShoulder.ignoreSuperRotation = true;
        this.bipedRightShoulder.rotateAngleX = viewVerticalAngelOffset / RadiantToAngle;
        this.bipedRightShoulder.rotateAngleY = this.workingAngle / RadiantToAngle;
        this.bipedRightShoulder.rotateAngleZ = Half;
        this.bipedRightShoulder.rotationOrder = ModelRotationRenderer.ZYX;
        this.bipedRightArm.reset();
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
        bipedNeck.ignoreBase = true;
        bipedHead.rotateAngleY = (rotationYaw + headYawAngle) / RadiantToAngle;
        bipedHead.rotateAngleX = headPitchAngle / RadiantToAngle;
    }

    private void animateSleeping() {
        if (isStandard) {
            bipedNeck.ignoreBase = false;
            bipedHead.rotateAngleY = 0F;
            bipedHead.rotateAngleX = Eighth;
            bipedTorso.rotationPointZ = -17F;
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
        bipedRightArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 2.0F * currentHorizontalSpeed * 0.5F;
        bipedLeftArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 2.0F * currentHorizontalSpeed * 0.5F;

        bipedRightLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 1.4F * currentHorizontalSpeed;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 1.4F * currentHorizontalSpeed;
    }

    private void animateRiding() {
        if (isStandard) {
            bipedRightArm.rotateAngleX += -0.6283185F;
            bipedLeftArm.rotateAngleX += -0.6283185F;
            bipedRightLeg.rotateAngleX = -1.256637F;
            bipedLeftLeg.rotateAngleX = -1.256637F;
            bipedRightLeg.rotateAngleY = 0.3141593F;
            bipedLeftLeg.rotateAngleY = -0.3141593F;
        }
    }

    private void animateLeftArmItemHolding() {
        if (isStandard) {
            bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - 0.3141593F;// * mp.heldItemLeft;
        }
    }

    private void animateRightArmItemHolding() {
        if (isStandard) {
            bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - 0.3141593F;// * mp.heldItemRight;
            // if(mp.heldItemRight == 3)
            // bipedRightArm.rotateAngleY = -0.5235988F;
        }
    }

    private void animateWorkingBody(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset,
            float factor) {
        if (isStandard) {
            float angle = MathHelper.sin(MathHelper.sqrt(this.swingProgress) * Whole) * 0.2F;
            bipedBreast.rotateAngleY = bipedBody.rotateAngleY += angle;
            bipedBreast.rotationOrder = bipedBody.rotationOrder = ModelRotationRenderer.YXZ;
            bipedLeftArm.rotateAngleX += angle;
        } else if (isWorking())
            animateNonStandardWorking(viewVerticalAngelOffset);
    }

    private void animateWorkingArms() {
        if (isStandard || isWorking()) {
            float f6 = 1.0F - this.swingProgress;
            f6 = 1.0F - f6 * f6 * f6;
            float f7 = MathHelper.sin(f6 * Half);
            float f8 = MathHelper.sin(this.swingProgress * Half) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
            bipedRightArm.rotateAngleX -= f7 * 1.2D + f8;
            bipedRightArm.rotateAngleY += MathHelper.sin(MathHelper.sqrt(this.swingProgress) * Whole) * 0.4F;
            bipedRightArm.rotateAngleZ -= MathHelper.sin(this.swingProgress * Half) * 0.4F;
        }
    }

    private void animateSneaking() {
        if (isStandard && !renderState.angleJump) {
            bipedTorso.rotateAngleX += 0.5F;
            bipedRightLeg.rotateAngleX += -0.5F;
            bipedLeftLeg.rotateAngleX += -0.5F;
            bipedRightArm.rotateAngleX += -0.1F;
            bipedLeftArm.rotateAngleX += -0.1F;

            bipedPelvic.offsetY = -0.13652F;
            bipedPelvic.offsetZ = -0.05652F;

            bipedBreast.offsetY = -0.01872F;
            bipedBreast.offsetZ = -0.07502F;

            bipedNeck.offsetY = 0.0621F;
        }
    }

    private void animateArms(float ageInTicks) {
        if (isStandard) {
            bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
            bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
            bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        }
    }

    private void animateNonStandardBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset,
            float viewVerticalAngelOffset, float factor) {
        this.bipedRightShoulder.ignoreSuperRotation = true;
        this.bipedRightShoulder.rotateAngleY = this.workingAngle / RadiantToAngle;
        this.bipedRightShoulder.rotateAngleZ = Half;
        this.bipedRightShoulder.rotationOrder = ModelRotationRenderer.ZYX;

        this.bipedLeftShoulder.ignoreSuperRotation = true;
        this.bipedLeftShoulder.rotateAngleY = this.workingAngle / RadiantToAngle;
        this.bipedLeftShoulder.rotateAngleZ = Half;
        this.bipedLeftShoulder.rotationOrder = ModelRotationRenderer.ZYX;

        this.bipedRightArm.reset();
        this.bipedLeftArm.reset();

        float headRotateAngleY = this.bipedHead.rotateAngleY;
        float outerRotateAngleY = this.bipedOuter.rotateAngleY;
        float headRotateAngleX = this.bipedHead.rotateAngleX;

        this.bipedHead.rotateAngleY = 0;
        this.bipedOuter.rotateAngleY = 0;
        this.bipedHead.rotateAngleX = 0;

        // imp.superAnimateBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
        animateStandardBowAiming(totalTime);

        this.bipedHead.rotateAngleY = headRotateAngleY;
        this.bipedOuter.rotateAngleY = outerRotateAngleY;
        this.bipedHead.rotateAngleX = headRotateAngleX;
    }

    private void animateStandardBowAiming(float totalTime) {
        bipedRightArm.rotateAngleZ = 0.0F;
        bipedLeftArm.rotateAngleZ = 0.0F;
        bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - bipedOuter.rotateAngleY;
        bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY + 0.4F - bipedOuter.rotateAngleY;
        bipedRightArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
        bipedLeftArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
        bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
    }

    private void animateBowAiming(float totalHorizontalDistance, float currentHorizontalSpeed, float totalTime, float viewHorizontalAngelOffset, float viewVerticalAngelOffset,
            float factor) {
        if (isStandard)
            animateStandardBowAiming(totalTime);
        else
            animateNonStandardBowAiming(totalHorizontalDistance, currentHorizontalSpeed, totalTime, viewHorizontalAngelOffset, viewVerticalAngelOffset, factor);
    }

    private void reset() {
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

        if (isModelPlayer) {
            bipedBodywear.reset();
            bipedHeadwear.reset();
            bipedRightArmwear.reset();
            bipedLeftArmwear.reset();
            bipedRightLegwear.reset();
            bipedLeftLegwear.reset();

            bipedEars.reset();
            bipedCape.reset();
        }

        bipedRightShoulder.setRotationPoint(-5F, isModelPlayer && smallArms ? 2.5F : 2.0F, 0.0F);
        bipedLeftShoulder.setRotationPoint(5F, isModelPlayer && smallArms ? 2.5F : 2.0F, 0.0F);
        bipedPelvic.setRotationPoint(0.0F, 12.0F, 0.1F);
        bipedRightLeg.setRotationPoint(-1.9F, 0.0F, 0.0F);
        bipedLeftLeg.setRotationPoint(1.9F, 0.0F, 0.0F);

        if (isModelPlayer)
            bipedCape.setRotationPoint(0.0F, 0.0F, 2.0F);
    }

    private ModelRenderer getRandomBox(Random par1Random) {
        List<ModelRenderer> boxList = this.boxList;
        int size = boxList.size();
        int renderersWithBoxes = 0;

        for (int i = 0; i < size; i++) {
            ModelRenderer renderer = boxList.get(i);
            if (canBeRandomBoxSource(renderer))
                renderersWithBoxes++;
        }

        if (renderersWithBoxes != 0) {
            int random = par1Random.nextInt(renderersWithBoxes);
            renderersWithBoxes = -1;

            for (int i = 0; i < size; i++) {
                ModelRenderer renderer = boxList.get(i);
                if (canBeRandomBoxSource(renderer))
                    renderersWithBoxes++;
                if (renderersWithBoxes == random)
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
            this.bipedRightArm.scaleY = rightScale;
            this.bipedLeftArm.scaleY = leftScale;
        } else if (scaleArmType == NoScaleEnd) {
            this.bipedRightArm.offsetY -= (1F - rightScale) * 0.5F;
            this.bipedLeftArm.offsetY -= (1F - leftScale) * 0.5F;
        }
    }

    private void setLegScales(float rightScale, float leftScale) {
        if (scaleLegType == Scale) {
            this.bipedRightLeg.scaleY = rightScale;
            this.bipedLeftLeg.scaleY = leftScale;
        } else if (scaleLegType == NoScaleEnd) {
            this.bipedRightLeg.offsetY -= (1F - rightScale) * 0.5F;
            this.bipedLeftLeg.offsetY -= (1F - leftScale) * 0.5F;
        }
    }

    private static float Factor(float x, float x0, float x1) {
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

    // private static float Between(float min, float max, float value) {
    // if (value < min)
    // return min;
    // if (value > max)
    // return max;
    // return value;
    // }
    //
    // private static float Normalize(float radiant) {
    // while (radiant > Half)
    // radiant -= Whole;
    // while (radiant < -Half)
    // radiant += Whole;
    // return radiant;
    // }

    private static final int Scale = 0;
    private static final int NoScaleStart = 1;
    private static final int NoScaleEnd = 2;
}
