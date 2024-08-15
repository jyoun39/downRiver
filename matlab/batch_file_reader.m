function post = batch_file_reader(general)
    post.data = readtable(general.paths.local_batch_path, 'Delimiter', ',');
    
    case_paths = 'sim_files_setup_path';
    case_names = 'sim_files_folder'; 
    
    case_paths_column_index = find(strcmp(case_paths, post.data.Properties.VariableNames));
    case_names_column_index = find(strcmp(case_names, post.data.Properties.VariableNames));
    
    case_paths = post.data{:, case_paths_column_index};
    case_names = post.data{:, case_names_column_index};
    
    case_full_paths = strcat(case_paths,"/",case_names);
    
end
    