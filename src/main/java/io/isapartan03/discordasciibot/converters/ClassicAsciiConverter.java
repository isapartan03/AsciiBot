
package io.isapartan03.discordasciibot.converters;

import io.github.seujorgenochurras.image.ascii.AsciiParser;
import io.github.seujorgenochurras.image.ascii.ParserBuilder;
import io.github.seujorgenochurras.image.ascii.ParserConfig;
import io.github.seujorgenochurras.image.ascii.algorithm.pixel.bright.Algorithms;
import java.nio.file.Path;
        
public class ClassicAsciiConverter implements ImageConverter{
    
    @Override
    public String convert(Path rutaImagen){
        String[] caracteres = {"@","#","%","*","+","=","-",":","."};
        //@%#*+=-:. 
        ParserConfig config = ParserBuilder.startBuild()
                .parserAlgorithm(Algorithms.HUMAN_EYE_ALGORITHM)
                .scaled()
                .width(85)
                .height(50)
                .getScale()
                .symbols(caracteres)
                .build();
        
        return AsciiParser.parse(rutaImagen.toString(), config);
    }
    
}
