package uk.co.qmunity.lib.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import uk.co.qmunity.lib.helper.PlayerHelper;
import uk.co.qmunity.lib.helper.SystemInfoHelper;
import uk.co.qmunity.lib.helper.TeleportHelper;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Quetzi on 26/02/15.
 */
public class CommandQLib extends CommandBase {

    @Override
    public String getName() {
        return "qlib";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/qlib getuuid|tps|uptime";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        } else if (args[0].equalsIgnoreCase("getuuid")) {
            if (args.length != 2) {
                sender.sendMessage(new TextComponentString("Usage: /qlib getuuid <player>"));
            } else {
                String UUID = server.getPlayerList().getPlayerByUsername(args[1].toLowerCase()) != null ? server.getPlayerList().getPlayerByUsername(args[1].toLowerCase()).getUniqueID().toString() : "Player not found";
                sender.sendMessage(new TextComponentString("UUID for " + args[1] + ": " + UUID));
            }
        } else if (args[0].equalsIgnoreCase("tps")) {
            if (args.length == 1) {
                sendTextLines(sender, SystemInfoHelper.getTPSSummary());
            } else if (args.length == 2) {
                int dimension;
                try {
                    dimension = NumberFormat.getInstance().parse(args[1]).intValue();
                    sendTextLines(sender, SystemInfoHelper.getTPSDetail(dimension));
                } catch (ParseException e1) {
                    sender.sendMessage(new TextComponentString("Invalid dimension ID."));
                }
            } else {
                sender.sendMessage(new TextComponentString("Usage: /qlib tps [dimension number]"));
            }
        } else if (args[0].equalsIgnoreCase("uptime")) {
            sender.sendMessage(new TextComponentString("Uptime: " + SystemInfoHelper.getUptime()));
            sender.sendMessage(new TextComponentString("Memory Usage: " + SystemInfoHelper.getAllocatedMem() + "/" + SystemInfoHelper.getMaxMem() + "[" + SystemInfoHelper.getPercentMemUse() + "%]"));
        } else if (args[0].equalsIgnoreCase("tp")) {
            if (sender.getName().equals("Server") || PlayerHelper.isOpped(sender.getName())) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("showqueue")) {
                        if (TeleportHelper.teleportQueue.getQueue().size() > 0) {
                            String list = "";
                            for (String line : TeleportHelper.teleportQueue.getQueue()) {
                                if (!list.equals("")) {
                                    list = list + ", " + line;
                                } else {
                                    list = line;
                                }
                            }
                            sender.sendMessage(new TextComponentString("Queued players: " + list));
                        }
                    } else if (args[1].equalsIgnoreCase("clearqueue")) {
                        TeleportHelper.teleportQueue.clearQueue();
                        sender.sendMessage(new TextComponentString("Teleport queue has been cleared."));
                    } else if (TeleportHelper.sendToDefaultSpawn(args[1].toLowerCase())) {
                        sender.sendMessage(new TextComponentString(args[1] + " moved to their spawn."));
                    } else {
                        sender.sendMessage(new TextComponentString(args[1] + " not online, added to the queue for processing when next online."));
                    }
                } else if (args.length == 3) {
                    int dim = parseInt(args[2]);
                    if (TeleportHelper.sendToDimension(args[1], dim)) {
                        sender.sendMessage(new TextComponentString("Moved " + args[1] + " to dimension " + dim));
                    } else {
                        sender.sendMessage(new TextComponentString(args[1] + " is not online, added to queue."));
                    }
                } else if (args.length == 6) {
                    int dim = parseInt(args[2]);
                    int x = parseInt(args[3]);
                    int y = parseInt(args[4]);
                    int z = parseInt(args[5]);
                    if (TeleportHelper.movePlayer(args[1], dim, new BlockPos(x, y, z))) {
                        sender.sendMessage(new TextComponentString(args[1] + " moved to dimension " + dim + ": " + x + ", " + ", " + y + ", " + z));
                    } else {
                        sender.sendMessage(new TextComponentString(args[1] + " is offline, added to queue."));
                    }
                } else {
                    sender.sendMessage(new TextComponentString("Usage: /qlib tp <player> [dim] [x] [y] [z]"));
                }
            } else {
                sender.sendMessage(new TextComponentString("You do not have permission to use this command."));
            }
        }
    }

    private void sendTextLines(ICommandSender sender, List<String> text) {

        for (String line : text) {
            sender.sendMessage(new TextComponentString(line));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {

        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
