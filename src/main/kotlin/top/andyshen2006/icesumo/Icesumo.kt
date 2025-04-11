package top.andyshen2006.icesumo

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


class Icesumo : JavaPlugin() , Listener{
    override fun onEnable() {
        server.pluginManager.registerEvents(FailStatusChecker(), this)
        server.pluginManager.registerEvents(StartCommandExecutor(),this)
        server.pluginManager.registerEvents(CheckedPlayerLeaveListener(),this)
        saveResource("config.yml",false)
        saveDefaultConfig() //TODO:允许手动配置参数
        UniversalDataManager.analyseConfig(config)
        logger.info("Enabling Ice Sumo Plugin!")
        logger.info("Ice Sumo plugin successfully enabled!")
        // Plugin startup logic
        getCommand("start")?.setExecutor(StartCommandExecutor())
        getCommand("showinfo")?.setExecutor(InfoCommandExecutor())
        getCommand("terminate")?.setExecutor(TerminateCommandExecutor())
        getCommand("clear")?.setExecutor { sender, command, label, args ->
            UniversalDataManager.clear()
            return@setExecutor true
        }
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
        getCommand("testkit")?.setExecutor(KitCommandExecutor())

    }

    override fun onDisable() {
        logger.info("Disabling Ice Sumo Plugin!")
        // Plugin shutdown logic
    }
}


