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

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;
import com.tommsy.smartmoving.client.renderer.ModelPreviousRotationRenderer;
import com.tommsy.smartmoving.client.renderer.ModelRotationRenderer;
import com.tommsy.smartmoving.client.renderer.ModelRotationRenderer.RotationOrder;
import com.tommsy.smartmoving.client.renderer.ScaleType;
import com.tommsy.smartmoving.common.SmartMovingPlayerState;

import static com.tommsy.smartmoving.client.renderer.RenderUtils.Eighth;
import static com.tommsy.smartmoving.client.renderer.RenderUtils.Half;
import static com.tommsy.smartmoving.client.renderer.RenderUtils.Quarter;
import static com.tommsy.smartmoving.client.renderer.RenderUtils.RadianToAngle;
import static com.tommsy.smartmoving.client.renderer.RenderUtils.Sixteenth;
import static com.tommsy.smartmoving.client.renderer.RenderUtils.Sixtyfourth;
import static com.tommsy.smartmoving.client.renderer.RenderUtils.Thirtysecond;
import static com.tommsy.smartmoving.client.renderer.RenderUtils.Whole;

public class SmartMovingModelBipedHandler {

    // These are the same object, just different types
    private final SmartMovingModelBiped smModel;
    private final ModelBiped model;

    private final boolean doGLMatrixPop;

    protected ModelPreviousRotationRenderer bipedOuter;
    protected ModelRotationRenderer bipedTorso;
    protected ModelRotationRenderer bipedBody;
    protected ModelRotationRenderer bipedBreast;
    protected ModelRotationRenderer bipedNeck;
    protected ModelRotationRenderer bipedHead;
    protected ModelRotationRenderer bipedRightShoulder;
    protected ModelRotationRenderer bipedRightArm;
    protected ModelRotationRenderer bipedLeftShoulder;
    protected ModelRotationRenderer bipedLeftArm;
    protected ModelRotationRenderer bipedPelvic;
    protected ModelRotationRenderer bipedRightLeg;
    protected ModelRotationRenderer bipedLeftLeg;

    protected ModelRotationRenderer bipedHeadwear;

    // Updated from RenderPlayer
    public float rotationYaw;
    /**
     * Set to true when the model is being rendered in the inventory GUI
     */
    public boolean isBeingRenderedInInventory;

    private ScaleType scaleArmType, scaleLegType;

    public SmartMovingModelBipedHandler(SmartMovingModelBiped smModel) {
        this(smModel, true);
    }

    protected SmartMovingModelBipedHandler(SmartMovingModelBiped smModel, final boolean doGLMatrixPop) {
        model = (this.smModel = smModel).getImplementation();
        this.doGLMatrixPop = doGLMatrixPop;

        // Clear boxList that holds default ModelRenderers
        // The custom ones are automatically added back
        model.boxList.clear();
        // Can't call initialize from constructor because fields are not accessible until object done constructing
    }

    public final void initialize() {
        initializeRenderers();
        reset(); // Set default rotation points
    }

    protected void initializeRenderers() {
        bipedOuter = new ModelPreviousRotationRenderer(model, -1, -1, null);
        bipedOuter.fadeEnabled = true;

        bipedTorso = new ModelRotationRenderer(model, bipedOuter);
        bipedBody = new ModelRotationRenderer(model, bipedTorso, model.bipedBody);
        bipedBreast = new ModelRotationRenderer(model, bipedTorso);
        bipedNeck = new ModelRotationRenderer(model, bipedBreast);
        bipedHead = new ModelRotationRenderer(model, bipedNeck, model.bipedHead);
        bipedRightShoulder = new ModelRotationRenderer(model, bipedBreast);
        bipedRightArm = new ModelRotationRenderer(model, bipedRightShoulder, model.bipedRightArm);
        bipedLeftShoulder = new ModelRotationRenderer(model, bipedBreast);
        bipedLeftShoulder.mirror = true;
        bipedLeftArm = new ModelRotationRenderer(model, bipedLeftShoulder, model.bipedLeftArm);
        bipedPelvic = new ModelRotationRenderer(model, bipedTorso);
        bipedRightLeg = new ModelRotationRenderer(model, bipedPelvic, model.bipedRightLeg);
        bipedLeftLeg = new ModelRotationRenderer(model, bipedPelvic, model.bipedLeftLeg);

        bipedHeadwear = new ModelRotationRenderer(model, bipedHead, model.bipedHeadwear);

        model.bipedBody = bipedBody;
        model.bipedHead = bipedHead;
        model.bipedHeadwear = bipedHeadwear;
        model.bipedRightArm = bipedRightArm;
        model.bipedLeftArm = bipedLeftArm;
        model.bipedRightLeg = bipedRightLeg;
        model.bipedLeftLeg = bipedLeftLeg;
    }

    public void preRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GL11.glPushMatrix();
        bipedBody.ignoreRender = bipedHead.ignoreRender = bipedRightArm.ignoreRender = bipedLeftArm.ignoreRender = bipedRightLeg.ignoreRender = bipedLeftLeg.ignoreRender = bipedHeadwear.ignoreRender = true;
    }

    public void postRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        bipedBody.ignoreRender = bipedHead.ignoreRender = bipedRightArm.ignoreRender = bipedLeftArm.ignoreRender = bipedRightLeg.ignoreRender = bipedLeftLeg.ignoreRender = bipedHeadwear.ignoreRender = false;

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

        bipedHeadwear.renderIgnoreBase(scale);

        popMatrix();
    }

    // Probably shouldn't be field
    private boolean isStandardAnimation;

    /**
     * @return True if {@linkplain ModelBiped#setRotationAngles(float, float, float, float, float, float, Entity)} should not be called
     */
    public boolean preSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor,
            Entity entity) {
        reset(); // Reset all model renderers to their default states

        if (isBeingRenderedInInventory) {
            bipedBody.ignoreBase = true;
            bipedHead.ignoreBase = true;
            bipedRightArm.ignoreBase = true;
            bipedLeftArm.ignoreBase = true;
            bipedRightLeg.ignoreBase = true;
            bipedLeftLeg.ignoreBase = true;

            bipedBody.forceRender = false;
            bipedHead.forceRender = false;
            bipedRightArm.forceRender = false;
            bipedLeftArm.forceRender = false;
            bipedRightLeg.forceRender = false;
            bipedLeftLeg.forceRender = false;

            bipedRightArm.setRotationPoint(-5F, 2.0F, 0.0F);
            bipedLeftArm.setRotationPoint(5F, 2.0F, 0.0F);
            bipedRightLeg.setRotationPoint(-2F, 12F, 0.0F);
            bipedLeftLeg.setRotationPoint(2.0F, 12F, 0.0F);

            return false;
        }

        final AbstractClientPlayer player = (AbstractClientPlayer) entity;

        bipedOuter.rotateAngleY = rotationYaw / RadianToAngle;
        bipedOuter.fadeRotateAngleY = !player.isRiding();

        isStandardAnimation = false;

        // Handle smart moving state...

        final float partialTicks = ageInTicks - player.ticksExisted;

        float diffX = (float) (entity.posX - entity.prevPosX), diffZ = (float) (entity.posZ - entity.prevPosZ);
        final float horizontalSpeed = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        SmartMovingAbstractClientPlayer smPlayer = (SmartMovingAbstractClientPlayer) player;
        SmartMovingPlayerState state = smPlayer.getState();
        if (state.isCrawling) {
            float distance = totalHorizontalDistance * 1.3F;
            float walkFactor = factor(horizontalSpeed, 0F, 0.12951545F);
            float standFactor = factor(horizontalSpeed, 0.12951545F, 0F);

            bipedTorso.offsetZ = -0.85f;

            bipedHead.rotateAngleZ = -headYawAngle / RadianToAngle;
            bipedHead.rotateAngleX = -Eighth;
            bipedHead.rotationPointZ = -2F;

            bipedTorso.rotationOrder = RotationOrder.YZX;
            bipedTorso.rotateAngleX = Quarter - Thirtysecond;
            bipedTorso.rotationPointY = 3F;
            bipedTorso.rotateAngleZ = MathHelper.cos(distance + Quarter) * Sixtyfourth * walkFactor;
            bipedBody.rotateAngleY = MathHelper.cos(distance + Half) * Sixtyfourth * walkFactor;

            bipedRightLeg.rotateAngleX = (MathHelper.cos(distance - Quarter) * Sixtyfourth + Thirtysecond) * walkFactor + Thirtysecond * standFactor;
            bipedLeftLeg.rotateAngleX = (MathHelper.cos(distance - Half - Quarter) * Sixtyfourth + Thirtysecond) * walkFactor + Thirtysecond * standFactor;

            bipedRightLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) + 1F) * 0.25F * walkFactor + Thirtysecond * standFactor;
            bipedLeftLeg.rotateAngleZ = (MathHelper.cos(distance - Quarter) - 1F) * 0.25F * walkFactor - Thirtysecond * standFactor;

            if (scaleLegType != ScaleType.NoScaleStart)
                setLegScales(
                        1F + (MathHelper.cos(distance + Quarter - Quarter) - 1F) * 0.25F * walkFactor,
                        1F + (MathHelper.cos(distance - Quarter - Quarter) - 1F) * 0.25F * walkFactor);

            bipedRightArm.rotationOrder = RotationOrder.YZX;
            bipedLeftArm.rotationOrder = RotationOrder.YZX;

            bipedRightArm.rotateAngleX = Half + Eighth;
            bipedLeftArm.rotateAngleX = Half + Eighth;

            bipedRightArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth + Thirtysecond) * walkFactor + Sixteenth * standFactor;
            bipedLeftArm.rotateAngleZ = ((MathHelper.cos(distance + Half)) * Sixtyfourth - Thirtysecond) * walkFactor - Sixteenth * standFactor;

            bipedRightArm.rotateAngleY = -Quarter;
            bipedLeftArm.rotateAngleY = Quarter;

            if (scaleArmType != ScaleType.NoScaleStart)
                setArmScales(
                        1F + (MathHelper.cos(distance + Quarter) - 1F) * 0.15F * walkFactor,
                        1F + (MathHelper.cos(distance - Quarter) - 1F) * 0.15F * walkFactor);
        } else if (player.isElytraFlying())
            animateElytraFlying((EntityLivingBase) entity, partialTicks, ageInTicks);
        else
            isStandardAnimation = true;

        animateHeadRotation(headYawAngle, headPitchAngle, player.isElytraFlying());

        if (player.isPlayerSleeping())
            animateSleeping();

        float elytraMagnitude = player.getTicksElytraFlying() > 4 ? getElytraMagnitude(entity) : 1;
        if (isStandardAnimation || elytraMagnitude != 1) {
            animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed, elytraMagnitude);
        }

        if (model.isRiding)
            animateRiding();

        if (model.leftArmPose == ModelBiped.ArmPose.ITEM)
            this.animateLeftArmItemHolding();

        if (model.rightArmPose == ModelBiped.ArmPose.ITEM)
            this.animateRightArmItemHolding();

        if (model.swingProgress > 0F)
            animateSwinging(entity);
        if (model.isSneak)
            animateSneaking();

        animateArms(ageInTicks);

        // Should this be || or &&?
        if (model.rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW)
            animateBowAimingRight(ageInTicks);
        else if (model.leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW)
            animateBowAimingLeft(ageInTicks);

        // ???
        if (!bipedOuter.fadeRotateAngleX)
            bipedOuter.previous.rotateAngleX = bipedOuter.rotateAngleX;

        if (!bipedOuter.fadeRotateAngleY)
            bipedOuter.previous.rotateAngleY = bipedOuter.rotateAngleY;

        bipedOuter.fadeIntermediate(ageInTicks);
        bipedOuter.fadeStore(ageInTicks);

        return true;
    }

    private float getElytraMagnitude(Entity entity) {
        float f = (float) (entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ);
        f = f / 0.2F;
        f = f * f * f;
        if (f < 1.0F)
            return 1;
        return f;
    }

    public void postSetRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {

    }

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

        bipedHeadwear.reset();

        bipedRightShoulder.setRotationPoint(-5F, 2.0F, 0.0F);
        bipedLeftShoulder.setRotationPoint(5F, 2.0F, 0.0F);
        bipedPelvic.setRotationPoint(0.0F, 12.0F, 0.1F);
        bipedRightLeg.setRotationPoint(-1.9F, 0.0F, 0.0F);
        bipedLeftLeg.setRotationPoint(1.9F, 0.0F, 0.0F);
    }

    private void popMatrix() {
        if (doGLMatrixPop)
            GL11.glPopMatrix();
    }

    private void animateHeadRotation(float headYawAngle, float headPitchAngle, boolean elytraFlying) {
        if (!isStandardAnimation) { return; }
        bipedNeck.ignoreBase = !elytraFlying;
        bipedHead.rotateAngleY = (elytraFlying ? 0 : rotationYaw + headYawAngle) / RadianToAngle;
        bipedHead.rotateAngleX = elytraFlying ? -Eighth : headPitchAngle / RadianToAngle;
    }

    private void animateSleeping() {
        if (!isStandardAnimation) { return; }
        bipedNeck.ignoreBase = false;
        bipedHead.rotateAngleY = 0F;
        bipedHead.rotateAngleX = Eighth;
        bipedTorso.rotationPointZ = -17F;
    }

    private void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed, float elytraMagnitude) {
        bipedRightArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 2.0F * currentHorizontalSpeed * 0.5F / elytraMagnitude;
        bipedLeftArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 2.0F * currentHorizontalSpeed * 0.5F / elytraMagnitude;

        bipedRightLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 1.4F * currentHorizontalSpeed / elytraMagnitude;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 1.4F * currentHorizontalSpeed / elytraMagnitude;
    }

    private void animateRiding() {
        if (!isStandardAnimation) { return; }
        bipedRightArm.rotateAngleX += -0.6283185F;
        bipedLeftArm.rotateAngleX += -0.6283185F;
        bipedRightLeg.rotateAngleX = -1.256637F;
        bipedLeftLeg.rotateAngleX = -1.256637F;
        bipedRightLeg.rotateAngleY = 0.3141593F;
        bipedLeftLeg.rotateAngleY = -0.3141593F;
    }

    private void animateLeftArmItemHolding() {
        if (!isStandardAnimation) { return; }
        bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - 0.3141593F;
    }

    private void animateRightArmItemHolding() {
        if (!isStandardAnimation) { return; }
        bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - 0.3141593F;
    }

    private void animateSwinging(Entity entity) {
        float angle = MathHelper.sin(MathHelper.sqrt(model.swingProgress) * Whole) * 0.2F;
        bipedBreast.rotateAngleY = bipedBody.rotateAngleY += angle;
        bipedBreast.rotationOrder = bipedBody.rotationOrder = ModelRotationRenderer.RotationOrder.YXZ;
        bipedLeftArm.rotateAngleX += angle;

        EnumHandSide enumhandside = model.getMainHand(entity);
        if (enumhandside == EnumHandSide.LEFT)
            this.bipedBody.rotateAngleY *= -1.0F;

        float f1 = 1.0F - model.swingProgress;
        f1 = f1 * f1;
        f1 = f1 * f1;
        f1 = 1.0F - f1;
        float f2 = MathHelper.sin(f1 * Half);
        float f3 = MathHelper.sin(model.swingProgress * Half) * -(this.bipedHead.rotateAngleX - 0.7F) * 0.75F;

        ModelRenderer modelrenderer = this.getArmForSide(enumhandside);
        modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
        modelrenderer.rotateAngleY += this.bipedBody.rotateAngleY * 2.0F;
        modelrenderer.rotateAngleZ += MathHelper.sin(model.swingProgress * Half) * -0.4F;
    }

    private void animateSneaking() {
        if (!isStandardAnimation) { return; }
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

    private void animateArms(float totalTime) {
        if (!isStandardAnimation) { return; }
        bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
    }

    private void animateBowAimingLeft(float totalTime) {
        bipedRightArm.rotateAngleZ = 0.0F;
        bipedLeftArm.rotateAngleZ = 0.0F;
        bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - bipedOuter.rotateAngleY - 0.4F;
        bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY - bipedOuter.rotateAngleY;
        bipedRightArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
        bipedLeftArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
        bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
    }

    private void animateBowAimingRight(float totalTime) {
        bipedRightArm.rotateAngleZ = 0.0F;
        bipedLeftArm.rotateAngleZ = 0.0F;
        bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - bipedOuter.rotateAngleY;
        bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY - bipedOuter.rotateAngleY + 0.4F;
        bipedRightArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
        bipedLeftArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
        bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
    }

    private void animateElytraFlying(EntityLivingBase entity, float partialTicks, float totalTime) {
        bipedTorso.rotationOrder = RotationOrder.YZX;
        bipedTorso.rotationPointY = 23.5F;
        bipedTorso.offsetY = -1.15f;
        float f = entity.getTicksElytraFlying() + partialTicks;
        float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
        bipedTorso.rotateAngleX = f1 * (Quarter + entity.rotationPitch / RadianToAngle);
        Vec3d vec3d = entity.getLook(partialTicks);
        double d0 = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
        double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;

        if (d0 > 0.0D && d1 > 0.0D) {
            double d2 = (entity.motionX * vec3d.x + entity.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
            double d3 = entity.motionX * vec3d.z - entity.motionZ * vec3d.x;
            bipedTorso.rotateAngleY = (float) (Math.signum(d3) * Math.acos(d2));
        }

        bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
    }

    private void setArmScales(float rightScale, float leftScale) {
        if (scaleArmType == ScaleType.Scale) {
            bipedRightArm.scaleY = rightScale;
            bipedLeftArm.scaleY = leftScale;
        } else if (scaleArmType == ScaleType.NoScaleEnd) {
            bipedRightArm.offsetY -= (1F - rightScale) * 0.5F;
            bipedLeftArm.offsetY -= (1F - leftScale) * 0.5F;
        }
    }

    private void setLegScales(float rightScale, float leftScale) {
        if (scaleLegType == ScaleType.Scale) {
            bipedRightLeg.scaleY = rightScale;
            bipedLeftLeg.scaleY = leftScale;
        } else if (scaleLegType == ScaleType.NoScaleEnd) {
            bipedRightLeg.offsetY -= (1F - rightScale) * 0.5F;
            bipedLeftLeg.offsetY -= (1F - leftScale) * 0.5F;
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

    private ModelRenderer getArmForSide(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
    }

    public ModelRenderer getRandomModelBox(Random rand) {
        List<ModelRenderer> boxList = model.boxList;
        int size = boxList.size();
        int renderersWithBoxes = 0;
        int[] stack = new int[size + 1];

        for (int i = 0; i < size; i++) {
            ModelRenderer renderer = boxList.get(i);
            if (canBeRandomBoxSource(renderer))
                stack[renderersWithBoxes++] = i;
        }

        if (renderersWithBoxes != 0) {
            int randInt = rand.nextInt(renderersWithBoxes);
            for (int i = 0; i < renderersWithBoxes; i++) {
                ModelRenderer renderer = boxList.get(stack[i]);
                if (i == randInt)
                    return renderer;
            }
        }

        return null;
    }

    private static boolean canBeRandomBoxSource(ModelRenderer renderer) {
        return renderer.cubeList != null && renderer.cubeList.size() > 0 &&
                (!(renderer instanceof ModelRotationRenderer) || ((ModelRotationRenderer) renderer).canBeRandomBoxSource());
    }
}
