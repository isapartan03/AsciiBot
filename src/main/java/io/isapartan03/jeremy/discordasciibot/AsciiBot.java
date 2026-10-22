
package io.isapartan03.jeremy.discordasciibot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import io.github.seujorgenochurras.DefaultAsciifier;
import io.github.seujorgenochurras.image.ascii.AsciiParser;
import io.github.seujorgenochurras.image.ascii.ParserBuilder;
import io.github.seujorgenochurras.image.ascii.ParserConfig;
import io.github.seujorgenochurras.image.ascii.algorithm.pixel.bright.Algorithms;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;


public class AsciiBot extends ListenerAdapter{

    
    public static void main(String[] args) throws Exception {
        //1. Configurar un token (lee la variable de entorno por seguridad)
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isEmpty()){
            System.err.println("La variable de entorno de discord no está definida");
            return;
        }
        
        /*2. Construir la instancia JDA, se habilita el intent MESSAGGE_CONTENT para poder leer
        el texto y adjuntos*/
        
        JDA jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new AsciiBot())// Registra está mima clase como Listener
                .build();
        
        //3. Esperar que JDA esté listo
        jda.awaitReady();
        System.out.println("Bot conectado como "+jda.getSelfUser().getAsTag());
        
    }
    
    //4. Esté método se dispara cada vez que llega un mensaje
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        //ignorar menjesas de otros bots (incluido el mismo)
        if (event.getAuthor().isBot()) return;
        
        //Verificamos si el mensaje empieza con !ascii
        if(!event.getMessage().getContentRaw().startsWith("!ascii")) return;
        
        //5. Buscar si el mensaje trae una imagen adjunta
        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment.isImage()) {
                //6. Procesar la imagen en un hilo, aparte de no bloquear el Gateway
                new Thread(() -> procesarImagen(attachment, event)).start();
                return;
            }
        }
        
        //Si no hay imagen, decir
        event.getChannel().sendMessage("Necesitas adjuntar la imagen junto al comando").queue();
    }
    
    private void procesarImagen(Message.Attachment attachment, MessageReceivedEvent event){
        Path tempArch = null;
        try{
            //7. Descargar la imagen en un archivo temporal
            tempArch = Files.createTempFile("discord-ascii-",".img");
            attachment.getProxy().downloadToFile(tempArch.toFile()).get();
            
            /*8. Convertir a ASCII*/
            String[] caracteres = {"@", "#", "S", "%", "?", "*", "+", ";", ":", ",", "."};
            
            ParserConfig config = ParserBuilder.startBuild()
                    .parserAlgorithm(Algorithms.HUMAN_EYE_ALGORITHM)
                    .scaled()
                    .width(85)
                    .height(50)
                    .getScale()
                    .symbols(caracteres)
                    .build();
            
            /*9. Enviar el resultado, discord tiene un límite de 2000 caracteres por mensaje. Si el
            arte es muy grande, lo enviaremos por .txt*/
            String asciiArt = AsciiParser.parse(tempArch.toString(), config);
            if (asciiArt.length() <=2000){
                event.getChannel().sendMessage("```\n" + asciiArt + "\n```").queue();
            }else{
                File asciiFile = new File("ascii_art.txt");
                Files.writeString(asciiFile.toPath(), asciiArt);
                event.getChannel().sendFiles(FileUpload.fromData(asciiFile, "ascii_art.txt")).queue();
            }
        } catch (Exception e){
            e.printStackTrace();
            event.getChannel().sendMessage("Error al procesar la imagen: " + e.getMessage()).queue();
        } finally {
            //10. Limpiar el archivo temporal
            if (tempArch != null){
                try{
                    Files.deleteIfExists(tempArch);
                } catch (Exception ignored){
                    
                }
            }
        }
    }
}
