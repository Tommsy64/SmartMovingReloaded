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

package com.tommsy.smartmoving.client.renderer.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import com.tommsy.smartmoving.client.model.SmartMovingModelElytra;
import com.tommsy.smartmoving.client.model.SmartMovingModelPlayer;

public class SmartMovingLayerElytra implements LayerRenderer<AbstractClientPlayer> {

    protected final RenderLivingBase<AbstractClientPlayer> renderPlayer;
    private final SmartMovingModelElytra modelElytra;

    public SmartMovingLayerElytra(RenderLivingBase<AbstractClientPlayer> renderPlayer) {
        this.renderPlayer = renderPlayer;
        this.modelElytra = new SmartMovingModelElytra((SmartMovingModelPlayer) renderPlayer.getMainModel());
    }

    public void doRenderLayer(AbstractClientPlayer abstractClientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch, float scale) {
        ItemStack itemstack = abstractClientPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (itemstack.getItem() != Items.ELYTRA)
            return;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        if (abstractClientPlayer.isPlayerInfoSet() && abstractClientPlayer.getLocationElytra() != null)
            this.renderPlayer.bindTexture(abstractClientPlayer.getLocationElytra());
        else if (abstractClientPlayer.hasPlayerInfo() && abstractClientPlayer.getLocationCape() != null && abstractClientPlayer.isWearing(EnumPlayerModelParts.CAPE))
            this.renderPlayer.bindTexture(abstractClientPlayer.getLocationCape());
        else
            this.renderPlayer.bindTexture(LayerElytra.TEXTURE_ELYTRA);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 0.125F);
        this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, abstractClientPlayer);
        this.modelElytra.render(abstractClientPlayer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        if (itemstack.isItemEnchanted())
            LayerArmorBase.renderEnchantedGlint(this.renderPlayer, abstractClientPlayer, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw,
                    headPitch, scale);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
