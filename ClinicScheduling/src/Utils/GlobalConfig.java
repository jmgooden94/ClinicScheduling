package Utils;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;

public class GlobalConfig
{
    private static GlobalConfig instance = null;

    public static final int WEEK_LENGTH = 7;

    public static final int PROVIDER_WEEK_LENGTH = 5;

    private static final String CONFIG_FILENAME = "resources/config.properties";

    private double start_time;

    private double end_time;

    private static Properties props;

    public static GlobalConfig getInstance()
    {
        if (instance == null)
        {
            instance =  new GlobalConfig();
        }
        return instance;
    }

    private GlobalConfig()
    {
        try
        {
            props = new Properties();
            FileInputStream in = new FileInputStream(GlobalConfig.CONFIG_FILENAME);
            props.load(in);
            in.close();
            start_time = Double.parseDouble(props.getProperty("start_time"));
            end_time = Double.parseDouble(props.getProperty("close_time"));
        }
        catch (FileNotFoundException ex)
        {
            JOptionPane.showMessageDialog(new JFrame(),
                    "Cant open configuration file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(new JFrame(),
                    "Problem reading configuration file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

    }

    public double getStart_time()
    {
        return start_time;
    }

    public double getEnd_time()
    {
        return end_time;
    }

    public static String getUrl()
    {
        return props.getProperty("url");
    }

}
