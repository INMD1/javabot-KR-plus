package com.inmd.javabot.commands.admin;

import com.inmd.javabot.Bot;
import com.inmd.javabot.audio.AudioHandler;
import com.inmd.javabot.commands.AdminCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

public class mute extends AdminCommand{

    public mute(Bot bot)
    {
        this.name = "mute";
        this.help = "해당 사람을 입을 막을수 잇습니다";
        this.arguments = "<name>";
        this.aliases = bot.getConfig().getAliases(this.name);
    }
    
	@Override
	protected void execute(CommandEvent event) {
		// TODO Auto-generated method stub		String id;
        String get;
        if(event.getArgs().isEmpty())
        	get = ((AudioHandler)event.getGuild().getAudioManager().getSendingHandler()).getPlayer().getPlayingTrack().getInfo().title;
        else
        	get = event.getArgs();
        String member  = get.replace("<@", "").replace(">", "");       
        // Role role = (Role) event.getGuild().getRoleById("827607496704262194");    
        event.getChannel().sendMessage("테스트 규칙이 사용자에게 처리 했습니다." + get + ".").queue();
		System.out.print(member);
		
	}
	

}
