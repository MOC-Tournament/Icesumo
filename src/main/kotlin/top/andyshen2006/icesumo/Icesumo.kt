package top.andyshen2006.icesumo

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


class Icesumo : JavaPlugin() , Listener{
    override fun onEnable() {
        server.pluginManager.registerEvents(FailStatusChecker(), this)
        saveResource("config.yml",false)
        saveDefaultConfig() //TODO:允许手动配置参数
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
        // Tests
        getCommand("testdelay")?.setExecutor( TestDelayCommandExecutor())
        getCommand("testlisten")?.setExecutor(StartListenCommandExecutor())
        getCommand("testedit")?.setExecutor(EditFlagCommandExecutor())

    }

    override fun onDisable() {
        logger.info("Disabling Ice Sumo Plugin!")
        // Plugin shutdown logic
    }
}


