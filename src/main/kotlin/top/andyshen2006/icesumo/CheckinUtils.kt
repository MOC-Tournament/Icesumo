package top.andyshen2006.icesumo

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CheckinCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val player=sender
            val playerName=sender.name
            sender.sendMessage("正在检录$playerName")
            if (UniversalDataManager.addCheckinPlayer(player)) {
                sender.sendMessage(playerName + "检录成功！")
            }else{
                sender.sendMessage(playerName + "你已检录过了！")
            }
            return true
        }else{
            sender.sendMessage("控制台不能检录")
            return false
        }

    }
}

class CheckinlistCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage("当前已检录玩家为：")
        val checkinList=UniversalDataManager.checkinList
        for(checkinPlayers in checkinList) {
            val playerName=checkinPlayers.name
            sender.sendMessage(playerName)
        }
        if (UniversalDataManager.isListValid()) {
            sender.sendMessage("当前检录玩家人数${checkinList.size}人已符合要求，可以开始比赛")
        }else{
            sender.sendMessage("当前检录玩家人数${checkinList.size}人不符合要求，不能开始比赛")
        }
        return true
    }
}

class UncheckinCommandExecutor : CommandExecutor {  //TODO: 应当添加功能：控制台可以强行取消某个玩家的检录
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender is Player) {
            val player=sender
            val playerName=sender.name
            sender.sendMessage("正在取消检录$playerName")
            if (UniversalDataManager.delCheckinPlayer(player)) {
                sender.sendMessage(playerName + "取消检录成功！")
            }else{
                sender.sendMessage(playerName + "你没有检录过！")
            }
            return true
        }else{
            sender.sendMessage("控制台不能检录")
            return false
        }
    }
}