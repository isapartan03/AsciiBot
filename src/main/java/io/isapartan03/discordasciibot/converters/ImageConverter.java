
package io.isapartan03.discordasciibot.converters;

import java.nio.file.Path;
public interface ImageConverter {
    //Todos los convertores recibiran una ruta y devolverán un String
    String convert(Path rutaImagen);
}
