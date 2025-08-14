package com.himcbbs.play.serverclient.himcbbsauth.command;

import com.himcbbs.play.serverclient.himcbbsauth.HiMCBBSAccountAuth;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length==1) {
            if (args[0].equals("reload")) {
                HiMCBBSAccountAuth.getInstance().disable();
                HiMCBBSAccountAuth.getInstance().enable();
                sender.sendMessage(ChatColor.GREEN + "Plugin reloaded.");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Invalid arguments!");
        return false;
    }
}
