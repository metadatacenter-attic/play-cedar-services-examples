import com.typesafe.config.ConfigFactory;
import play.*;

import java.io.File;

public class Global extends GlobalSettings {

    @Override
    public Configuration onLoadConfig(Configuration config, File path, ClassLoader classloader, Mode mode) {
        // System.out.println("Execution mode: " + mode.name());
        // Modifies the configuration according to the execution mode (DEV, TEST, PROD)
        if (mode.name().compareTo("TEST")==0)
            return new Configuration(ConfigFactory.load("application." + mode.name().toLowerCase() + ".conf"));
        else
            return onLoadConfig(config, path, classloader); // default implementation
    }

//    @Override
//    public void onStart(Application app) {
//        Logger.info("Application has started");
//    }
//
//    @Override
//    public void onStop(Application app) {
//        Logger.info("Application shutdown...");
//    }

}