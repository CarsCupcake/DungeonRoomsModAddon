package io.github.quantizr.dungeonrooms.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.event.ClickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.UUID;

public class CopyAttributes extends CommandBase {
    private static final Minecraft mc = Minecraft.getMinecraft();
    @Override
    public String getCommandName() {
        return "copynbt";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/<command>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) throws CommandException {
        if(strings.length == 0){
            Vec3 vec = times(mc.thePlayer.getLookVec().normalize(), 5);
            Vec3 base = mc.thePlayer.getLookVec().normalize();
            Vec3 m1 = times(base.rotateYaw(90f), 0.75);
            Vec3 m2 = times(base.rotateYaw(-90f), 0.75).add(vec);
            double x = mc.thePlayer.posX;
            double y = mc.thePlayer.posY;
            double z = mc.thePlayer.posZ;
            AxisAlignedBB bb = new AxisAlignedBB(x + m1.xCoord, y + m1.yCoord, z + m1.zCoord, x + m2.xCoord, y + 2 + m2.yCoord, z + m2.zCoord);
            mc.theWorld.getEntitiesWithinAABB(EntityLivingBase.class, bb, (input) -> input.getEntityId() != mc.thePlayer.getEntityId() && !(input.getName().contains("❤"))).forEach(entityLivingBase -> {
                        IChatComponent c = new ChatComponentText("Entitys nbt from " + entityLivingBase.getName() + " (" + entityLivingBase.getClass().getSimpleName() + ")");
                        ChatComponentText clickable = new ChatComponentText("§a[Click Here]");
                        clickable.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copynbt " + entityLivingBase.getEntityId())));
                        c.appendSibling(clickable);
                        mc.thePlayer.addChatMessage(c);
                    });
            return;
        }
        if(strings.length == 1) {
            Entity e = mc.theWorld.getEntityByID(Integer.parseInt(strings[0]));
            NBTTagCompound compound = new NBTTagCompound();
            e.writeToNBT(compound);
            StringBuilder b = new StringBuilder(compound.toString());
            if(e instanceof EntityOtherPlayerMP)
                ((EntityOtherPlayerMP) e).getGameProfile().getProperties().get("textures").stream().filter(property -> property.getName().equals("textures"))
                        .forEach(property -> b.append("\n").append("Texture: ").append(property.getValue()).append("\n").append("Signature: ").append(property.getSignature()));

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b.toString()), null);
        }
    }
    private Vec3 times(Vec3 vec3, double times){
        return new Vec3(vec3.xCoord * times, vec3.yCoord * times, vec3.zCoord * times);
    }
}
