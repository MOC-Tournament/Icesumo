package top.andyshen2006.icesumo

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


class Icesumo : JavaPlugin() , Listener{
    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(FailStatusChecker(), this)
        server.pluginManager.registerEvents(StartCommandExecutor(),this)
//        server.pluginManager.registerEvents(CheckedPlayerLeaveListener(),this)
        saveResource("config.yml",false)
        saveDefaultConfig() //TODO:允许手动配置参数
        UniversalDataManager.analyseConfig(config)
        logger.info("Enabling Ice Sumo Plugin!")
        logger.info("Ice Sumo plugin successfully enabled!")
        // Control Flow Logic
        getCommand("start")?.setExecutor(StartCommandExecutor())
        getCommand("showinfo")?.setExecutor(InfoCommandExecutor())
        getCommand("terminate")?.setExecutor(TerminateCommandExecutor())
        getCommand("icesumo_clear")?.setExecutor { sender, command, label, args ->
            if(!sender.hasPermission("icesumo.maintainer")) {
                MessageUtils.sendMessage(sender,"你没有执行该命令的权限：该命令只允许运维执行")
                return@setExecutor false
            }
            UniversalDataManager.clear()
            return@setExecutor true
        }
        // Checkins Logic
        getCommand("checkin")?.setExecutor(CheckinCommandExecutor())
        getCommand("checkinlist")?.setExecutor(CheckinlistCommandExecutor())
        getCommand("checkin_result")?.setExecutor(CheckinResultCommandExecutor())   // 按照MOG数据包规范，该函数实现类似功能，和checkinlist不同
        getCommand("uncheckin")?.setExecutor(UncheckinCommandExecutor())
        // Settings Logic
        getCommand("editheight")?.setExecutor(EditHeightCommandExecutor())
        getCommand("editpos")?.setExecutor(EditStadiumPositionExecutor())
        getCommand("editgrave")?.setExecutor(EditGravePositionExecutor() )
        // Server Checkers
        getCommand("checkrule")?.setExecutor(CheckRuleCommandExecutor())
        getCommand("setrule")?.setExecutor(SetRuleCommandExecutor())
        // Tests
        getCommand("testdelay")?.setExecutor( TestDelayCommandExecutor())
        getCommand("testlisten")?.setExecutor(StartListenCommandExecutor())
        getCommand("testedit")?.setExecutor(EditFlagCommandExecutor())
        getCommand("testkit")?.setExecutor(KitCommandExecutor())
        getCommand("testwrite")?.setExecutor(TestWriteCommandExecutor())

    }

    override fun onDisable() {
        logger.info("Disabling Ice Sumo Plugin!")
        // Plugin shutdown logic
    }
}


