package pl.extollite.bancmd;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.util.*;

public class Main extends PluginBase implements Listener {

    public static Main instance;
    private Map<String, List<String>> cmdBlockLevelList = new HashMap<>();

    public static Main getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Config config = this.getConfig();
        for(String key: config.getKeys()){
            if(key.equals("cfgversion"))
                continue;
            cmdBlockLevelList.put(key, config.getStringList(key));
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        instance = this;
        this.getLogger().info(TextFormat.GREEN+"Hello!");
/*        this.getLogger().info(cmdBlockLevelList.toString());*/
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().toLowerCase().equals("bancmd") || !cmd.getName().toLowerCase().equals("unbancmd")) return true;
        if (cmd.getName().toLowerCase().equals("bancmd") && args.length == 2) {
            if(sender instanceof Player){
                if( sender.isOp() || sender.hasPermission("bancmd.command")){
                    List<String> cmds;
                    if(cmdBlockLevelList.containsKey(args[1])){
                        cmds = cmdBlockLevelList.get(args[1]);
                        if(!cmds.contains(args[0])){
                            cmds.add(args[0]);
                            cmdBlockLevelList.replace(args[1], cmds);
                            this.getConfig().set(args[1], cmds);
                            this.getConfig().save();
                            sender.sendMessage(TextFormat.GREEN+"Successful banned "+args[0]+" command in world "+args[1]+"!");
                            return true;
                        }
                    }
                    else{
                        cmds = new ArrayList<>();
                        cmds.add(args[0]);
                        cmdBlockLevelList.put(args[1], cmds);
                        this.getConfig().set(args[1], cmds);
                        this.getConfig().save();
                        sender.sendMessage(TextFormat.GREEN+"Successful banned "+args[0]+" command in world "+args[1]+"!");
                        return true;
                    }
                }
            }
        }
        else if(cmd.getName().toLowerCase().equals("unbancmd") && args.length == 2){
            if(sender instanceof Player){
                if( sender.isOp() || sender.hasPermission("bancmd.command")){
                    if(cmdBlockLevelList.containsKey(args[1])){
                        List<String> cmds = cmdBlockLevelList.get(args[1]);
                        if(cmds.contains(args[0])){
                            cmds.remove(args[0]);
                            cmdBlockLevelList.replace(args[1], cmds);
                            this.getConfig().set(args[1], cmds);
                            this.getConfig().save();
                            sender.sendMessage(TextFormat.GREEN+"Successful unbanned "+args[0]+" command in world "+args[1]+"!");
                            return true;
                        }
                    }
                }
            }
        }
        sender.sendMessage(TextFormat.GREEN+"Usage: ");
        sender.sendMessage(TextFormat.GREEN+"/bancmd <command> <world> - ban command in world.");
        sender.sendMessage(TextFormat.GREEN+"/unbancmd <command> <world> - unban command in world.");
        return false;
    }

    @EventHandler
    public void onPlayerCmd(PlayerCommandPreprocessEvent ev){
        if(ev.isCancelled())
            return;
        Player player = ev.getPlayer();
        if(player.isOp() || player.hasPermission("bancmd.bypass"))
            return;
        String level = player.getLevel().getName();
        if(!cmdBlockLevelList.containsKey(level))
            return;
        String msg = ev.getMessage();
        msg = msg.trim();
        String[] cmd = msg.split("\\s+");
/*        this.getLogger().info(cmd[0].substring(1));*/
        if(cmdBlockLevelList.get(level).contains(cmd[0].substring(1))){
            player.sendMessage(TextFormat.RED+"This command is banned in this level!");
            ev.setCancelled();
        }
    }

}
