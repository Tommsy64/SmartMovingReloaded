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

import java.util.Random;

import lombok.Getter;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelPlayerArmor extends ModelBiped implements SmartMovingModelBiped {

    @Getter
    protected final SmartMovingModelBipedHandler handler;

    public ModelPlayerArmor() {
        this(0);
    }

    public ModelPlayerArmor(float modelSize) {
        this(modelSize, 0, 64, 32);
    }

    public ModelPlayerArmor(float modelSize, float p_i1149_2_, int textureWidth, int textureHeight) {
        super(modelSize, p_i1149_2_, textureWidth, textureHeight);
        this.handler = new SmartMovingModelBipedHandler(this);
        handler.initialize();
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        handler.preRender(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        handler.postRender(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float headYawAngle, float headPitchAngle, float scaleFactor,
            Entity entity) {
        boolean cancel = handler.preSetRotationAngles(limbSwing, limbSwingAmount, ageInTicks, headYawAngle, headPitchAngle, scaleFactor, entity);
        if (!cancel)
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, headYawAngle, headPitchAngle, scaleFactor, entity);
        handler.postSetRotationAngles(limbSwing, limbSwingAmount, ageInTicks, headYawAngle, headPitchAngle, scaleFactor, entity);
    }

    @Override
    public ModelRenderer getRandomModelBox(Random rand) {
        return handler.getRandomModelBox(rand);
    }
}