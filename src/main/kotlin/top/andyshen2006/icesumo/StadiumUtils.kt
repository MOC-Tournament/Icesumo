package top.andyshen2006.icesumo

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class EditHeightCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!sender.hasPermission("icesumo.stadium")) {
            MessageUtils.sendMessage(sender,"你没有执行该命令的权限：该命令只允许场地管理员执行")
            return false
        }
        val newHeight= args?.get(0)?.toInt()
        if (newHeight == null) {
            MessageUtils.sendMessage(sender,"判定高度不能为空！")
        }else{
            MessageUtils.sendMessage(sender,"正在修改判定高度，原高度为：${UniversalDataManager.height}，现高度为：$newHeight")
            UniversalDataManager.height=newHeight
        }
        return true
    }
}


class EditStadiumPositionExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when{
            !sender.hasPermission("icesumo.stadium") -> {
                MessageUtils.sendMessage(sender,"你没有执行该命令的权限：该指令只允许场地管理员执行")
            }
            args == null -> {
                MessageUtils.sendMessage(sender,"执行该命令必须要有参数")
                return false
            }
            args.size != 4 -> {
                MessageUtils.sendMessage(sender,"指令参数个数不符合要求")
                return false
            }
            else -> {//TODO：可能要增加额外的参数合法性判断，但是由于该命令为管理员命令，所以可以不做严格要求
                val stadiumPos= UniversalDataManager.getStadiumPos(args[0].toInt())
                MessageUtils.sendMessage(sender,"正在修改出生位置${args[0]}：原值为：${stadiumPos.first},${stadiumPos.second},${stadiumPos.third},现值为：${args[1]},${args[2]},${args[3]}")
                UniversalDataManager.setStadiumPos(args[0].toInt(), Triple(args[1].toDouble(),args[2].toDouble(),args[3].toDouble()))
            }
        }
        return true
    }
}

class EditGravePositionExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when{
            !sender.hasPermission("icesumo.stadium") -> {
                MessageUtils.sendMessage(sender,"你没有执行该命令的权限：该指令只允许场地管理员执行")
            }
            args == null -> {
                MessageUtils.sendMessage(sender,"执行该命令必须要有参数")
                return false
            }
            args.size != 3 -> {
                MessageUtils.sendMessage(sender,"指令参数个数不符合要求")
                return false
            }
            else -> {//TODO：可能要增加额外的参数合法性判断，但是由于该命令为管理员命令，所以可以不做严格要求
                val gravePos= UniversalDataManager.gravePos
                MessageUtils.sendMessage(sender,"正在修改死亡位置：原值为：${gravePos.first},${gravePos.second},${gravePos.third},现值为：${args[0]},${args[1]},${args[2]}")
                UniversalDataManager.gravePos=(Triple(args[0].toDouble(),args[1].toDouble(),args[2].toDouble()))
            }
        }
        return true
    }
}
