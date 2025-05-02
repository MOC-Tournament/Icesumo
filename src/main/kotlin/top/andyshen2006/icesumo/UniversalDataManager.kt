package top.andyshen2006.icesumo

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.locks.ReentrantLock

object UniversalDataManager{
    var height=40
        set(newHeight) {
            try {
                lock.lock()
                field=newHeight
            }finally {
                lock.unlock()
            }
        }
    var isStart = false
    var checkinList=mutableListOf<Player>()
    var gravePos = Triple(0.0, 70.0, 0.0)
        set(newGravePos) {
            try {
                lock.lock()
                field=newGravePos
            }finally {
                lock.unlock()
            }
        }
    var preparing = false
        set(newStatus) {
            try {
                lock.lock()
                field=newStatus
            }finally {
                lock.unlock()
            }
        }
    var world = Bukkit.getWorlds()[0]!!
    var time: Long=0
    var restTime:Long =-1
    var failList=mutableListOf<Player>()    // TODO:其实应该给checkinList里面的玩家附加属性的，可惜当时没做，等哪次Refactor的时候再搞吧
    private var stadiumPos= arrayOf( Triple(-1.0,60.0,-1.0), Triple(1.0,60.0,1.0), Triple(-1.0,60.0,1.0), Triple(1.0,60.0,-1.0) )
    private val lock= ReentrantLock()
    private val plugin = Bukkit.getPluginManager().getPlugin("Icesumo") as Icesumo
    private val dataDir= File(plugin.dataFolder, "results")
    var resultFile: File? = null

    // Setters
    fun setStadiumPos(playerNum: Int, newStadiumPos: Triple<Double,Double, Double>) {
        try {
            lock.lock()
            stadiumPos[playerNum-1] = newStadiumPos
        }finally {
            lock.unlock()
        }
    }

    // Control Flow
    fun analyseConfig(config: FileConfiguration) {
        // Height Analysis
        height = config.getInt("height")
        // World Analysis
        world = Bukkit.getWorld(config.getString("world")!!)!!
        // Time Analysis
        time = config.getLong("time")
        // Stadium Analysis
        stadiumPos=config.getMapList("stadium").map { it ->
            Triple(
                it["x"] as Double,
                it["y"] as Double,
                it["z"] as Double
            )
        }.toTypedArray()
        // Grave Analysis
        val graveSection = config.getConfigurationSection("grave")!!
        gravePos= Triple(graveSection.getDouble("x"),graveSection.getDouble("y"),graveSection.getDouble("z"))
    }

    fun start() : Boolean{
        try {
            lock.lock()
            if (!isStart) {
                failList.clear()
                isStart = true
                resultFile= File(dataDir, "results-${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmSS"))}.csv")
                restTime=time
                CSVManager.startWriteResult(resultFile!!)
            }else{
                return false
            }
        } finally {
            lock.unlock()
        }
        return true
    }

    fun stop() : Boolean{
        try {
            lock.lock()
            if (isStart) {
                isStart = false
            }else {
                return false
            }
        } finally {
            lock.unlock()
        }
        return true
    }

    fun clear() {
        try {
            lock.lock()
            checkinList.clear()
            failList.clear()
        }finally {
            lock.unlock()}
    }

    fun addCheckinPlayer(player: Player): Boolean {
        try {
            lock.lock()
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
        try {
            lock.lock()
            var flag=false
            checkinList.forEach { checkedPlayer ->
                when{
                    checkedPlayer.uniqueId==player.uniqueId -> flag=true
                }
            }
            if (!flag) {
                return false
            }else {
                checkinList.removeIf { it.uniqueId == player.uniqueId }
                flag=false
                failList.forEach { failedPlayer->
                    when{
                        failedPlayer.uniqueId==player.uniqueId -> flag=true
                    }
                }
                if (flag) {
                    failList.removeIf { it.uniqueId == player.uniqueId }
                }
            }
        }finally {
            lock.unlock()
        }
        return true
    }

    fun playerFail(player: Player): Boolean {
        try {
            lock.lock()
            if (failList.contains(player)) {
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
        return UniversalDataManager.stadiumPos[stadiumPos]
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
    // Server checker
    @Suppress("Deprecation")
    fun isRuleValid(): String{
        var result = ""
        result += if (world.getGameRuleValue("doDaylightCycle").toBoolean()) {
            "F"
        }else{
            "T"
        }
        return result
    }

    // Showcase Information (For Maintainers)
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
        stadiumPos.forEach { stadium->
            info+="(${stadium.first},${stadium.second},${stadium.third}),"
        }
        info+="\n"
        info+="死亡场地设置$gravePos\n"
        return info
    }
}