package pl.mroczkarobert.vitalite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.mroczkarobert.vitalite.service.*;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class Process {

    private static final Logger LOG = LoggerFactory.getLogger(Process.class);

    @Autowired
    private VitaliteService vitaliteService;
    @Autowired
    private MorizonService morizonService;
    @Autowired
    private OtodomService otodomService;

    @Autowired
    private OtodomSearchNewService otodomSearchNewService;
    @Autowired
    private MorizonSearchNewService morizonSearchNewService;

    @Autowired
    private FriscoService friscoService;

    @PostConstruct
    public void init() throws IOException {
        friscoService.findNew();

//        otodomSearchNewService.findNew();
//        morizonSearchNewService.findNew();
//
//        boolean changedVitalite = vitaliteService.checkVitalite();
//        boolean changedOutlet = vitaliteService.checkOutlet();
//        boolean changedMorizon = morizonService.check();
//        boolean changedOtodom = otodomService.check();
//
//        if (changedVitalite || changedOutlet || changedMorizon || changedOtodom) {
//            LOG.error("There were changes!");
//
//        } else {
//            LOG.warn("No changes at all.");
//        }
    }
}
