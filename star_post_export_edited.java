package macro;

import java.io.*;
import java.util.*;
import javax.swing.*;

import star.common.*;
import star.base.query.*;
import star.base.neo.*;
import star.base.report.*;
import star.flow.*;
import star.vis.*;
import star.prismmesher.*;
import star.meshing.*;

public class star_post_export_edited extends StarMacro {

  public void execute() {
    // Access the active simulation
    Simulation simulation_0 = getActiveSimulation();

    // Retrieve the GlobalParameterManager
    GlobalParameterManager globalParamManager = simulation_0.getGlobalParameterManager();

    // Obtain all parameters
    Collection<Object> parameters = globalParamManager.getChildren();

    // Prepare the CSV file for writing
    File csvFile = new File("C:\\Users\\younj\\Downloads\\downRiver\\parameters.csv");

    try (PrintWriter writer = new PrintWriter(csvFile)) {
        // Write CSV header
        writer.println("Parameter Name, Quantity");

        for (Object obj : parameters) {
            // Print the details of each object
            simulation_0.println("Object: " + obj.toString());

            GlobalParameterBase parameter = getGlobalParameterByName(globalParamManager, obj.toString());

            if (parameter != null) {
                // Get the quantity associated with the parameter
                CompiledValueFunction quantity = parameter.getQuantity();

                // Write to CSV
                if (quantity != null) {
                    writer.println(parameter.getPresentationName() + ", " + quantity.toString());
                    simulation_0.println("Quantity: " + quantity.toString());
                } else {
                    writer.println(parameter.getPresentationName() + ", No quantity available");
                    simulation_0.println("No quantity available for this parameter.");
                }
            } else {
                writer.println(obj.toString() + ", Parameter not found");
                simulation_0.println("Parameter not found.");
            }
        }
        simulation_0.println("Data successfully exported to " + csvFile.getAbsolutePath());
    } catch (IOException e) {
        simulation_0.println("Error writing to CSV file: " + e.getMessage());
        e.printStackTrace();
    }
	
    try{
		// String batch_file = "/home/ga10025834/project/MQ9B_STOL/case_matrix.csv";
		String batch_file = "/home/ga10028979/project/stIIpod/sim/dev/stIIpod_batch.csv";
		BufferedReader br = new BufferedReader(new FileReader(batch_file));
		Batch sheet = new Batch();
		sheet.header = br.readLine().split(",");
		String line = "";
		while ((line = br.readLine()) != null){
			sheet.row = line.split(",");
			//mySim.println(sheet.col("postprocess"));
			if (Integer.parseInt(sheet.col("postprocess")) == 1){
				Simulation mySim = open_starccm_sim(sheet);
				post_process(sheet,mySim);
			}
		}
	}
	catch (IOException e){e.printStackTrace();}   
  }

  public class Batch{
	  String[] header;
	  String[] row;
 	  private String col(String filter){
		List<String> string_list = Arrays.asList(header);
 		return row[string_list.indexOf(filter)];
    }
   }

private Simulation open_starccm_sim(Batch sheet) {
	String case_name = sheet.col("sim.files.folder");
	String case_path = sheet.col("sim.files.setup_path");
	
	File sim_path = new File(resolvePath(case_path+"/"+case_name+"/"));
	String[] sim_files = sim_path.list(new FilenameFilter() {
    public boolean accept(File sim_path, String fileName) {
        return fileName.endsWith(".sim") && fileName.contains("@");
    }});
	
	Simulation mySim = getActiveSimulation();
	mySim.println(sim_files[0]);
	mySim.println(sim_files.length);
	// int iter_num = 0;
	// for (int i = 0; i < sim_files.length; i++) {
	// 	int sim_iter_num = Integer.parseInt(sim_files[i].substring(sim_files[i].indexOf("@"+1),sim_files[i].indexOf(".sim")));
	// 	if(iter_num < sim_iter_num) {
	// 		iter_num = sim_iter_num;
	// 	}
	// }
	// String sim_file = case_name+"@"+iter_num+".sim";
	Simulation simulation_0 = new Simulation(resolvePath(case_path+"/"+case_name+"/"+sim_files[0]));
	return simulation_0;
}

// Example method to obtain a GlobalParameterBase by name (placeholder)
private GlobalParameterBase getGlobalParameterByName(GlobalParameterManager manager, String name) {
    // This method should implement logic to find and return the GlobalParameterBase based on the name
    // Placeholder implementation
    for (Object obj : manager.getChildren()) {
        if (obj instanceof GlobalParameterBase) {
            GlobalParameterBase param = (GlobalParameterBase) obj;
            if (param.getPresentationName().equals(name)) {
                return param;
            }
        }
    }
    return null;
}

private void post_process(Batch sheet, Simulation simulation_0) {

	String case_name = sheet.col("sim.files.folder");
	String case_path = sheet.col("sim.files.setup_path");

	//Create folder structures
    File path_name = new File(case_path);
	if (! path_name.exists()) {
		path_name.mkdir();
	}
    File folder_name = new File(resolvePath(case_path+"/"+case_name));
    folder_name.mkdir();
    File reports_name = new File(resolvePath(case_path+"/"+case_name+"/Reports"));
    reports_name.mkdir();
    File pictures_name = new File(resolvePath(case_path+"/"+case_name+"/Pictures"));
    pictures_name.mkdir();
    File plots_name = new File(resolvePath(case_path+"/"+case_name+"/Plots"));
    plots_name.mkdir();
	File monitors_name = new File(resolvePath(case_path+"/"+case_name+"/Monitors"));
    monitors_name.mkdir();

	//Export Monitors
	//Export Plots
	Collection<Monitor> monitors = simulation_0.getMonitorManager().getMonitors();
	for(Monitor mon_iter : monitors) {
		String mon_name = mon_iter.getPresentationName().replace(" ","_");
		mon_iter.export(resolvePath(case_path+"/"+case_name+"/Monitors/"+mon_name+".csv"));
	}

	//Export Plots
	Collection<StarPlot> plots = simulation_0.getPlotManager().getPlots();
	for(StarPlot plot_iter : plots) {
		String plot_name = plot_iter.getPresentationName().replace(" ","_");
		plot_iter.encode(resolvePath(case_path+"/"+case_name+"/Plots/"+plot_name+".png"), "png", 1920, 1080, true, false);
		plot_iter.export(resolvePath(case_path+"/"+case_name+"/Plots/"+plot_name+".csv"), ",");
	}

	//Export Scenes
	Collection<Scene> scenes = simulation_0.getSceneManager().getScenes();
	for(Scene scene_iter : scenes) {
		String scene_name = scene_iter.getPresentationName().replace(" ","_");
		scene_iter.printAndWait(resolvePath(case_path+"/"+case_name+"/Pictures/"+scene_name+".png"), 1, 1920, 1080, true, false);
	}

	//Export Reports
	Collection<Report> reports = simulation_0.getReportManager().getObjects();
	ArrayList<Report> reports_list = new ArrayList<>(reports);
	//Export Reports to Individual Files
	try{
		for(int i = 0; i < reports_list.size(); i++) {
			String report_name = reports_list.get(i).getPresentationName().replace(" ","_");
			reports_list.get(i).printReport(resolvePath(case_path+"/"+case_name+"/Reports/"+report_name+".txt"), false);
		}
	} catch(Exception e){}

	//Export All Reports to Single File (csv) 
	try{
		File reports_file = new File(resolvePath(case_path+"/"+case_name+"/Reports/reports.csv"));
		PrintWriter pw = new PrintWriter(reports_file);
		//Header
		StringBuilder line_header = new StringBuilder();
		for(int i = 0; i < reports_list.size(); i++) {
	    	line_header.append(reports_list.get(i).getPresentationName().replace(" ","_"));
	    	if (i != reports_list.size() - 1) {
	        	line_header.append(',');
	    	}
		}
		line_header.append("\n");
		pw.write(line_header.toString());
		//Data
		StringBuilder line_data = new StringBuilder();
		for (int i = 0; i < reports_list.size(); i++) {
	    	line_data.append(reports_list.get(i).monitoredValue());
	    	if (i != reports_list.size() - 1) {
				line_data.append(',');
	    	}
		}
		line_data.append("\n");
		pw.write(line_data.toString());
		pw.close();
	} catch(Exception e){}

	simulation_0.kill();
}
}