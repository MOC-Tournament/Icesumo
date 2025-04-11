package top.andyshen2006.icesumo

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

//以下规则请参见：https://moc.miraheze.org/wiki/?curid=452

class CheckRuleCommandExecutor : CommandExecutor {
    @Suppress("Deprecation")
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!sender.hasPermission("icesumo.referee")) {
            sender.sendMessage("你没有执行该命令的权限：该命令只允许裁判员执行")
            return false
        }
        val result= UniversalDataManager.isRuleValid()
        if(result[0]=='F') {
            sender.sendMessage("终为白日规则检测失败")
        }
        if(result=="T") {
            sender.sendMessage("所有规则都正确，可以开始比赛")
        }else{
            sender.sendMessage("规则有错误（${result}），可以通过setrule指令自动修复")
        }
        return true
    }
}

class SetRuleCommandExecutor : CommandExecutor {
    @Suppress("Deprecation")
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!sender.hasPermission("icesumo.referee")) {
            sender.sendMessage("你没有执行该命令的权限：该命令只允许裁判员执行")
            return false
        }
        val world = UniversalDataManager.world
        world.setGameRuleValue("doDaylightCycle","false")
        world.time=6000
        sender.sendMessage("重置成功")
        return true
    }
}