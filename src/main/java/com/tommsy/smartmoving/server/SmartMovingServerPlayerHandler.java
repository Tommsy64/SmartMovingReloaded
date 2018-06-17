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

package com.tommsy.smartmoving.server;

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class SmartMovingServerPlayerHandler {

    private final SmartMovingServerPlayer player;

    public SmartMovingServerPlayerHandler(SmartMovingServerPlayer player) {
        this.player = player;
    }

    public boolean resetFallDistance, resetTicksForFloatKick;

    public int crawlingCooldown;

    public boolean isCrawling;
    public boolean isSmall;

    private boolean isSneakButtonPressed;

    public void processStatePacket(FMLProxyPacket packet, long state) {
        // if (!initialized)
        // initialize(false);

        boolean isCrawling = ((state >>> 13) & 1) != 0;
        setCrawling(isCrawling);

        boolean isSmall = ((state >>> 15) & 1) != 0;
        setSmall(isSmall);

        boolean isClimbing = ((state >>> 14) & 1) != 0;
        boolean isCrawlClimbing = ((state >>> 12) & 1) != 0;
        boolean isCeilingClimbing = ((state >>> 18) & 1) != 0;

        boolean isWallJumping = ((state >>> 31) & 1) != 0;

        isSneakButtonPressed = ((state >>> 33) & 1) != 0;

        resetFallDistance = isClimbing || isCrawlClimbing || isCeilingClimbing || isWallJumping;
        resetTicksForFloatKick = isClimbing || isCrawlClimbing || isCeilingClimbing;
        sendPacketToTrackedPlayers(packet);
    }

    private void sendPacketToTrackedPlayers(FMLProxyPacket packet) {
        player.getMinecraftServer().getWorld(player.getDimension()).getEntityTracker().sendToTracking(player.asEntityPlayerMP(), packet);
    }

    public void setCrawling(boolean crawling) {
        if (!crawling && isCrawling)
            crawlingCooldown = 10;
        isCrawling = crawling;
    }

    public void setSmall(boolean isSmall) {
        player.setHeight(isSmall ? 0.8F : 1.8F);
        this.isSmall = isSmall;
    }
}
