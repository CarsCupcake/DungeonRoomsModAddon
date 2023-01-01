package io.github.quantizr.dungeonrooms.CustomWaypoints;

import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import io.github.quantizr.dungeonrooms.utils.RoomDetectionUtils;
import io.github.quantizr.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CustomWaypointsFile {
    private final static String file = "config/DungeonRoomsCustom.cfg";
    public static HashMap<String, Set<CustomPoint>> points = new HashMap<>();
    static Configuration config;
    public static void initWaypoints() {
        config = new Configuration(new File(file));
        try {
            config.load();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        points.clear();
        int pointer = 0;
        while (config.hasCategory(pointer + "")) {
            try{
                ConfigCategory c = config.getCategory(pointer + "");
                BlockPos p = new BlockPos(c.get("x").getInt(), c.get("y").getInt(), c.get("z").getInt());
                String name = c.get("name").getString();
                String room = c.get("room").getString();
                if (points.containsKey(room))
                    points.get(room).add(new CustomPoint(p, name));
                else {
                    Set<CustomPoint> cP = new HashSet<>();
                    cP.add(new CustomPoint(p, name));
                    points.put(room, cP);
                }
                DungeonRooms.logger.info("Loaded custom waypoint \"" + name + "\" at " + p.toString());
            }catch (Exception e){
                e.printStackTrace();
                DungeonRooms.logger.error("Failed to load custom waypoint at the point "+ pointer +"!");
            }finally {
                pointer++;
            }
        }
    }
    public static void setWaypoint(BlockPos loc, String name){
        DungeonRooms.logger.info(loc.toString());
        String room = (RoomDetection.isInBossRoom) ? RoomDetectionUtils.getBossRoomId() : RoomDetection.roomName;
        if(!RoomDetection.isInBossRoom)
            loc = MapUtils.actualToRelative(loc, RoomDetection.roomDirection, RoomDetection.roomCorner);
        int pointer = 0;
        while (config.hasCategory(pointer + ""))
            pointer++;
        config.getCategory(pointer + "").put("name", new Property("name", name, Property.Type.STRING));
        config.getCategory(pointer + "").put("x", new Property("x", loc.getX() + "", Property.Type.INTEGER));
        config.getCategory(pointer + "").put("y", new Property("y", loc.getY() + "", Property.Type.INTEGER));
        config.getCategory(pointer + "").put("z", new Property("z", loc.getZ() + "", Property.Type.INTEGER));
        config.getCategory(pointer + "").put("room", new Property("room", room, Property.Type.STRING));
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Added a new waypoint to the relative Coords: " + blockPosToString(loc)));
        config.save();
        if(points.containsKey(room))
            points.get(room).add(new CustomPoint(loc, name));
        else {
            Set<CustomPoint> cP = new HashSet<>();
            cP.add(new CustomPoint(loc, name));
            points.put(room, cP);
        }
    }
    private static String blockPosToString(BlockPos p){
        return p.getX() + " " + p.getY() + " " + p.getZ();
    }
}
