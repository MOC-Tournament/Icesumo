package top.andyshen2006.icesumo

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class FailStatusChecker: Listener {
    @EventHandler
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

class CheckedPlayerLeaveListener : Listener {
    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        UniversalDataManager.getCheckinList().forEach { player ->
            if (player.uniqueId==event.player.uniqueId) {
                UniversalDataManager.delCheckinPlayer(player)
            }
        }
    }
}