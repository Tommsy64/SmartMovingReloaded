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

package com.tommsy.smartmoving.client.renderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderUtils {
    public static final float Whole = (float) Math.PI * 2F;
    public static final float Half = (float) Math.PI;
    public static final float Quarter = Half / 2F;
    public static final float Eighth = Quarter / 2F;
    public static final float Sixteenth = Eighth / 2F;
    public static final float Thirtysecond = Sixteenth / 2F;
    public static final float Sixtyfourth = Thirtysecond / 2F;

    public static final float RadianToAngle = 360F / Whole;
}
