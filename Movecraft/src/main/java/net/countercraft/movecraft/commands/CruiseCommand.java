package net.countercraft.movecraft.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.countercraft.movecraft.CruiseDirection;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.localisation.I18nSupport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.countercraft.movecraft.util.ChatUtils.MOVECRAFT_COMMAND_PREFIX;

@CommandAlias("cruise")
@CommandPermission("movecraft.commands.cruise")
public class CruiseCommand extends BaseCommand {

    @PreCommand
    public static boolean preCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player))
            return false;

        final Craft craft = CraftManager.getInstance().getCraftByPlayerName(player.getName());
        if (craft == null) {
            player.sendMessage(MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("You must be piloting a craft"));
            return false;
        }

        if (!player.hasPermission("movecraft." + craft.getType().getStringProperty(CraftType.NAME) + ".move")) {
            player.sendMessage(MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return false;
        }
        if (!craft.getType().getBoolProperty(CraftType.CAN_CRUISE)) {
            player.sendMessage(MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Cruise - Craft Cannot Cruise"));
            return false;
        }

        return true;
    }

    @Default
    @Syntax("[on|off|DIRECTION]")
    @CommandCompletion("@directions")
    @Description("Starts your craft cruising")
    public static void onCommand(Player player, String[] args) {
        if(args.length<1){ //same as no argument
            final Craft craft = CraftManager.getInstance().getCraftByPlayerName(player.getName());
            if (craft == null) {
                player.sendMessage(MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("You must be piloting a craft"));
                return;
            }
            if (!player.hasPermission("movecraft.commands") || !player.hasPermission("movecraft.commands.cruise")) {
                craft.setCruising(false);
                return;
            }

            if(craft.getCruising()){
                craft.setCruising(false);
                return;
            }
            // Normalize yaw from [-360, 360] to [0, 360]
            yawLocationCruising(player);
            return;
        }
        if (!player.hasPermission("movecraft.commands") || !player.hasPermission("movecraft.commands.cruise")) {
            player.sendMessage(MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return;
        }

        final Craft craft = CraftManager.getInstance().getCraftByPlayerName(player.getName());

        if (args[0].equalsIgnoreCase("north") || args[0].equalsIgnoreCase("n")) {
            craft.setCruiseDirection(CruiseDirection.NORTH);
            craft.setCruising(true);
            return;
        }
        if (args[0].equalsIgnoreCase("south") || args[0].equalsIgnoreCase("s")) {
            craft.setCruiseDirection(CruiseDirection.SOUTH);
            craft.setCruising(true);
            return;
        }
        if (args[0].equalsIgnoreCase("east") || args[0].equalsIgnoreCase("e")) {
            craft.setCruiseDirection(CruiseDirection.EAST);
            craft.setCruising(true);
            return;
        }
        if (args[0].equalsIgnoreCase("west") || args[0].equalsIgnoreCase("w")) {
            craft.setCruiseDirection(CruiseDirection.WEST);
            craft.setCruising(true);
            return;
        }
        if (args[0].equalsIgnoreCase("up") || args[0].equalsIgnoreCase("u")) {
            craft.setCruiseDirection(CruiseDirection.UP);
            craft.setCruising(true);
            return;
        }
        if (args[0].equalsIgnoreCase("down") || args[0].equalsIgnoreCase("d")) {
            craft.setCruiseDirection(CruiseDirection.DOWN);
            craft.setCruising(true);
            return;
        }
    }

    @Subcommand("off")
    public static void offCruising(Player player) {
        final Craft craft = CraftManager.getInstance().getCraftByPlayerName(player.getName());
        if (craft == null) {
            player.sendMessage(MOVECRAFT_COMMAND_PREFIX + I18nSupport.getInternationalisedString("You must be piloting a craft"));
            return;
        }

        craft.setCruising(false);
    }

    @Subcommand("on")
    public static void onCruising(Player player) {
        yawLocationCruising(player);
    }

    private static void yawLocationCruising(Player player) {
        final Craft craft = CraftManager.getInstance().getCraftByPlayerName(player.getName());

        float yaw = (player.getLocation().getYaw() + 360.0f);
        if (yaw >= 360.0f) {
            yaw %= 360.0f;
        }
        if (yaw >= 45 && yaw < 135) { // west
            craft.setCruiseDirection(CruiseDirection.WEST);
        } else if (yaw >= 135 && yaw < 225) { // north
            craft.setCruiseDirection(CruiseDirection.NORTH);
        } else if (yaw >= 225 && yaw <= 315){ // east
            craft.setCruiseDirection(CruiseDirection.EAST);
        } else { // default south
            craft.setCruiseDirection(CruiseDirection.SOUTH);
        }
        craft.setCruising(true);
    }
}
