package com.inmd.javabot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.inmd.javabot.Bot;
import com.inmd.javabot.settings.Settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public abstract class pluginCommand extends nolistenCommand
{
    public pluginCommand(Bot bot)
    {
        super(bot);
        this.category = new Category("plugin", event -> checkpuginPermission(event));
    }
    
    public static boolean checkpuginPermission(CommandEvent event)
    {
        if(event.getAuthor().getId().equals(event.getClient().getOwnerId()))
            return true;
        if(event.getGuild()==null)
            return true;
        if(event.getMember().hasPermission(Permission.MANAGE_SERVER))
            return true;
        Settings settings = event.getClient().getSettingsFor(event.getGuild());
        Role dj = settings.getRole(event.getGuild());
        return dj!=null && (event.getMember().getRoles().contains(dj) || dj.getIdLong()==event.getGuild().getIdLong());
    }
}