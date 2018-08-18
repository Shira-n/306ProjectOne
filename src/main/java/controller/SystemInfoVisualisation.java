package controller;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.scene.layout.Pane;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SystemInfoVisualisation extends Pane {
    private Pane _pane;

    public SystemInfoVisualisation() {
        GaugeBuilder builder = GaugeBuilder.create().skinType(Gauge.SkinType.TILE_SPARK_LINE);
        Gauge gauge = GaugeBuilder.create()
                                    .skinType(Gauge.SkinType.TILE_SPARK_LINE)
                                    .animated(true)
                                    .build();
        _pane = new Pane(gauge);

        Sigar sigar = new Sigar();
        try {
            CpuPerc cpu = sigar.getCpuPerc();
        }
        catch (SigarException e) {
            e.printStackTrace();
        }
    }
}
