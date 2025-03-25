package top.andyshen2006.icesumo

import org.bukkit.entity.Player
import java.util.concurrent.locks.ReentrantLock

object UniversalDataManager{
    private var height=60
    private val lock= ReentrantLock()
    private var isStart = false
    private var checkinList=mutableListOf<Player>()
    private var failList=mutableListOf<Player>()    // TODO:一个很糟的想法，这玩意可能会导致重复判定
    private var StadiumPos= arrayOfNulls<Triple<Double, Double, Double>>(4)
    private var gravePos = Triple<Double, Double, Double>(0.0, 0.0, 0.0)

    // Editors
    fun editHeight(newHeight: Int) {
        lock.lock()
        try {
            height=newHeight
        }finally {
            lock.unlock()
        }
    }

    fun editStadiumPos(playerNum: Int, newStadiumPos: Triple<Double,Double, Double>) {
        lock.lock()
        try {
            StadiumPos[playerNum] = newStadiumPos
        }finally {
            lock.unlock()
        }
    }

    fun editGravePos(playerNum: Int, newGravePos: Triple<Double, Double, Double>) {
        lock.lock()
        try {
            gravePos = newGravePos
        }finally {
            lock.unlock()
        }
    }

    // Control Flow
    fun start() : Boolean{
        lock.lock()
        try {
            if (!isStart) {
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
            if (checkinList.contains(player)) {
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
            if (checkinList.contains(player)) {
                checkinList.remove(player)
            }else{
                return false
            }

        }finally {
            lock.unlock()
        }
        return true
    }

    fun playerFail(player: Player): Boolean {
        lock.lock()
        try {
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
    fun getCheckinList(): MutableList<Player> {
        return checkinList
    }

    fun getHeight(): Int {
        return height
    }

    fun getStadiumPos(stadiumPos:Int): Triple<Double, Double, Double>? {
        return StadiumPos[stadiumPos]
    }

    fun getGravePos(stadiumPos:Int): Triple<Double, Double, Double>? {
        return gravePos
    }

    fun isListValid() : Boolean {
        return (checkinList.size >=2) and (checkinList.size<=4)
    }

    fun isStart() : Boolean {
        return isStart
    }

    fun isCheckin(player: Player): Boolean {
        return checkinList.contains(player)
    }

    fun isFail(player: Player): Boolean{
        return failList.contains(player)
    }
}