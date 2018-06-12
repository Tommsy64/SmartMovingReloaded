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

package com.tommsy.smartmoving.client;

import com.tommsy.smartmoving.common.AbstractSmartMovingPlayerHandler;

import net.minecraft.block.Block;
import net.minecraft.util.math.MathHelper;

public class SmartMovingOtherPlayerHandler extends AbstractSmartMovingPlayerHandler {

    private final SmartMovingOtherPlayer player;

    public SmartMovingOtherPlayerHandler(SmartMovingOtherPlayer player) {
        super(player);
        this.player = player;
    }

    @Override
    public double getOverGroundHeight(double maximum) {
        return (getBoundingBox().minY + 1D - getMaxPlayerSolidBetween(getBoundingBox().minY - maximum + 1D, getBoundingBox().minY + 1D, 0.1));
    }

    @Override
    public Block getOverGroundBlockId(double distance) {
        int x = MathHelper.floor(player.getPosX());
        int y = MathHelper.floor(getBoundingBox().minY);
        int z = MathHelper.floor(player.getPosZ());
        int minY = y - (int) Math.ceil(distance);

        y++;
        minY++;

        for (; y >= minY; y--) {
            Block block = getBlock(x, y, z);
            if (block != null)
                return block;
        }
        return null;
    }
}
