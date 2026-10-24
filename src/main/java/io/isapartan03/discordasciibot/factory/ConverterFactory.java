
package io.isapartan03.discordasciibot.factory;

import io.isapartan03.discordasciibot.converters.ClassicAsciiConverter;
import io.isapartan03.discordasciibot.converters.ImageConverter;
import io.isapartan03.discordasciibot.converters.InvertedAsciiConverter;

public class ConverterFactory {
    
    public static ImageConverter crearConversor(String tipo){
        if ("invertido".equalsIgnoreCase(tipo)){
            return new InvertedAsciiConverter();
        }
        //Por defecto siempre devuelve el clásico
        return new ClassicAsciiConverter();
    }
}
