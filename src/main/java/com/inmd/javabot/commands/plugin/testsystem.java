package com.inmd.javabot.commands.plugin;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import com.inmd.javabot.Bot;
import com.inmd.javabot.commands.pluginCommand;




public class testsystem extends pluginCommand  {

	public testsystem(Bot bot)
    {
        super(bot);
        this.name = "test";
        this.help = "현재 뜨는지 테스트중입니다.";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) 
    {
	
        MessageBuilder builder = new MessageBuilder() ;            
                                   
        EmbedBuilder ebuilder = new EmbedBuilder()
                .setColor(event.getSelfMember().getColor())
                .setDescription("뜨면 성공")
                .setImage("https://blog.kakaocdn.net/dn/Hh8rb/btqYE8eXaXX/YXQqAgzafbfKQ9ygkCOmKK/img.jpg");

        event.getChannel().sendMessage(builder.setEmbed(ebuilder.build()).build()).queue();
    }

}
