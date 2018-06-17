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

package com.tommsy.smartmoving.client.render.gui;

import org.lwjgl.opengl.GL11;

import com.tommsy.smartmoving.client.SmartMovingClientPlayer;
import com.tommsy.smartmoving.client.SmartMovingClientPlayerHandler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SmartMovingRenderGUI {
    public static void renderGuiIngame(Minecraft minecraft) {
        // getNativeUserInterfaceDrawing() appears to always be false
        // if (!SmartMovingContext.Client.getNativeUserInterfaceDrawing())
        // return;

        if (!GL11.glGetBoolean(GL11.GL_ALPHA_TEST))
            return;

        SmartMovingClientPlayer smPlayer = (SmartMovingClientPlayer) minecraft.player;
        SmartMovingClientPlayerHandler handle = smPlayer.getPlayerHandler();
        // if (SmartMovingContext.Config.enabled && (SmartMovingContext.Options._displayExhaustionBar.value
        // || SmartMovingContext.Options._displayJumpChargeBar.value)) {
        if (true) {
            ScaledResolution scaledresolution = new ScaledResolution(minecraft);
            int width = scaledresolution.getScaledWidth();
            int height = scaledresolution.getScaledHeight();

            if (minecraft.playerController.shouldDrawHUD()) {
                float maxExhaustion = 100.0f;// SmartMovingContext.Client.getMaximumExhaustion();
                float exhaustion = Math.min(handle.exhaustion, maxExhaustion);
                boolean drawExhaustion = exhaustion > 0 && exhaustion <= maxExhaustion;

                float maxStillJumpCharge = 20F;// SmartMovingContext.Config._jumpChargeMaximum.value;
                float stillJumpCharge = Math.min(handle.jumpCharge, maxStillJumpCharge);

                float maxRunJumpCharge = 10F;// SmartMovingContext.Config._headJumpChargeMaximum.value;
                float runJumpCharge = Math.min(handle.headJumpCharge, maxRunJumpCharge);

                boolean drawJumpCharge = stillJumpCharge > 0 || runJumpCharge > 0;
                float maxJumpCharge = stillJumpCharge > runJumpCharge ? maxStillJumpCharge : maxRunJumpCharge;
                float jumpCharge = Math.max(stillJumpCharge, runJumpCharge);

                if (drawExhaustion || drawJumpCharge) {
                    GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
                    minecraft.getTextureManager().bindTexture(new ResourceLocation("smartmoving", "gui/icons.png"));
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    _minecraft = minecraft;
                }

                if (drawExhaustion) {
                    float maxExhaustionForAction = Math.min(handle.maxExhaustionForAction, maxExhaustion);
                    float maxExhaustionToStartAction = Math.min(handle.maxExhaustionToStartAction, maxExhaustion);

                    float fitness = maxExhaustion - exhaustion;
                    float minFitnessForAction = Float.isNaN(maxExhaustionForAction) ? 0 : maxExhaustion - maxExhaustionForAction;
                    float minFitnessToStartAction = Float.isNaN(maxExhaustionToStartAction) ? 0 : maxExhaustion - maxExhaustionToStartAction;

                    float maxFitnessDrawn = Math.max(Math.max(minFitnessToStartAction, fitness), minFitnessForAction);

                    int halfs = (int) Math.floor(maxFitnessDrawn / maxExhaustion * 21F);
                    int fulls = halfs / 2;
                    int half = halfs % 2;

                    int fitnessHalfs = (int) Math.floor(fitness / maxExhaustion * 21F);
                    int fitnessFulls = fitnessHalfs / 2;
                    int fitnessHalf = fitnessHalfs % 2;

                    int minFitnessForActionHalfs = (int) Math.floor(minFitnessForAction / maxExhaustion * 21F);
                    int minFitnessForActionFulls = minFitnessForActionHalfs / 2;
                    int minFitnessForActionHalf = minFitnessForActionHalfs % 2;

                    int minFitnessToStartActionHalfs = (int) Math.floor(minFitnessToStartAction / maxExhaustion * 21F);
                    int minFitnessToStartActionFulls = minFitnessToStartActionHalfs / 2;

                    _jOffset = height - 39 - 10 - (minecraft.player.isInsideOfMaterial(Material.WATER) ? 10 : 0);
                    for (int i = 0; i < Math.min(fulls + half, 10); i++) {
                        _iOffset = (width / 2 + 90) - (i + 1) * 8;
                        if (i < fitnessFulls) {
                            if (i < minFitnessForActionFulls)
                                drawIcon(2, 2);
                            else if (i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
                                drawIcon(3, 2);
                            else
                                drawIcon(0, 0);
                        } else if (i == fitnessFulls && fitnessHalf > 0) {
                            if (i < minFitnessForActionFulls)
                                drawIcon(1, 2);
                            else if (i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
                                if (i < minFitnessToStartActionFulls)
                                    drawIcon(3, 1);
                                else
                                    drawIcon(4, 2);
                            else if (i < minFitnessToStartActionFulls)
                                drawIcon(1, 1);
                            else
                                drawIcon(1, 0);
                        } else {
                            if (i < minFitnessForActionFulls)
                                drawIcon(0, 2);
                            else if (i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
                                if (i < minFitnessToStartActionFulls)
                                    drawIcon(2, 1);
                                else
                                    drawIcon(5, 2);
                            else if (i < minFitnessToStartActionFulls)
                                drawIcon(0, 1);
                            else
                                drawIcon(4, 1);
                        }
                    }
                }

                if (drawJumpCharge) {
                    boolean max = jumpCharge == maxJumpCharge;
                    int fulls = max ? 10 : (int) Math.ceil(((jumpCharge - 2) * 10D) / maxJumpCharge);
                    int half = max ? 0 : (int) Math.ceil((jumpCharge * 10D) / maxJumpCharge) - fulls;

                    _jOffset = height - 39 - 10 - (minecraft.player.getTotalArmorValue() > 0 ? 10 : 0);
                    for (int i = 0; i < fulls + half; i++) {
                        _iOffset = (width / 2 - 91) + i * 8;
                        drawIcon(i < fulls ? 2 : 3, 0);
                    }
                }

                if (drawExhaustion || drawJumpCharge)
                    GL11.glPopAttrib();
            }
        }
    }

    private static void drawIcon(int x, int y) {
        _minecraft.ingameGUI.drawTexturedModalRect(_iOffset, _jOffset, x * 9, y * 9, 9, 9);
    }

    private static int _iOffset, _jOffset;
    private static Minecraft _minecraft;
}
