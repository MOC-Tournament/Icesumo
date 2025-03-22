package top.andyshen2006.icesumo

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.locks.ReentrantLock

object UniversalDataManager{
    private var height=60
    private val lock= ReentrantLock()
    private var isStart = false
    private var checkinList=mutableListOf<String>()

    // Editors
    fun editHeight(newHeight: Int) {
        lock.lock()
        try {
            height=newHeight
        }finally {
            lock.unlock()
        }
    }

    fun start() {
        lock.lock()
        try {
            isStart = true
        } finally {
            lock.unlock()
        }
    }

    fun terminate() {
        lock.lock()
        try {
            isStart = false
        } finally {
            lock.unlock()
        }
    }

    fun addCheckinPlayer(name: String): Boolean {
        lock.lock()
        try {
            if (checkinList.contains(name)) {
                return false
            }else{
                checkinList.add(name)
                return true
            }

        }finally {
            lock.unlock()
        }
    }

    fun delCheckinPlayer(name: String): Boolean {
        lock.lock()
        lock.lock()
        try {
            if (checkinList.contains(name)) {
                checkinList.remove(name)
                return true
            }else{
                return false
            }

        }finally {
            lock.unlock()
        }
    }

    // Getters
    fun getCheckinList(): MutableList<String> {
        return checkinList
    }

    fun getHeight(): Int {
        return height
    }

    fun isListValid() : Boolean {
        return (checkinList.size >=2) and (checkinList.size<=4)
    }
}

class Icesumo : JavaPlugin(), Listener {

    override fun onEnable() {
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
    }

    override fun onDisable() {
        logger.info("Disabling Ice Sumo Plugin!")
        // Plugin shutdown logic
    }
}

class StartCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage("你启动了比赛")
        UniversalDataManager.start()
        return true
    }
}

class InfoCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return true
    }
}

class TerminateCommandExecutor : CommandExecutor {  // TODO:1.加上原因的记录 2.加上部分自动判定（如玩家中途退出等）
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage("比赛被紧急终止，原因：")
        UniversalDataManager.terminate()
        return true
    }
}

class CheckinCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val playerName=sender.name
        sender.sendMessage("正在检录$playerName")
        if (UniversalDataManager.addCheckinPlayer(playerName)) {
            sender.sendMessage(playerName + "检录成功！")
        }else{
            sender.sendMessage(playerName + "你已检录过了！")
        }
        return true
    }
}

class CheckinlistCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage("当前已检录玩家为：")
        val checkinList=UniversalDataManager.getCheckinList()
        for(checkinPlayers in checkinList) {
            sender.sendMessage(checkinPlayers)
        }
        if (UniversalDataManager.isListValid()) {
            sender.sendMessage("当前检录玩家人数${checkinList.size}人已符合要求，可以开始比赛")
        }else{
            sender.sendMessage("当前检录玩家人数${checkinList.size}人不符合要求，不能开始比赛")
        }
        return true
    }
}

class UncheckinCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        val playerName=sender.name
        sender.sendMessage("正在取消检录$playerName")
        if (UniversalDataManager.delCheckinPlayer(playerName)) {
            sender.sendMessage(playerName + "取消检录成功！")
        }else{
            sender.sendMessage(playerName + "你没有检录过！")
        }
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