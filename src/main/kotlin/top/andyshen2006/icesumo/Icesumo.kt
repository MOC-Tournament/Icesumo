package top.andyshen2006.icesumo

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin


@Suppress("UNUSED_PARAMETER")   //IDEA的愚蠢警告
class Icesumo : JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        logger.info("Enabling Ice sumo Plugin!")
        // Plugin startup logic
        getCommand("start")?.setExecutor(StartCommandExecutor())
        getCommand("info")?.setExecutor(InfoCommandExecutor())
        getCommand("terminate")?.setExecutor(TerminateCommandExecutor())
        // Checkins
        getCommand("checkin")?.setExecutor(CheckinCommandExecutor())
        getCommand("checkinlist")?.setExecutor(CheckinlistCommandExecutor())
        getCommand("uncheckin")?.setExecutor(UncheckinCommandExecutor())
        // Settings
        getCommand("editheight")?.setExecutor(EditHeightCommandExecutor())
        getCommand("editpos")?.setExecutor(EditStadiumPositionExecutor())
        getCommand("editgrave")?.setExecutor(EditGravePositionExecutor() )
    }

    override fun onDisable() {
        logger.info("Disabling Ice Sumo Plugin!")
        // Plugin shutdown logic
    }

    @Suppress("DEPRECATION")//TODO:sendTitle被Paper弃用（未被Spigot弃用），应当替换为showTitle，但是必要性不大
    private fun countdownBeforeGame(player: Player) {
        Bukkit.getScheduler().runTaskAsynchronously(this, Runnable {//协程
            runBlocking {
                player.sendTitle("倒计时：","",10,20,10)
                for (i in 1..10) {
                    for(player in UniversalDataManager.getCheckinList()) {
                        player.sendTitle(i.toString(),"",10,20,10)
                    }
                    delay(1000)
                }
            }
        })
    }

    @EventHandler   //TODO:诶是不是这里能用协程+runTaskAsynchronously实现，不过这样子好像就直接改写Paper事件机制了
    @Suppress("DEPRECATION")
    fun isFailed(event: PlayerMoveEvent) {//TODO:该函数需要重点处理
        val player = event.player
        val from=event.from
        val to = event.to
        if (from.y==to.y) { //未发生y方向移动
            return
        }
        if (to.y<= UniversalDataManager.getHeight()) {    //低于判定高度
            executeFailAction(player)
        }
    }

    @Suppress("DEPRECATION")
    fun executeFailAction(player: Player) {
        when{
            !UniversalDataManager.isStart() -> return //比赛还未开始
            !UniversalDataManager.isCheckin(player) -> return //该玩家没有被检录
            UniversalDataManager.isFail(player) -> return //该玩家已经失败
            else -> {
                player.sendTitle("你输了！","",10,20,10)    // TODO:需要更多失败后处理
                player.gameMode = GameMode.SPECTATOR    //设置为旁观者模式
                UniversalDataManager.playerFail(player) //在数据管理器中登记失败
            }
        }
    }
}

class StartCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (!sender.hasPermission("icesumo.referee")) {
            sender.sendMessage("你没有执行该命令的权限：该命令只允许裁判员执行")
            return false
        }
        if(UniversalDataManager.start()){
            sender.sendMessage("比赛启动成功！")

        }else {
            sender.sendMessage("你不能开始一场已经开始的比赛")
        }
        return true
    }
}


class TerminateCommandExecutor : CommandExecutor {  // TODO:1.加上原因的记录 2.加上部分自动判定（如玩家中途退出等）
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage("比赛被紧急终止，原因：")
        UniversalDataManager.stop()
        return true
    }
}
class InfoCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return true
    }
}

class EditHeightCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val newHeight= args?.get(0)?.toInt()
        if (newHeight == null) {
            sender.sendMessage("判定高度不能为空！")
        }else{
            sender.sendMessage("正在修改判定高度，原高度为：${UniversalDataManager.getHeight()}，现高度为：$newHeight")
            UniversalDataManager.editHeight(newHeight)
        }
        return true
    }
}

@Suppress("RemoveExplicitTypeArguments") //TODO:IDEA你不会报警告不要乱报，报警告你还修不好我只能说你菜就多练
class EditStadiumPositionExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        when{
            sender.hasPermission("icesumo.stadium") -> {
                sender.sendMessage("你没有执行该命令的权限：该指令只允许场地管理员执行")
            }
            args == null -> {
                sender.sendMessage("执行该命令必须要有参数")
                return false
            }
            args.size != 4 -> {
                sender.sendMessage("指令参数个数不符合要求")
                return false
            }
            else -> {//TODO：可能要增加额外的参数合法性判断，但是由于该命令为管理员命令，所以可以不做严格要求
                val stadiumPos= UniversalDataManager.getStadiumPos(args[0].toInt())
                sender.sendMessage("正在修改出生位置${args[0]}：原值为：${stadiumPos?.first},${stadiumPos?.second},${stadiumPos?.third},现值为：${args[1]},${args[2]},${args[3]}")
                UniversalDataManager.editStadiumPos(args[0].toInt(), Triple<Double, Double, Double>(args[1].toDouble(),args[2].toDouble(),args[3].toDouble()))
            }
        }
        return true
    }
}

class EditGravePositionExecutor : CommandExecutor { //TODO:死亡后位置修改
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        return true
    }
}