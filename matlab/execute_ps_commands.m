function execute_ps_commands(general,post)
    command_1 = "scp "+ general.paths.local_directory_path +"post.java "+ general.ssh.username +"@"+ general.ssh.hostname +":"+ general.paths.cluster_directory_path;
    command_1_1 = "scp "+ general.paths.local_batch_path +" "+ general.ssh.username +"@"+ general.ssh.hostname +":"+ general.paths.cluster_directory_path;
    command_4 = "module load starccmplus/18.02";
    command_5 = "starccm+ -batch post.java -power -np 6 "+general.paths.template+".sim";
    command_test = "pwd";

    system(sprintf('powershell -Command "%s; %s"', command_1, command_1_1));

    system(sprintf('ssh %s@%s "cd %s && %s && %s && %s"', general.ssh.username, general.ssh.hostname, general.paths.cluster_directory_path, command_test, command_4, command_5))

    case_names_column_index = find(strcmp('sim_files_folder', post.data.Properties.VariableNames));
    case_names = post.data{:, case_names_column_index};
    
    create_folders = cell(1, length(case_names));
    copy_monitors = cell(1, length(case_names));
    copy_reports = cell(1, length(case_names));
    copy_plots = cell(1, length(case_names));
    copy_pitcures = cell(1, length(case_names));
    

    for i = 1:length(case_names)
        create_folders{i} = "New-Item -Path '"+general.paths.local_directory_path+case_names(i)+"_Results' -ItemType Directory";
        copy_monitors{i} = "scp -r "+ general.ssh.username +"@"+ general.ssh.hostname +":"+ general.paths.cluster_directory_path + case_names(i) +"/Monitors"+" "+ general.paths.local_directory_path + case_names(i)+"_Results";
        copy_reports{i} = "scp -r "+ general.ssh.username +"@"+ general.ssh.hostname +":"+ general.paths.cluster_directory_path + case_names(i) +"/Reports"+" "+ general.paths.local_directory_path + case_names(i)+"_Results";
        copy_plots{i} = "scp -r "+ general.ssh.username +"@"+ general.ssh.hostname +":"+ general.paths.cluster_directory_path + case_names(i) +"/Plots"+" "+ general.paths.local_directory_path + case_names(i)+"_Results";
        copy_pitcures{i} = "scp -r "+ general.ssh.username +"@"+ general.ssh.hostname +":"+ general.paths.cluster_directory_path + case_names(i) +"/Pictures"+" "+ general.paths.local_directory_path + case_names(i)+"_Results";
    end

    for i = 1:length(case_names)
        system(sprintf('powershell -Command "%s; %s; %s; %s; %s"', create_folders{i}, copy_monitors{i}, copy_reports{i}, copy_plots{i}, copy_pitcures{i}))
    end

end

