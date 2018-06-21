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

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import com.tommsy.smartmoving.client.render.ModelPreviousRotationRenderer;
import com.tommsy.smartmoving.client.render.ModelRotationRenderer;

import static com.tommsy.smartmoving.client.render.RenderUtils.Eighth;
import static com.tommsy.smartmoving.client.render.RenderUtils.Half;
import static com.tommsy.smartmoving.client.render.RenderUtils.RadianToAngle;
import static com.tommsy.smartmoving.client.render.RenderUtils.Whole;

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

    public SmartMovingModelBipedHandler(SmartMovingModelBiped smModel) {
        this(smModel, true);
    }

    protected SmartMovingModelBipedHandler(SmartMovingModelBiped smModel, final boolean doGLMatrixPop) {
        model = (this.smModel = smModel).getImplementation();
        this.doGLMatrixPop = doGLMatrixPop;

        model.boxList.clear(); // Why?
        // Can't call initialize from constructor because fields are not accessible until object done constructing
    }

    public final void initialize() {
        initializeRenderers();
        reset(); // set default rotation points // Why?
    }

    protected void initializeRenderers() {
        bipedOuter = new ModelPreviousRotationRenderer(model, -1, -1, null);
        bipedOuter.fadeEnabled = true;

        bipedTorso = createRenderer(bipedOuter);
        bipedBody = createRenderer(bipedTorso, model.bipedBody);
        bipedBreast = createRenderer(bipedTorso);
        bipedNeck = createRenderer(bipedBreast);
        bipedHead = createRenderer(bipedNeck, model.bipedHead);
        bipedRightShoulder = createRenderer(bipedBreast);
        bipedRightArm = createRenderer(bipedRightShoulder, model.bipedRightArm);
        bipedLeftShoulder = createRenderer(bipedBreast);
        bipedLeftShoulder.mirror = true;
        bipedLeftArm = createRenderer(bipedLeftShoulder, model.bipedLeftArm);
        bipedPelvic = createRenderer(bipedTorso);
        bipedRightLeg = createRenderer(bipedPelvic, model.bipedRightLeg);
        bipedLeftLeg = createRenderer(bipedPelvic, model.bipedLeftLeg);

        bipedHeadwear = createRenderer(bipedHead, model.bipedHeadwear);

        model.bipedBody = bipedBody;
        model.bipedHead = bipedHead;
        model.bipedHeadwear = bipedHeadwear;
        model.bipedRightArm = bipedRightArm;
        model.bipedLeftArm = bipedLeftArm;
        model.bipedRightLeg = bipedRightLeg;
        model.bipedLeftLeg = bipedLeftLeg;
    }

    protected ModelRotationRenderer createRenderer(ModelRotationRenderer base) {
        return new ModelRotationRenderer(model, -1, -1, base);
    }

    protected ModelRotationRenderer createRenderer(ModelRotationRenderer base, ModelRenderer original) {
        ModelRotationRenderer renderer = new ModelRotationRenderer(model, original.textureOffsetX, original.textureOffsetY, base);
        renderer.copyFrom(original);
        return renderer;
    }

    public void preRender(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GL11.glPushMatrix();
        // if (entity.isSneaking())
        // GL11.glTranslatef(0.0F, 0.2F, 0.0F); // Why?

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

    private boolean isStandardAnimation;

    /**
     * @return True if {@linkplain ModelBiped#setRotationAngles(float, float, float, float, float, float, Entity)} should not be called
     */
    public boolean preSetRotationAngles(float totalHorizontalDistance, float currentHorizontalSpeed, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor,
            Entity entity) {
        reset(); // Why?

        // SmartMovingAbstractClientPlayer player = (SmartMovingAbstractClientPlayer) entity;
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

        bipedOuter.rotateAngleY = rotationYaw / RadianToAngle;
        bipedOuter.fadeRotateAngleY = !entity.isRiding();

        isStandardAnimation = false;

        // Handle smart moving state...

        isStandardAnimation = true;

        animateHeadRotation(totalHorizontalDistance, currentHorizontalSpeed, ageInTicks, headYawAngle, headPitchAngle, scaleFactor);

        if (((EntityPlayer) entity).isPlayerSleeping())
            animateSleeping();

        animateArmSwinging(totalHorizontalDistance, currentHorizontalSpeed);

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

    private void animateHeadRotation(float totalHorizontalDistance, float currentHorizontalSpeed, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor) {
        if (!isStandardAnimation) { return; }
        bipedNeck.ignoreBase = true;
        bipedHead.rotateAngleY = (rotationYaw + headYawAngle) / RadianToAngle;
        bipedHead.rotateAngleX = headPitchAngle / RadianToAngle;
    }

    private void animateSleeping() {
        if (!isStandardAnimation) { return; }
        bipedNeck.ignoreBase = false;
        bipedHead.rotateAngleY = 0F;
        bipedHead.rotateAngleX = Eighth;
        bipedTorso.rotationPointZ = -17F;
    }

    private void animateArmSwinging(float totalHorizontalDistance, float currentHorizontalSpeed) {
        bipedRightArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 2.0F * currentHorizontalSpeed * 0.5F;
        bipedLeftArm.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 2.0F * currentHorizontalSpeed * 0.5F;

        bipedRightLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F) * 1.4F * currentHorizontalSpeed;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(totalHorizontalDistance * 0.6662F + Half) * 1.4F * currentHorizontalSpeed;
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

        EnumHandSide enumhandside = this.getMainHand(entity);
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
        bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - 0.4F - bipedOuter.rotateAngleY;
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
        bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY + 0.4F - bipedOuter.rotateAngleY;
        bipedRightArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
        bipedLeftArm.rotateAngleX = -1.570796F + bipedHead.rotateAngleX;
        bipedRightArm.rotateAngleZ += MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(totalTime * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(totalTime * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(totalTime * 0.067F) * 0.05F;
    }

    private ModelRenderer getArmForSide(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
    }

    private EnumHandSide getMainHand(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
            EnumHandSide enumhandside = entitylivingbase.getPrimaryHand();
            return entitylivingbase.swingingHand == EnumHand.MAIN_HAND ? enumhandside : enumhandside.opposite();
        } else {
            return EnumHandSide.RIGHT;
        }
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
