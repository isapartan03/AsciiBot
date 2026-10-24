
package io.isapartan03.discordasciibot;

import io.isapartan03.discordasciibot.listeners.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class AsciiBot{

    public static void main(String[] args) throws Exception {
        //Configurar un token (lee la variable de entorno por seguridad)
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isEmpty()){
            System.err.println("La variable de entorno de discord no está definida");
            return;
        }
        
        /*Construir la instancia JDA, se habilita el intent MESSAGGE_CONTENT para poder leer
        el texto y adjuntos*/
        
        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new MessageListener())// Registra está mima clase como Listener
                .build();
        
        //Esperar que JDA esté listo
        jda.awaitReady();
        System.out.println("Bot conectado como "+jda.getSelfUser().getAsTag());
        
    }
}
