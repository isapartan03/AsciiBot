
package io.isapartan03.discordasciibot.services;

import net.dv8tion.jda.api.utils.FileUpload;
import io.github.seujorgenochurras.DefaultAsciifier;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import io.github.seujorgenochurras.image.ascii.AsciiParser;
import io.github.seujorgenochurras.image.ascii.ParserBuilder;
import io.github.seujorgenochurras.image.ascii.ParserConfig;
import io.github.seujorgenochurras.image.ascii.algorithm.pixel.bright.Algorithms;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class AsciiServices {
    
    //Esté método se dispara cada vez que llega un mensaje
    public void procesarImagen(Message.Attachment attachment, MessageReceivedEvent event){
        Path tempArch = null;
        try{
            //Descargar la imagen en un archivo temporal
            tempArch = Files.createTempFile("discord-ascii-",".img");
            attachment.getProxy().downloadToFile(tempArch.toFile()).get();
            
            /*Convertir a ASCII*/
            String[] caracteres = {"@", "#", "S", "%", "?", "*", "+", ";", ":", ",", "."};
            
            ParserConfig config = ParserBuilder.startBuild()
                    .parserAlgorithm(Algorithms.HUMAN_EYE_ALGORITHM)
                    .scaled()
                    .width(85)
                    .height(50)
                    .getScale()
                    .symbols(caracteres)
                    .build();
            
            /*Enviar el resultado, discord tiene un límite de 2000 caracteres por mensaje. Si el
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