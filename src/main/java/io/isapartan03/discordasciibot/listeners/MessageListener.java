
package io.isapartan03.discordasciibot.listeners;


import io.isapartan03.discordasciibot.services.AsciiServices;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

//La clase se encarga de leer el chat y llamar al servicio
public class MessageListener extends ListenerAdapter{
    
    private final AsciiServices asciiServices = new AsciiServices(); //instancia del servicio
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        //ignorar menjesas de otros bots (incluido el mismo)
        if (event.getAuthor().isBot()) return;
        
        //obtener el mensaje
        String msj = event.getMessage().getContentRaw();
        
        //Solo continuar si el mensaje empieza por /ascii
        if (!msj.startsWith("a/ascii")) return;
        
        //comando a/ayuda
        if (msj.equalsIgnoreCase("a/ayuda")){
            event.getChannel().sendMessage("**Comandos: **\n`a/ascii` - Convierte una imagen.")
                    .queue();
        }
        
        String[] partes = msj.split(" ");
        String tipoConversor = (partes.length > 1) ? partes[1] : "normal";
        
        boolean tieneImagen = false;
        //Buscar si el mensaje trae una imagen adjunta
        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
            if (attachment.isImage()) {
                tieneImagen = true;
                //6. Procesar la imagen en un hilo, aparte de no bloquear el Gateway
                asciiServices.procesarImagen(attachment, event, tipoConversor);
                break;
            }
        }
        
        //Si no hay imagen, decir
        if (!tieneImagen || tieneImagen==false){
            event.getChannel().sendMessage("Escribiste: 'a/ascii', pero olvidaste adjuntar una"
                    + " imagen. Inténtalo de nuevo").queue();
        }
    }
}