/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.inmd.javabot.commands.admin;

import java.util.List;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.inmd.javabot.Bot;
import com.inmd.javabot.commands.AdminCommand;
import com.inmd.javabot.settings.Settings;
import com.inmd.javabot.utils.FormatUtil;

import net.dv8tion.jda.api.entities.Role;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetdjCmd extends AdminCommand
{
    public SetdjCmd(Bot bot)
    {
        this.name = "setdj";
        this.help = "특정 음악 명령에 대한 DJ 역할을 설정합니다.";
        this.arguments = "<rolename|NONE>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
    @Override
    protected void execute(CommandEvent event) 
    {
        if(event.getArgs().isEmpty())
        {
            event.reply(event.getClient().getError()+" 역할 이름 또는 NONE을 포함하십시오.");
            return;
        }
        Settings s = event.getClient().getSettingsFor(event.getGuild());
        if(event.getArgs().equalsIgnoreCase("none"))
        {
            s.setDJRole(null);
            event.reply(event.getClient().getSuccess()+"DJ 역할이 삭제되었습니다. 관리자만 DJ 명령을 사용할 수 있습니다.");
        }
        else
        {
            List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
            if(list.isEmpty())
                event.reply(event.getClient().getWarning()+" No Roles found matching \""+event.getArgs()+"\"");
            else if (list.size()>1)
                event.reply(event.getClient().getWarning()+FormatUtil.listOfRoles(list, event.getArgs()));
            else
            {
                s.setDJRole(list.get(0));
                event.reply(event.getClient().getSuccess()+" 이제 DJ 명령은  **"+list.get(0).getName()+"** 역할을 가지고 있는 사용자가 사용할 수 있습니다.");
            }
        }
    }
    
}
