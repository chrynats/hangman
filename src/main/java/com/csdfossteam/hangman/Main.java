
package com.csdfossteam.hangman;

import com.csdfossteam.hangman.face.cli.HangmanScreen;
import com.csdfossteam.hangman.face.cli.base.View;
import com.csdfossteam.hangman.face.cli.base.ViewValues;
import com.csdfossteam.hangman.face.cli.base.ViewsFile;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class Main {

    private Main() {}

    public static void main(String[] args) throws Exception {

        System.out.println("hangman");
        
        Path path;
        
        path = Paths.get(new java.io.File( "." ).getCanonicalPath(),"src","main","resources","com","csdfossteam","hangman","face","cli","classic");
       
        new HangmanScreen(ViewsFile.fromPath(path)).start();
    }

}
