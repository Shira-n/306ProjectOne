package model;

import java.io.File;
import java.util.List;

//@TODO Corey
/*
  ________                  .___ .____                   __
 /  _____/  ____   ____   __| _/ |    |    __ __   ____ |  | __
/   \  ___ /  _ \ /  _ \ / __ |  |    |   |  |  \_/ ___\|  |/ /
\    \_\  (  <_> |  <_> ) /_/ |  |    |___|  |  /\  \___|    <
 \______  /\____/ \____/\____ |  |_______ \____/  \___  >__|_ \
        \/                   \/          \/           \/     \/
 */
public class  DotFileAdapter {
    //TODO From the project description, INPUT should be the name of input dot file.
    private String _outputFilePath = "INPUT_output.dot";
    private List<Node> _data;
    private File _file;

    public DotFileAdapter(String filename){
        //TODO Check filename, throw FileNotFoundException if the filename is invalid, i.e. file doesn't exist,
        //TODO file name doesn't end up with .dot.

        _file = new File(filename);
    }

    private void readData(){
        //TODO Read file, populate _data. Plz refer to Node class to see available methods.

    }

    private void writeData(){
        //TODO Ask for overwriting if file already exists, or save as INPUT_ouput_1.dot.
        //TODO Haven't decided on the output data structure.
        //I was thinking about passing List<Processor> from scheduler. Can leave this for now
    }

    public List<Node> getData(){
        return _data;
    }

}
