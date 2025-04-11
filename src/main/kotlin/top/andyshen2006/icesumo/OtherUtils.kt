package top.andyshen2006.icesumo

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class InfoCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("icesumo.maintainer")) {
            sender.sendMessage("你没有执行该命令的权限：该命令只允许运维执行")
            return false
        }
        sender.sendMessage(UniversalDataManager.showInfo())
        return true
    }
}