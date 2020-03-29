package pl.mroczkarobert.vitalite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.mroczkarobert.vitalite.service.MorizonService;
import pl.mroczkarobert.vitalite.service.VitaliteService;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class Process {

    private static final Logger LOG = LoggerFactory.getLogger(Process.class);

    @Autowired
    private VitaliteService vitaliteService;
    @Autowired
    private MorizonService morizonService;

    @PostConstruct
    public void init() throws IOException {
        boolean changedVitalite = vitaliteService.checkVitalite();
        boolean changedOutlet = vitaliteService.checkOutlet();
        boolean changedMorizon = morizonService.checkMorizon();

        if (changedVitalite || changedOutlet || changedMorizon) {
            LOG.error("There were changes!");

        } else {
            LOG.warn("No changes at all.");
        }
    }
}
