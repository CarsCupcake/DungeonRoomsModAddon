package io.github.quantizr.dungeonrooms.commands;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.forge.ForgeWorld;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DownloadRoom extends CommandBase {
    @Override
    public String getCommandName() {
        return "download";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/download";
    }
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        if(!(iCommandSender instanceof EntityPlayer))
            return;
        RoomDetection.updateCurrentRoom();
        System.out.println(RoomDetection.roomName);
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = (EntityPlayer) iCommandSender;
        System.out.println(player);
        System.out.println(player.playerLocation);

        save(player);
    }
    private static List<EnumFacing> rotating(){
        ArrayList<EnumFacing> facings = new ArrayList<>();
        String d = RoomDetection.roomDirection;
        if(getFromLetter(RoomDetection.roomDirection.charAt(0)) != null && getFromLetter(RoomDetection.roomDirection.charAt(1)) != null)
            d = "NW";

        facings.add(getFromLetter(d.charAt(0)));
        facings.add(getFromLetter(d.charAt(1)));
        facings.add(getFromLetter(d.charAt(0)).getOpposite());
        facings.add(getFromLetter(d.charAt(1)).getOpposite());
        return facings;
    }
    private static EnumFacing getFromLetter(char c){
        switch (c){
            case 'N': return EnumFacing.NORTH;
            case 'E': return EnumFacing.EAST;
            case 'S': return EnumFacing.SOUTH;
            case 'W': return EnumFacing.WEST;
        }
        return null;
    }
    public static void save(EntityPlayer player){
        Minecraft minecraft = Minecraft.getMinecraft();
        Thread thread = new Thread(()->
        {
            BlockPos base = new BlockPos(player.chasingPosX, 0, player.chasingPosZ);
            while (minecraft.theWorld.getBlockState(base).getBlock().equals(Blocks.air))
                base = base.up();
            System.out.println("Base: " + base);
            BlockPos pos1 = base.west();
            BlockPos pos2 = base.north();
            List<EnumFacing> facings = rotating();
            while (!minecraft.theWorld.isAirBlock(pos1.offset(facings.get(0))))
                pos1 = pos1.offset(facings.get(0));
            while (!minecraft.theWorld.isAirBlock(pos1.offset(facings.get(1))))
                pos1 = pos1.offset(facings.get(1));
            pos1 = new BlockPos(pos1.getX(), 0, pos1.getZ());
            System.out.println("Pos1: " + pos1);
            while (!minecraft.theWorld.isAirBlock(pos2.offset(facings.get(2))))
                pos2 = pos2.offset(facings.get(2));
            while (!minecraft.theWorld.isAirBlock(pos2.offset(facings.get(3))))
                pos2 = pos2.offset(facings.get(3));
            while (!minecraft.theWorld.isAirBlock(pos2.up()))
                pos2 = pos2.up();
            System.out.println("Pos2: " + pos2);
            CuboidRegion region = new CuboidRegion(new Vector(pos1.getX(), pos1.getY(), pos1.getZ()), new Vector(pos2.getX(), pos2.getY(), pos2.getZ()));
            ForgeWorld world = ForgeWorldEdit.inst.getWorld(player.worldObj);
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);

            Clipboard clipboard = new BlockArrayClipboard(region);
            clipboard.setOrigin(new Vector(pos1.getX(), pos1.getY(), pos1.getZ()));
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard, world.getWorldData());
            int rotation;
            switch(RoomDetection.roomDirection){
                case "NE":
                    rotation = -90;
                    break;
                case "SE":
                    rotation = 180;
                    break;
                case "SW":
                    rotation = 90;
                    break;
                default:
                    rotation = 0;
                    break;
            }
            clipboardHolder.setTransform(clipboardHolder.getTransform().combine(new AffineTransform().rotateY(rotation)));
            clipboard = clipboardHolder.getClipboard();
            ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboardHolder.getClipboard(), region.getMinimumPoint());
            try {
                Operations.complete(copy);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }
            File dir = new File(DungeonRooms.schmaticDir, RoomDetection.roomCategory + "-" + RoomDetection.roomName + ".schematic");
            try (ClipboardWriter writer = ClipboardFormat.SCHEMATIC.getWriter(Files.newOutputStream(dir.toPath()))) {
                writer.write(clipboard, world.getWorldData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            player.addChatMessage(new ChatComponentText("§aSaved schematic: " + RoomDetection.roomName));
        });
        thread.start();
    }

}
