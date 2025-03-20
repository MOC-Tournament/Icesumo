package top.andyshen2006.icesumo

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class UniversalDataManager private constructor(){
    companion object{
        val instance:UniversalDataManager by lazy(mode=LazyThreadSafetyMode.SYNCHRONIZED) {
            UniversalDataManager()
        }
    }
}

class Icesumo : JavaPlugin() {

    override fun onEnable() {
        logger.info("Enabling Ice sumo Plugin!")
        // Plugin startup logic
        getCommand("start")?.setExecutor(StartCommandExecutor())
        getCommand("terminate")?.setExecutor(TerminateCommandExecutor())
        getCommand("checkin")?.setExecutor(CheckinCommandExecutor())
        getCommand("checkinlist")?.setExecutor(CheckinlistCommandExecutor())
        getCommand("uncheckin")?.setExecutor(UncheckinCommandExecutor())
    }

    override fun onDisable() {
        logger.info("Disabling Ice Sumo Plugin!")
        // Plugin shutdown logic
    }
}

class StartCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        sender.sendMessage("Using Start Command Now") // TODO: This line will be deleted in formal version
        TODO("Not yet implemented")
    }
}

class TerminateCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        TODO("Not yet implemented")
    }
}

class CheckinCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        TODO("Not yet implemented")
    }
}

class CheckinlistCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        TODO("Not yet implemented")
    }
}

class UncheckinCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        TODO("Not yet implemented")
    }
}