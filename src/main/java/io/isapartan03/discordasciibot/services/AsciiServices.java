
package io.isapartan03.discordasciibot.services;

import io.isapartan03.discordasciibot.converters.ImageConverter;
import io.isapartan03.discordasciibot.factory.ConverterFactory;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class AsciiServices {
    
    //Esté método se dispara cada vez que llega un mensaje
    public void procesarImagen(Message.Attachment attachment, MessageReceivedEvent event, String 
            tipoConversor){
        
        Path tempArch = null;
        try{
            //Descargar la imagen en un archivo temporal
            tempArch = Files.createTempFile("discord-ascii-",".img");
            attachment.getProxy().downloadToFile(tempArch.toFile()).get();
            
            //usar la fábrica
            ImageConverter conversor = ConverterFactory.crearConversor(tipoConversor);
            String asciiArt = conversor.convert(tempArch);
            
            //se manda el ascii por chat o por .txt
            if (asciiArt.length() <= 2000){
                event.getChannel().sendMessage("```\n" + asciiArt + "\n```").queue();
            }else{
                File asciiFile = new File("acii_art.txt");
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