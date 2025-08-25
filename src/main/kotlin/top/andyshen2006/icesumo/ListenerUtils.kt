package top.andyshen2006.icesumo

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FailStatusChecker : Listener {
    @EventHandler
    @Suppress("DEPRECATION")
    fun isFailed(event: PlayerMoveEvent) {
        val player = event.player
        val from = event.from
        val to = event.to
        if (from.y == to.y) { //未发生y方向移动
            return
        }
        if (to.y <= UniversalDataManager.height) {    //低于判定高度
            executeFailAction(player)
        }
    }

    @Suppress("DEPRECATION","UnstableApiUsage")
    fun executeFailAction(player: Player) {
        when {
            !UniversalDataManager.isStart -> return //比赛还未开始
            !UniversalDataManager.isCheckin(player) -> return //该玩家没有被检录
            UniversalDataManager.isFail(player) -> return //该玩家已经失败
            else -> {
//                player.sendTitle("你输了！", "", 10, 20, 10)
                for (person in Bukkit.getOnlinePlayers()) {
                    person.sendTitle("玩家${player.name}坠落！","",10,20,10)
                }
                Bukkit.getLogger().info("${player.name} Failed!")
//                player.gameMode = GameMode.SPECTATOR    //设置为旁观者模式
                val location = Location(
                    UniversalDataManager.world,
                    UniversalDataManager.gravePos.first,
                    UniversalDataManager.gravePos.second,
                    UniversalDataManager.gravePos.third,
                    player.yaw,
                    player.pitch
                )
                player.teleport(location)
                UniversalDataManager.playerFail(player) //在数据管理器中登记失败
                CSVManager.appendResult(
                    UniversalDataManager.resultFile!!,
                    player.uniqueId.toString(),
                    player.name,
                    false,
                    UniversalDataManager.restTime
                )

            }
        }
    }
}

//class CheckedPlayerLeaveListener : Listener {
//    @EventHandler
//    fun onPlayerLeave(event: PlayerQuitEvent) {
//        UniversalDataManager.checkinList.forEach { player ->
//            if (player.uniqueId == event.player.uniqueId) {
//                UniversalDataManager.delCheckinPlayer(player)
//            }
//        }
//    }
//}