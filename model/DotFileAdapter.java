package model;

import java.io.File;
import java.io.FileNotFoundException;
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
    private List<Node> _data;;

    public DotFileAdapter(String inputPath) throws FileNotFoundException {
        readGraph();
    }

    private void readGraph() throws FileNotFoundException{
        //TODO Read file, populate _data. Plz refer to Node class to see available methods.
        //Throw FileNotFoundException and I/O exception when needed, will be handled in Main.java
        //See Main.java for usage of this method
    }

    public void writeSchedule(List<Processor> schedule, String outputPath){
        //TODO Haven't decided on the output data structure.
        //I was thinking about passing List<Processor> from scheduler. Can leave this for now
    }

    public List<Node> getData(){
        return _data;
    }

}
