//TODO: This File is used to test my ability in using features of Kotlin. Should be removed in production
package top.andyshen2006.icesumo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import top.andyshen2006.icesumo.TempDataManager.isEnd
import top.andyshen2006.icesumo.TempDataManager.testFlag

// Test1: coroutine delay(OK)
class TestDelayCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!sender.hasPermission("icesumo.maintainer")) {
            sender.sendMessage("你没有执行该命令的权限：该命令只允许运维执行")
            return false
        }
        if (args == null) {
            return false
        }
        Bukkit.getPluginManager().getPlugin("Icesumo")?.let {
            Bukkit.getScheduler().runTask(it, Runnable {
                CoroutineScope(Dispatchers.Default).launch{
                    val delayTime= args[0].toLong()
                    sender.sendMessage("延时测试开始，延时时间：$delayTime s")
                    delay(1000*delayTime)
                    sender.sendMessage("延时结束，测试完成")
                }
            })
        }
        return true
    }
}

object TempDataManager{
    var testFlag = false
    var isEnd = true
}

// Test2: Coroutine listener(OK)
class StartListenCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("icesumo.maintainer")) {
            sender.sendMessage("你没有执行该命令的权限：该命令只允许运维执行")
            return false
        }
        Bukkit.getPluginManager().getPlugin("Icesumo")?.let {
            Bukkit.getScheduler().runTask(it, Runnable {
                CoroutineScope(Dispatchers.Default).launch{
                    isEnd = false
                    sender.sendMessage("开始监听")
                    while (!isEnd) {
                        if (testFlag) {
                            sender.sendMessage("监听到标记")
                            isEnd=true
                            testFlag =false
                            break
                        }
                        delay(100)
                    }
                }
            })
        }
        return false
    }
}

class EditFlagCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!sender.hasPermission("icesumo.maintainer")) {
            sender.sendMessage("你没有执行该命令的权限：该命令只允许运维执行")
            return false
        }
        sender.sendMessage("修改成功")
        testFlag =true
        return true
    }
}

class KitCommandExecutor : CommandExecutor {    //测试工具：强制使用dispatchCommand
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!sender.hasPermission("icesumo.maintainer")) {
            sender.sendMessage("你没有执行该命令的权限：该命令只允许运维执行")
            return false
        }
        val player = sender
        Bukkit.dispatchCommand(player,"kit testkit")
        return true
    }
}