package com.inmd.javabot.commands.plugin;

import com.inmd.javabot.Bot;
import com.inmd.javabot.commands.pluginCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

public class systeminfo extends pluginCommand{

	public systeminfo(Bot bot) {
		super(bot);
		// TODO Auto-generated constructor stub
        this.name = "info";
        this.help = "현재서버의 정보를 알려줌니다.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = false;
	}

	@Override
	public void doCommand(CommandEvent event) {
		// TODO Auto-generated method stub
        String osName = "os.name";
        String cpu = "java.version";
        

        
        MessageBuilder builder = new MessageBuilder() ;            
        
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setColor(event.getSelfMember().getColor())
                .setTitle("서버정보")
                .addField("OS 정보", System.getProperty(osName), beListening)
                .addField("자바 정보", System.getProperty(cpu), beListening)
                .addField("cpu 쓰레드", System.getenv("NUMBER_OF_PROCESSORS"), beListening )
                .setImage("https://cdn.clien.net/web/api/file/F01/5476084/c81190d3c014417da0a.JPG");
        

       
                
      
        event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();

		
	}
	
	

}
