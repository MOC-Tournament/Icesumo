package top.andyshen2006.icesumo

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CheckinCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when {
            (args != null) && (args.size == 1) && (sender.hasPermission("icesumo.referee")) -> {   //强制检录
                val playerName = args[0]
                val player = Bukkit.getPlayer(playerName)
                if (player == null) {
                    sender.sendMessage("该玩家不存在")
                    return false
                }
                sender.sendMessage("正在检录$playerName")
                if (UniversalDataManager.addCheckinPlayer(player)) {
                    sender.sendMessage(playerName + "检录成功！")
                } else {
                    sender.sendMessage(playerName + "你已经检录！")
                }
                return true
            }
            (sender is Player)&&(sender.hasPermission("icesumo.athlete"))-> {
                val player = sender
                val playerName = sender.name
                sender.sendMessage("正在检录$playerName")
                if (UniversalDataManager.addCheckinPlayer(player)) {
                    sender.sendMessage(playerName + "检录成功！")
                } else {
                    sender.sendMessage(playerName + "你已检录过了！")
                }
                return true
            }

            else -> {
                sender.sendMessage("检录对象非法/格式错误/没有相关权限（运动员/裁判员）")
                return false
            }
        }

    }
}

class CheckinResultCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!sender.hasPermission("icesumo.referee")) {
            sender.sendMessage("本命令需要裁判员执行，你没有权限执行")
            return false
        }
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage("§9===============================")
            player.sendMessage("§b冰上相扑 - 检录结果")
            val checkinList = UniversalDataManager.checkinList
            for (checkinPlayers in checkinList) {
                val playerName = checkinPlayers.name
                player.sendMessage(playerName)
            }
            player.sendMessage("§b\n请已检录运动员确保自身生命值和饥饿值已恢复正常。\n参赛运动员，如果您的名字没有显示在刚才的列表中，请立刻联系裁判员！")
            player.sendMessage("§9===============================")
        }
        return true
    }
}

class CheckinlistCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!sender.hasPermission("icesumo.referee")) {
            sender.sendMessage("本命令需要裁判员执行，你没有权限执行")
            return false
        }
        sender.sendMessage("§b冰上相扑 - 检录列表")
        val checkinList = UniversalDataManager.checkinList
        for (checkinPlayers in checkinList) {
            val playerName = checkinPlayers.name
            sender.sendMessage(playerName)
        }
        if (UniversalDataManager.isListValid()) {
            sender.sendMessage("当前检录玩家人数${checkinList.size}人已符合要求，可以开始比赛")
        } else {
            sender.sendMessage("当前检录玩家人数${checkinList.size}人不符合要求，不能开始比赛")
        }
        return true
    }
}

class UncheckinCommandExecutor : CommandExecutor {  //TODO: 应当添加功能：控制台可以强行取消某个玩家的检录
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when {
            (args != null) && (args.size == 1) && (sender.hasPermission("icesumo.referee")) -> {   //强制取消检录
                val playerName = args[0]
                val player = Bukkit.getPlayer(playerName)
                if (player == null) {
                    sender.sendMessage("该玩家不存在")
                    return false
                }
                sender.sendMessage("正在取消检录$playerName")
                if (UniversalDataManager.delCheckinPlayer(player)) {
                    sender.sendMessage(playerName + "取消检录成功！")
                } else {
                    sender.sendMessage(playerName + "你没有检录过！")
                }
                return true

            }
            (sender is Player)&&(sender.hasPermission("icesumo.athlete")) -> {
                val player = sender
                val playerName = sender.name
                sender.sendMessage("正在取消检录$playerName")
                if (UniversalDataManager.delCheckinPlayer(player)) {
                    sender.sendMessage(playerName + "取消检录成功！")
                } else {
                    sender.sendMessage(playerName + "你没有检录过！")
                }
                return true
            }

            else -> {
                sender.sendMessage("检录对象非法/格式错误/没有相关权限（运动员/裁判员）")
                return false
            }
        }
    }
}