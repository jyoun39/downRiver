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
public class post extends StarMacro {
  public void execute() {
    try {
      String batch_file = "/storage/coda1/p-sm53/0/jyoun39/project/batch.csv";
      BufferedReader br = new BufferedReader(new FileReader(batch_file));
      Batch sheet = new Batch();
      sheet.header = br.readLine().split(",");
      String line = "";
      while ((line = br.readLine()) != null) {
        sheet.row = line.split(",");
        if (Integer.parseInt(sheet.col("postprocess")) == 1) {
          Simulation mySim = open_starccm_sim(sheet);
          post_process(sheet, mySim);
        }
      }
    } catch (IOException e) { e.printStackTrace(); }
  }
  public class Batch {
    String[] header;
    String[] row;
    private String col(String filter) {
      List<String> string_list = Arrays.asList(header);
      return row[string_list.indexOf(filter)];
    }
  }
  private Simulation open_starccm_sim(Batch sheet) {
    String case_name = sheet.col("sim.files.folder");
    String case_path = sheet.col("sim.files.setup_path");
    File sim_path = new File(resolvePath(case_path + "/" + case_name + "/"));
    String[] sim_files = sim_path.list(new FilenameFilter() {
      public boolean accept(File sim_path, String fileName) {
        return fileName.endsWith(".sim") && fileName.contains("@");
      }
    });
    Simulation mySim = getActiveSimulation();
    mySim.println(sim_files[0]);
    mySim.println(sim_files.length);
    Simulation simulation_0 = new Simulation(resolvePath(case_path + "/" + case_name + "/" + sim_files[0]));
    return simulation_0;
  }
  private void post_process(Batch sheet, Simulation simulation_0) {
    String case_name = sheet.col("sim.files.folder");
    String case_path = sheet.col("sim.files.setup_path");
    File path_name = new File(case_path);
    if (!path_name.exists()) {
      path_name.mkdir();
    }
    File folder_name = new File(resolvePath(case_path + "/" + case_name));
    folder_name.mkdir();
    File reports_name = new File(resolvePath(case_path + "/" + case_name + "/Reports"));
    reports_name.mkdir();
    File pictures_name = new File(resolvePath(case_path + "/" + case_name + "/Pictures"));
    pictures_name.mkdir();
    File plots_name = new File(resolvePath(case_path + "/" + case_name + "/Plots"));
    plots_name.mkdir();
    File monitors_name = new File(resolvePath(case_path + "/" + case_name + "/Monitors"));
    monitors_name.mkdir();
    Collection<Monitor> monitors = simulation_0.getMonitorManager().getMonitors();
    for (Monitor mon_iter : monitors) {
      String mon_name = mon_iter.getPresentationName().replace(" ", "_");
      mon_iter.export(resolvePath(case_path + "/" + case_name + "/Monitors/" + mon_name + ".csv"));
    }
    Collection<StarPlot> plots = simulation_0.getPlotManager().getPlots();
    for (StarPlot plot_iter : plots) {
      String plot_name = plot_iter.getPresentationName().replace(" ", "_");
      plot_iter.encode(resolvePath(case_path + "/" + case_name + "/Plots/" + plot_name + ".png"), "png", 1920, 1080, true, false);
      plot_iter.export(resolvePath(case_path + "/" + case_name + "/Plots/" + plot_name + ".csv"), ",");
    }
    Collection<Scene> scenes = simulation_0.getSceneManager().getScenes();
    for (Scene scene_iter : scenes) {
      String scene_name = scene_iter.getPresentationName().replace(" ", "_");
      scene_iter.printAndWait(resolvePath(case_path + "/" + case_name + "/Pictures/" + scene_name + ".png"), 1, 1920, 1080, true, false);
    }
    Collection<Report> reports = simulation_0.getReportManager().getObjects();
    ArrayList<Report> reports_list = new ArrayList<>(reports);
    try {
      for (int i = 0; i < reports_list.size(); i++) {
        String report_name = reports_list.get(i).getPresentationName().replace(" ", "_");
        reports_list.get(i).printReport(resolvePath(case_path + "/" + case_name + "/Reports/" + report_name + ".txt"), false);
      }
    } catch (Exception e) {}
    try {
      File reports_file = new File(resolvePath(case_path + "/" + case_name + "/Reports/reports.csv"));
      PrintWriter pw = new PrintWriter(reports_file);
      StringBuilder line_header = new StringBuilder();
      for (int i = 0; i < reports_list.size(); i++) {
        line_header.append(reports_list.get(i).getPresentationName().replace(" ", "_"));
        if (i != reports_list.size() - 1) {
          line_header.append(",");
        }
      }
      line_header.append("\n");
      pw.write(line_header.toString());
      StringBuilder line_data = new StringBuilder();
      for (int i = 0; i < reports_list.size(); i++) {
        line_data.append(reports_list.get(i).monitoredValue());
        if (i != reports_list.size() - 1) {
          line_data.append(",");
        }
      }
      line_data.append("\n");
      pw.write(line_data.toString());
      pw.close();
    } catch (Exception e) {}
    simulation_0.kill();
  }
}
