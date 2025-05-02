package top.andyshen2006.icesumo

import org.bukkit.command.CommandSender

object MessageUtils {
    fun sendMessage(sender: CommandSender,message: String) {
        sender.sendMessage("[冰上相扑] $message")
    }
}