package top.andyshen2006.icesumo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class StartCommandExecutor : CommandExecutor, Listener {
    @Suppress("Deprecation", "UnstableApiUsage")
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        val plugin = Bukkit.getPluginManager().getPlugin("IceSumo") as Icesumo
        when{
            !sender.hasPermission("icesumo.referee") -> {
                MessageUtils.sendMessage(sender,"你没有执行该命令的权限：该命令只允许裁判员执行")
                return false
            }
            !UniversalDataManager.isListValid() -> {
                MessageUtils.sendMessage(sender,"人数不符合要求，无法启动比赛")
                return false
            }
            UniversalDataManager.isRuleValid()!="T" -> {
                MessageUtils.sendMessage(sender,"服务器规则不符合要求，无法启动比赛")
            }
            UniversalDataManager.start() -> {   //规则请参见：https://moc.miraheze.org/wiki/?curid=452
                MessageUtils.sendMessage(sender,"正在启动比赛……")
                for (i in 0..(UniversalDataManager.checkinList.size - 1)) {  //自动传送
                    val player = UniversalDataManager.checkinList[i]
                    val stadiumPos = UniversalDataManager.getStadiumPos(i)
                    val location = Location(
                        UniversalDataManager.world,
                        stadiumPos.first,
                        stadiumPos.second,
                        stadiumPos.third,
                        player.yaw,
                        player.pitch
                    )
                    player.teleport(location)
                    player.gameMode= GameMode.ADVENTURE //设置为冒险模式
                    player.activePotionEffects.forEach{ //移除所有效果
                        player.removePotionEffect(it.type)
                    }
//                    Attribute.entries.forEach {attribute -> //移除所有属性
//                        val attributeInstance=player.getAttribute(attribute)
//                        if (attributeInstance!=null){
//                            attributeInstance.baseValue=attributeInstance.defaultValue
//                        }
//                    }
                    // Reference: https://github.com/MOC-Tournament/StatCleaner/blob/main/src/main/java/org/moc/statCleaner/command/CommandReset.java
                    // TODO: 应该替换为上面的Bukkit API
                    setAttribute(player, Attribute.GENERIC_ARMOR, 0.0)
                    setAttribute(player, Attribute.GENERIC_ARMOR_TOUGHNESS, 0.0)
                    setAttribute(player, Attribute.GENERIC_ATTACK_DAMAGE, 1.0)
                    setAttribute(player, Attribute.GENERIC_ATTACK_KNOCKBACK, 0.0)
                    setAttribute(player, Attribute.GENERIC_ATTACK_SPEED, 4.0)
                    setAttribute(player, Attribute.GENERIC_BURNING_TIME, 1.0)
                    setAttribute(player, Attribute.GENERIC_EXPLOSION_KNOCKBACK_RESISTANCE, .00)
                    setAttribute(player, Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER, 1.0)
                    setAttribute(player, Attribute.GENERIC_GRAVITY, 0.08)
                    setAttribute(player, Attribute.GENERIC_JUMP_STRENGTH, 0.42)
                    setAttribute(player, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0)
                    setAttribute(player, Attribute.GENERIC_LUCK, 0.0)
                    setAttribute(player, Attribute.GENERIC_MAX_ABSORPTION, 0.0)
                    setAttribute(player, Attribute.GENERIC_MOVEMENT_SPEED, 0.1)
                    setAttribute(player, Attribute.GENERIC_OXYGEN_BONUS, 0.0)
                    setAttribute(player, Attribute.GENERIC_SAFE_FALL_DISTANCE, 3.0)
                    setAttribute(player, Attribute.GENERIC_SCALE, 1.0)
                    setAttribute(player, Attribute.GENERIC_STEP_HEIGHT, 0.6)
                    setAttribute(player, Attribute.GENERIC_WATER_MOVEMENT_EFFICIENCY, 0.0)
                    setAttribute(player, Attribute.PLAYER_BLOCK_BREAK_SPEED, 1.0)
                    setAttribute(player, Attribute.PLAYER_BLOCK_INTERACTION_RANGE, 4.5)
                    setAttribute(player, Attribute.PLAYER_ENTITY_INTERACTION_RANGE, 3.0)
                    setAttribute(player, Attribute.PLAYER_MINING_EFFICIENCY, 0.0)
                    setAttribute(player, Attribute.PLAYER_SNEAKING_SPEED, 0.3)
                    setAttribute(player, Attribute.PLAYER_SUBMERGED_MINING_SPEED, 0.2)
                    setAttribute(player, Attribute.PLAYER_SWEEPING_DAMAGE_RATIO, 0.0)
                    player.inventory.clear()    //清空背包
                    player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 3600, 5, false))   //抗性提升
                    player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 3600, 5, false)) //生命恢复
                    player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 3600, 5, false))
                    player.health=player.maxHealth
//                    player.saturation=20.0.toFloat()
                    Bukkit.getLogger().info("Giving Kit to ${player.name} now")
                    Bukkit.getLogger().info("Status ${Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "sudo command ${player.name} kit icesumo")}")
                }
                UniversalDataManager.world.time=6000    //调整为白天
                Bukkit.getScheduler().runTask(plugin, Runnable {
                    CoroutineScope(Dispatchers.Default).launch {    //协程，启动！
                        val countdownJob = UniversalDataManager.checkinList.map { player ->            //赛前倒计时
                            async { countdownBeforeGame(player) }
                        }
                        countdownJob.awaitAll()
                        UniversalDataManager.preparing=false
                        val gameJob=listOf(::allOnlineCheck, ::winningCheck, ::countdown,::onFailPlayerLeave).map { check ->
                            async { check() }
                        }
                        gameJob.awaitAll()
                        UniversalDataManager.checkinList.forEach { player ->
                            player.activePotionEffects.forEach{ //移除所有效果
                                player.removePotionEffect(it.type)
                            }
                            player.inventory.clear()    //清空背包
                        }
                        UniversalDataManager.clear()
                    }
                })
            }
            else -> {
                MessageUtils.sendMessage(sender,"你不能开始一场已经开始的比赛")
                return false
            }
        }
        return true
    }

    fun setAttribute(player: Player, attribute: Attribute, value: Double) {
        val attributeInstance = player.getAttribute(attribute)
        attributeInstance?.baseValue = value
    }

    @Suppress("DEPRECATION")
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
                CSVManager.appendResult(UniversalDataManager.resultFile!!,winner?.uniqueId.toString(),winner?.name!!,true,
                    UniversalDataManager.restTime)
                UniversalDataManager.stop()
                UniversalDataManager.clear()
                break
            }else if(UniversalDataManager.isEnd()==-1) {
                for (player in UniversalDataManager.checkinList) {
                    player.sendTitle("比赛结束", "", 10, 20, 10)
                    player.sendTitle("本轮未决出胜者", "", 20, 20, 20)//宣布未胜利
                }
                UniversalDataManager.stop()
                UniversalDataManager.clear()
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
        while (UniversalDataManager.restTime>0) {
            when{
                !UniversalDataManager.isStart->break
                UniversalDataManager.restTime>5-> {
                    UniversalDataManager.checkinList.forEach { player ->
                        player.sendActionBar("${UniversalDataManager.restTime}")
                    }
                    UniversalDataManager.restTime--
                    delay(1000)
                }
                else -> {
                    UniversalDataManager.checkinList.forEach { player ->
                        player.sendTitle("${UniversalDataManager.restTime}", "", 10, 20, 10)
                    }
                    UniversalDataManager.restTime--
                    delay(1000)
                }
            }
        }
        UniversalDataManager.checkinList.forEach { player ->
            player.sendTitle("时间到！", "", 10, 20, 10)
            player.sendTitle("比赛结束！", "", 10, 20, 10)
        }
        if(UniversalDataManager.isEnd()==0) {
            UniversalDataManager.checkinList.forEach { player ->
                player.sendTitle("比赛结束", "", 10, 20, 10)
                player.sendTitle("本轮未决出胜者", "", 20, 20, 20)//宣布未胜利
            }
        }
        UniversalDataManager.stop()//时间到，终止比赛
        UniversalDataManager.clear()
    }

    @Suppress("Deprecation")
    suspend fun allOnlineCheck() {    //对
        //如果中途有玩家退出->紧急终止
        while (true) {
            UniversalDataManager.checkinList.forEach { player ->
                if (!player.isOnline && !UniversalDataManager.isFail(player)) {   //比赛中玩家+中途退出
                    UniversalDataManager.checkinList.forEach { player ->
                        player.sendTitle("比赛被紧急终止，原因：游戏中途有玩家退出", "", 10, 20, 10)
                    }
                    UniversalDataManager.stop()    //需要终止游戏
                    UniversalDataManager.clear()
                }
            }
            if (!UniversalDataManager.isStart) {  //比赛已经终止，结束监听
                break
            }
            delay(100)  //间隔2tick再次检测
        }
    }

    suspend fun onFailPlayerLeave() {
        while (true) {
            UniversalDataManager.failList.forEach { player ->
                if (!player.isOnline && !UniversalDataManager.isFail(player)) {   //结束玩家+中途退出
                    UniversalDataManager.failList.removeIf { it.uniqueId==player.uniqueId }
                }
            }
            if (!UniversalDataManager.isStart) {  //比赛已经终止，结束监听
                break
            }
            delay(100)  //间隔2tick再次检测
        }
    }
}

class TerminateCommandExecutor : CommandExecutor {
    @Suppress("Deprecation")
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(!sender.hasPermission("icesumo.referee")) {
            MessageUtils.sendMessage(sender,"你没有执行该命令的权限：该命令只允许裁判员执行")
            return false
        }
        if(UniversalDataManager.isStart) {
            UniversalDataManager.checkinList.forEach { player ->
                player.sendTitle("比赛被紧急终止，原因：${args?.get(0)}", "", 10, 20, 10)
            }
            UniversalDataManager.stop()
            UniversalDataManager.clear()
        }else{
            sender.sendMessage("无法终止未开始的比赛")
        }
        return true
    }
}