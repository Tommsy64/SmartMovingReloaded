package com.tommsy.smartmoving.server;

import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class SmartMovingServerPlayerHandler {

    private final SmartMovingServerPlayer player;

    public SmartMovingServerPlayerHandler(SmartMovingServerPlayer player) {
        this.player = player;
    }

    public boolean resetFallDistance, resetTicksForFloatKick;

    public int crawlingCooldown;

    public boolean crawlingInitialized;
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
