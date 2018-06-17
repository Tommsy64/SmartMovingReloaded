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

package com.tommsy.smartmoving.client.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RenderDataTracker {
    public static RendererData getPreviousRendererData(SmartMovingAbstractClientPlayer player) {
        return getPreviousRendererData((EntityPlayer) ((Object) player));
    }

    public static RendererData getPreviousRendererData(EntityPlayer entityplayer) {
        if (++previousRendererDataAccessCounter > 1000) {
            List<EntityPlayer> players = Minecraft.getMinecraft().world.playerEntities;

            Iterator<EntityPlayer> iterator = previousRendererData.keySet().iterator();
            while (iterator.hasNext())
                if (!players.contains(iterator.next()))
                    iterator.remove();

            previousRendererDataAccessCounter = 0;
        }

        RendererData result = previousRendererData.get(entityplayer);
        if (result == null)
            previousRendererData.put(entityplayer, result = new RendererData());
        return result;
    }

    private static Map<EntityPlayer, RendererData> previousRendererData = new HashMap<EntityPlayer, RendererData>();
    private static int previousRendererDataAccessCounter = 0;
}
