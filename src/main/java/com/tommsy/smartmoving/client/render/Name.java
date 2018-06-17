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

public class Name {
    public final String obfuscated;
    public final String forgefuscated;
    public final String deobfuscated;

    public Name(String name) {
        this(name, null);
    }

    public Name(String deobfuscatedName, String obfuscatedName) {
        this(deobfuscatedName, null, obfuscatedName);
    }

    public Name(String deobfuscatedName, String forgefuscatedName, String obfuscatedName) {
        deobfuscated = deobfuscatedName;
        forgefuscated = forgefuscatedName;
        obfuscated = obfuscatedName;
    }
}