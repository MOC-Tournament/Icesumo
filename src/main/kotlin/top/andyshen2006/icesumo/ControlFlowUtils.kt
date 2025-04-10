package top.andyshen2006.icesumo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class StartCommandExecutor : CommandExecutor, Listener {
    @Suppress("Deprecation")
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        val plugin = Bukkit.getPluginManager().getPlugin("IceSumo") as Icesumo
        if (!sender.hasPermission("icesumo.referee")) { //权限控制
            sender.sendMessage("你没有执行该命令的权限：该命令只允许裁判员执行")
            return false
        }
        if (UniversalDataManager.start()) {
            sender.sendMessage("正在启动比赛……")
            if (UniversalDataManager.isListValid()) {
                for (i in 0..(UniversalDataManager.checkinList.size - 1)) {  //自动传送
                    val player = UniversalDataManager.checkinList[i]
                    val stadiumPos = UniversalDataManager.getStadiumPos(i)
                    val location = Location(
                        player.world,
                        stadiumPos.first,
                        stadiumPos.second,
                        stadiumPos.third,
                        player.yaw,
                        player.pitch
                    )
                    player.teleport(location)
                    Bukkit.dispatchCommand(player,"kit icesumo")
                }
            } else {
                sender.sendMessage("人数不符合要求，无法启动比赛")
                return false
            }
            Bukkit.getScheduler().runTask(plugin, Runnable {
                CoroutineScope(Dispatchers.Default).launch {    //协程，启动！
                    val countdownJob = UniversalDataManager.checkinList.map { player ->            //赛前倒计时
                        async { countdownBeforeGame(player) }
                    }
                    countdownJob.awaitAll()
                    UniversalDataManager.preparing=false
                    listOf(::allOnlineCheck, ::winningCheck, ::countdown).forEach { check ->
                        launch { check() }
                    }

                }
            })
        } else {
            sender.sendMessage("你不能开始一场已经开始的比赛")
            return false
        }
        return true
    }

    @Suppress("DEPRECATION")//TODO:sendTitle被Paper弃用（未被Spigot弃用），应当替换为showTitle，但是必要性不大
    suspend fun countdownBeforeGame(player: Player) {   // 协程函数，可以被挂起
        UniversalDataManager.preparing=true
        player.sendTitle("比赛准备开始", "", 10, 20, 10)
        delay(400)
        player.sendTitle("比赛准备开始", "", 10, 20, 10)
        delay(400)
        player.sendTitle("比赛准备开始", "", 10, 20, 10)
        delay(400)
        player.sendTitle("倒计时：", "", 10, 20, 10)
        for (i in 5 downTo 0) {
            UniversalDataManager.checkinList.forEach { player ->
                player.sendTitle(i.toString(), "", 10, 20, 10)
            }
            delay(1000)
        }
        player.sendTitle("比赛开始", "", 10, 20, 10)
        UniversalDataManager.preparing=false
    }

    @EventHandler
    fun moveForbiddenExecutor(event: PlayerMoveEvent) {
        event.isCancelled = UniversalDataManager.isPreparing()
    }

    @Suppress("Deprecation", "UnstableApiUsage")
    suspend fun winningCheck() {
        //胜利检测
        while (true) {
            if (UniversalDataManager.isEnd()==1) {
                var winner: Player? = null
                UniversalDataManager.checkinList.forEach { player ->
                    if (!UniversalDataManager.isFail(player)) {
                        winner = player
                    }
                }
                for (player in UniversalDataManager.checkinList) {
                    player.sendTitle("比赛结束", "", 10, 20, 10)
                    player.sendTitle("胜利者为：${winner?.name}", "", 20, 20, 20)//宣布胜利者
                    Bukkit.getLogger().info("胜利者为：${winner?.name}")
                }
                UniversalDataManager.stop()
                break
            }else if(UniversalDataManager.isEnd()==-1) {
                for (player in UniversalDataManager.checkinList) {
                    player.sendTitle("比赛结束", "", 10, 20, 10)
                    player.sendTitle("本轮未决出胜者", "", 20, 20, 20)//宣布未胜利
                }
                UniversalDataManager.stop()
                break
            }
            if (!UniversalDataManager.isStart) {  //比赛已经终止
                break
            }
            delay(100)
        }
    }

    @Suppress("Deprecation")
    suspend fun countdown() {
        val time: Long = 300//TODO：暂定300s，应当可以在配置文件中被修改
        for (i in time downTo 5) {
            if (!UniversalDataManager.isStart) {  //比赛已经终止
                break
            }
            UniversalDataManager.checkinList.forEach { player ->
                player.sendActionBar("$i")
            }
            delay(1000)
        }
        for (i in 5 downTo 0) {
            if (!UniversalDataManager.isStart) {  //比赛已经终止
                break
            }
            UniversalDataManager.checkinList.forEach { player ->
                player.sendTitle("$i", "", 10, 20, 10)
                delay(1000)
            }
        }
        UniversalDataManager.checkinList.forEach { player ->
            player.sendTitle("时间到！", "", 10, 20, 10)
            player.sendTitle("比赛结束！", "", 10, 20, 10)
        }
        UniversalDataManager.stop()//时间到，终止比赛
    }

    suspend fun allOnlineCheck() {    //对
        //如果中途有玩家退出->紧急终止
        while (true) {
            UniversalDataManager.checkinList.forEach { player ->
                if (!player.isOnline && !UniversalDataManager.isFail(player)) {   //比赛中玩家+中途退出
                    UniversalDataManager.stop()    //需要终止游戏
                }
            }
            if (!UniversalDataManager.isStart) {  //比赛已经终止，结束监听
                break
            }
            delay(100)  //间隔2tick再次检测
        }
    }

}

class TerminateCommandExecutor : CommandExecutor {  // TODO:1.加上原因的记录 2.加上部分自动判定（如玩家中途退出等）
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage("比赛被紧急终止，原因：")
        UniversalDataManager.stop()
        return true
    }
}