package top.andyshen2006.icesumo

import org.bukkit.entity.Player
import java.util.concurrent.locks.ReentrantLock

object UniversalDataManager{
    var height=60
        set(newHeight) {
            lock.lock()
            try {
                height=newHeight
            }finally {
                lock.unlock()
            }
        }
    var isStart = false
    var checkinList=mutableListOf<Player>()
    var gravePos = Triple(0.0, 70.0, 0.0)
        set(newGravePos) {
            lock.lock()
            try {
                gravePos = newGravePos
            }finally {
                lock.unlock()
            }
        }
    var preparing = false
        set(newStatus) {
            lock.lock()
            try {
                preparing = newStatus
            }finally {
                lock.unlock()
            }
        }
    private var failList=mutableListOf<Player>()    // TODO:一个很糟的想法，这玩意可能会导致重复判定
    private var StadiumPos= arrayOf( Triple(-1.0,60.0,-1.0), Triple(1.0,60.0,1.0), Triple(-1.0,60.0,1.0), Triple(1.0,60.0,-1.0) )
    private val lock= ReentrantLock()

    // Setters
    fun setStadiumPos(playerNum: Int, newStadiumPos: Triple<Double,Double, Double>) {
        lock.lock()
        try {
            StadiumPos[playerNum-1] = newStadiumPos
        }finally {
            lock.unlock()
        }
    }

    // Control Flow
    fun start() : Boolean{
        lock.lock()
        try {
            if (!isStart) {
                failList.clear()
                isStart = true
            }else{
                return false
            }

        } finally {
            lock.unlock()
        }
        return true
    }

    fun stop() : Boolean{
        lock.lock()
        try {
            if (isStart) {
                failList.clear()
                checkinList.clear()
                isStart = false
            }else {
                return false
            }
        } finally {
            lock.unlock()
        }
        return true
    }

    fun addCheckinPlayer(player: Player): Boolean {
        lock.lock()
        try {
            var flag=true
            checkinList.forEach { checkedPlayer ->
                when{
                    checkedPlayer.uniqueId==player.uniqueId -> flag=false
                }
            }
            if (!flag) {
                return false
            }else{
                checkinList.add(player)
            }

        }finally {
            lock.unlock()
        }
        return true
    }

    fun delCheckinPlayer(player: Player): Boolean {
        lock.lock()
        lock.lock()
        try {
            checkinList.forEach { checkedPlayer->
                if (checkedPlayer.uniqueId==player.uniqueId) {
                    checkinList.remove(checkedPlayer)
                }
            }
        }finally {
            lock.unlock()
        }
        return true
    }

    fun playerFail(player: Player): Boolean {
        lock.lock()
        try {
            if (failList.contains(player)) {    //TODO:可能需要修改
                throw UnsupportedOperationException("不可能出现两次失败")
            }else{
                failList.add(player)
            }
        }finally {
            lock.unlock()
        }
        return true
    }

    // Getters
    fun getStadiumPos(stadiumPos:Int): Triple<Double, Double, Double> {
        return StadiumPos[stadiumPos]
    }

    fun isListValid() : Boolean {
        return (checkinList.size >=2) and (checkinList.size<=4)
    }

    fun isCheckin(player: Player): Boolean {
        checkinList.forEach { checkedPlayer ->
            if(checkedPlayer.uniqueId==player.uniqueId){
                return true
            }
        }
       return false
    }

    fun isFail(player: Player): Boolean{
        failList.forEach { failedPlayer ->
            if(failedPlayer.uniqueId==player.uniqueId){
                return true
            }
        }
        return false
    }

    fun isEnd(): Int{   //只剩下一个人
        return when{
            failList.size+1==checkinList.size -> 1   //决出胜负
            failList.size==checkinList.size -> -1    //全负
            else-> 0 //继续比赛
        }
    }

    fun isPreparing(): Boolean{
        return preparing
    }
    // Showcase Information
    fun showInfo():String {
        var info=""
        info+="设定高度:$height\n"
        info+="是否开始：$isStart\n"
        info+="是否处于准备状态：$preparing\n"
        info+="检录玩家名单："
        checkinList.forEach { player ->
            info+=player.name+","
        }
        info+="\n"
        info+="当前已失败玩家名单："
        failList.forEach { player ->
            info+=player.name+","
        }
        info+="\n"
        info+="场馆设置："
        StadiumPos.forEach { stadium->
            info+="(${stadium.first},${stadium.second},${stadium.third}),"
        }
        info+="\n"
        info+="死亡场地设置$gravePos\n"
        return info
    }
}