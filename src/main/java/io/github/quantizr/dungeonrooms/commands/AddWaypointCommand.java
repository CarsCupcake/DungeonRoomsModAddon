package io.github.quantizr.dungeonrooms.commands;

import io.github.quantizr.dungeonrooms.CustomWaypoints.CustomWaypointsFile;
import io.github.quantizr.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;


public class AddWaypointCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "addroomswaypoint";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/" + getCommandName() + " <name>";
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        if(!Utils.inCatacombs) {
            iCommandSender.addChatMessage(new ChatComponentText("§cYou are not in dungeons!"));
            return;
        }
        EntityPlayer player = (EntityPlayer) iCommandSender;
        if(Minecraft.getMinecraft().objectMouseOver == null) {
            player.addChatMessage(new ChatComponentText("§cYou do not look at a block!"));
            return;
        }
        BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
        if(Minecraft.getMinecraft().objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            player.addChatMessage(new ChatComponentText("§cYou do not look at a block!"));
            return;
        }
        if(pos == null) {
            player.addChatMessage(new ChatComponentText("§cYou do not look at a block!"));
            return;
        }
        CustomWaypointsFile.setWaypoint(pos, (strings.length >= 1) ? stringFromMult(strings) : "Custom Waypoint");
    }
    private String stringFromMult(String[] s){
        StringBuilder b = new StringBuilder();
        for(String str : s)
            b.append(str).append(" ");
        b.deleteCharAt(b.length() - 1);
        return b.toString();
    }

}
